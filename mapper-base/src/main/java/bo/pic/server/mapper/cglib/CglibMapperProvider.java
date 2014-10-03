package bo.pic.server.mapper.cglib;

import bo.pic.server.mapper.Mapper;
import bo.pic.server.mapper.MappingContext;
import bo.pic.server.mapper.ObjectMapperProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class CglibMapperProvider implements ObjectMapperProvider {
    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type type, @Nonnull MappingContext context) {
        if (!(type instanceof Class)) {
            return null;
        }
        Class clazz = (Class) type;
        if (clazz.isInterface() ||
                clazz.isPrimitive() ||
                (clazz.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
            return null;
        }
        if (!context.containsMarker(clazz, AutoSerializable.class)) {
            return null;
        }
        return new CglibMapper(clazz, context);
    }
}
