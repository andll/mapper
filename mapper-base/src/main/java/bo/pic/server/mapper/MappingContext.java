package bo.pic.server.mapper;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.UUID;

public class MappingContext {
    private final UUID id = UUID.randomUUID();
    private final ObjectMapperProvider root;

    public MappingContext(ObjectMapperProvider root) {
        this.root = root;
    }

    @Nonnull
    public Mapper mapperFor(@Nonnull Type type) throws MapperNotFoundException {
        Mapper mapper = root.mapperFor(type, this);
        if (mapper == null) {
            throw new MapperNotFoundException(type);
        }
        return mapper;
    }

    public ObjectMapperProvider getRoot() {
        return root;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MappingContext && ((MappingContext) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean containsMarker(@Nonnull Class clazz, @Nonnull Class<?> marker) {
        return marker.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotation) {
        return (T) clazz.getAnnotation(annotation);
    }
}
