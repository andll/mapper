package bo.pic.server.mapper.cglib;

import bo.pic.server.mapper.*;
import bo.pic.server.mapper.tokens.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class InheritanceMapperProvider implements ObjectMapperProvider {
    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type type, @Nonnull MappingContext context) {
        if (!(type instanceof Class)) {
            return null;
        }
        Class clazz = (Class) type;
        Implementations implementations = context.getAnnotation(clazz, Implementations.class);
        if (implementations == null) {
            return null;
        }
        return createInheritanceMapper(clazz, context, implementations.value());
    }

    @SuppressWarnings("unchecked")
    private Mapper createInheritanceMapper(Class clazz, @Nonnull MappingContext context, Class... implementations) {
        Map<String, Mapper<Object>> mapping = new HashMap<>(implementations.length);
        for (Class impl : implementations) {
            mapping.put(impl.getName(), context.mapperFor(impl));
        }
        return new InheritanceMapper(clazz.toString(), mapping);
    }

    private static class InheritanceMapper implements Mapper {
        private final String superTypeName;
        private final Map<String, Mapper<Object>> mapping;

        public InheritanceMapper(String superTypeName, Map<String, Mapper<Object>> mapping) {
            this.superTypeName = superTypeName;
            this.mapping = mapping;
        }

        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Object object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
                return;
            }
            String implName = object.getClass().getName();
            Mapper<Object> actualMapper = mapping.get(implName);
            if (actualMapper == null) {
                throw new IllegalArgumentException("Can not find mapper for " + implName + ", did you include" +
                        " annotation @Implementations to " + superTypeName + "?");
            }
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
                            String mapperName = token.getValue();
                            Mapper<Object> mapper = mapping.get(mapperName);
                            if (mapper == null) {
                                throw new IllegalArgumentException("Can not find mapper for " + mapperName + ", possible you serialized" +
                                        " object with incompatible version of application");
                            }
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

    }
}
