package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.NullObjectToken;
import bo.pic.server.mapper.tokens.Token;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Java8OptionalMapperProvider implements ObjectMapperProvider {
    private static final String JAVA8_OPTIONAL_CLASS_NAME = "java.util.Optional";

    @Nullable
    public static Java8OptionalMapperProvider tryCreate() {
        Class optionalClass;
        try {
            optionalClass = Class.forName(JAVA8_OPTIONAL_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            return null;
        }

        FastClass fastClass = FastClass.create(optionalClass);

        FastMethod methodOf = fastClass.getMethod("of", new Class[]{Object.class});
        FastMethod methodEmpty = fastClass.getMethod("empty", new Class[0]);
        FastMethod methodIsPresent = fastClass.getMethod("isPresent", new Class[0]);
        FastMethod methodGet = fastClass.getMethod("get", new Class[0]);

        return new Java8OptionalMapperProvider(optionalClass, methodOf, methodEmpty, methodIsPresent, methodGet);
    }

    private final Class optionalClass;
    private final FastMethod methodOf;
    private final FastMethod methodEmpty;
    private final FastMethod methodIsPresent;
    private final FastMethod methodGet;

    private Java8OptionalMapperProvider(Class optionalClass, FastMethod methodOf, FastMethod methodEmpty, FastMethod methodIsPresent, FastMethod methodGet) {
        this.optionalClass = optionalClass;
        this.methodOf = methodOf;
        this.methodEmpty = methodEmpty;
        this.methodIsPresent = methodIsPresent;
        this.methodGet = methodGet;
    }

    public boolean isOptionalClass(Class clazz) {
        return clazz == optionalClass;
    }

    public Object getDefaultValue() {
        try {
            return methodEmpty.invoke(null, null);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type t, @Nonnull MappingContext context) {
        if (t instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) t;
            if (type.getRawType() == optionalClass) {
                return new Java8OptionalMapper(context.mapperFor(type.getActualTypeArguments()[0]));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private class Java8OptionalMapper implements Mapper {
        private final Mapper mapper;

        public Java8OptionalMapper(Mapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Object object) {
            try {
                boolean isPresent = (boolean) methodIsPresent.invoke(object, null);
                if (object == null || !isPresent) {
                    writer.append(NullObjectToken.INSTANCE);
                } else {
                    Object value = methodGet.invoke(object, null);
                    mapper.write(writer, value);
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Nullable
        @Override
        public Object read(@Nonnull TreeReader reader) {
            Class<? extends Token> peek = reader.peekTokenClass();
            try {
                if (peek == NullObjectToken.class) {
                    reader.nextToken();
                    return methodEmpty.invoke(null, null);
                }
                Object value = mapper.read(reader);

                return methodOf.invoke(null, new Object[]{value});
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
