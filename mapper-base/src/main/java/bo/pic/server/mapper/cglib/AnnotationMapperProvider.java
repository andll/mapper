package bo.pic.server.mapper.cglib;

import bo.pic.server.mapper.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AnnotationMapperProvider implements ObjectMapperProvider {
    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type type, @Nonnull MappingContext context) {
        Class clazz = tryExtractClass(type);
        while (clazz != null) {
            WithMapper mapperAnnotation = (WithMapper) clazz.getAnnotation(WithMapper.class);
            if (mapperAnnotation != null) {
                return newInstance(mapperAnnotation.value(), context);
            }
            WithMapperProvider mapperProviderAnnotation = (WithMapperProvider) clazz.getAnnotation(WithMapperProvider.class);
            if (mapperProviderAnnotation != null) {
                return newInstance(mapperProviderAnnotation.value(), context).mapperFor(type, context);
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    @Nullable
    private static Class tryExtractClass(@Nonnull Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                return (Class) rawType;
            }
        }
        return null;
    }

    /**
     * Creates instance, first trying ctor(ObjectMapperProvider), then ctor()
     */
    private <T> T newInstance(@Nonnull Class<T> clazz, @Nonnull MappingContext context) {
        try {
            try {
                return clazz.getConstructor(MappingContext.class).newInstance(context);
            } catch (NoSuchMethodException e) {
                //Proceed to no-args ctor
            }
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
