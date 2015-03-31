package bo.pic.server.mapper;

import bo.pic.server.mapper.cglib.AnnotationMapperProvider;
import bo.pic.server.mapper.cglib.CglibMapperProvider;
import bo.pic.server.mapper.cglib.InheritanceMapperProvider;
import bo.pic.server.mapper.collections.CollectionsMapperProvider;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.List;

public class MapperFacade {
    private final MappingContext rootContext;

    public MapperFacade() {
        List<ObjectMapperProvider> providers = Lists.newArrayList(new BasicMapperProvider(),
                new CollectionsMapperProvider(),
                new AnnotationMapperProvider(),
                new InheritanceMapperProvider(),
                new EnumMapperProvider());

        Java8OptionalMapperProvider java8OptionalMapperProvider = Java8OptionalMapperProvider.tryCreate();
        if (java8OptionalMapperProvider != null) {
            providers.add(java8OptionalMapperProvider);
        }

        providers.add(new CglibMapperProvider());
        ListMapperProvider listProvider = new ListMapperProvider(providers);
        CachingMapperProvider cachingProvider = new CachingMapperProvider(listProvider);
        rootContext = new MappingContext(cachingProvider);
    }

    @SuppressWarnings("unchecked")
    public <T> void write(@Nonnull TreeWriter writer, @Nonnull Class<T> clazz, @Nonnull T object) {
        Mapper<T> mapper = getRootContext().mapperFor(clazz);
        mapper.write(writer, object);
    }

    @SuppressWarnings("unchecked")
    public <T> T read(@Nonnull TreeReader reader, @Nonnull Class<T> clazz) {
        Mapper<T> mapper = getRootContext().mapperFor(clazz);
        return mapper.read(reader);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> Mapper<T> mapperFor(@Nonnull Class<T> clazz) {
        return mapperFor((Type) clazz);
    }

    @Nonnull
    public Mapper mapperFor(@Nonnull Type type) {
        return getRootContext().mapperFor(type);
    }

    protected MappingContext getRootContext() {
        return rootContext;
    }
}
