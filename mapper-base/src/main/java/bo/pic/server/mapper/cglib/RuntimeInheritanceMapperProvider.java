package bo.pic.server.mapper.cglib;

import bo.pic.server.mapper.*;
import bo.pic.server.mapper.tokens.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Disabled by default, since uses Class.forName. This class can only be used with trusted source
 */
public class RuntimeInheritanceMapperProvider implements ObjectMapperProvider {
    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type type, @Nonnull final MappingContext context) {
        if (!(type instanceof Class)) {
            return null;
        }
        final Class clazz = (Class) type;
        if (context.getAnnotation(clazz, RuntimeInheritance.class) == null) {
            return null;
        }

        return new RuntimeInheritanceMapper(context, clazz);
    }

    @SuppressWarnings("unchecked")
    public static class RuntimeInheritanceMapper implements Mapper {
        private final MappingContext context;
        private final Class          clazz;

        public RuntimeInheritanceMapper(MappingContext context, Class clazz) {
            this.context = context;
            this.clazz = clazz;
        }

        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Object object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
                return;
            }
            String implName = object.getClass().getName();
            Mapper actualMapper = context.mapperFor(object.getClass());
            writer.append(new BeginArrayToken());
            writer.append(new StringToken(implName));
            actualMapper.write(writer, object);
            writer.append(new EndArrayToken());
        }

        @Nullable
        @Override
        public Object read(@Nonnull final TreeReader reader) {
            final AtomicReference<Object> result = new AtomicReference<>();
            reader.nextToken().accept(new AbstractTokenVisitor() {
                @Override
                public void dispatch(NullObjectToken token) {
                    result.set(null);
                }

                @Override
                public void dispatch(BeginArrayToken token) {
                    reader.nextToken().accept(new AbstractTokenVisitor() {
                        @Override
                        public void dispatch(StringToken token) {
                            Class<?> implClazz = classForName(token.getValue());
                            if (!clazz.isAssignableFrom(implClazz)) {
                                throw new IllegalStateException(
                                    String.format("Requested implementation class %s is not subclass of %s, consider broken stream",
                                                  implClazz,
                                                  clazz));
                            }
                            Mapper mapper = context.mapperFor(implClazz);
                            result.set(mapper.read(reader));
                            reader.nextToken().accept(new AbstractTokenVisitor() {
                                @Override
                                public void dispatch(EndArrayToken token) {
                                }
                            });
                        }
                    });
                }
            });
            return result.get();
        }

        private static Class<?> classForName(String implName) {
            try {
                return Class.forName(implName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
