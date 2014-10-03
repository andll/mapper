package bo.pic.server.mapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.javatuples.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class CachingMapperProvider implements ObjectMapperProvider {
    private static final Mapper NULL_MAPPER = new Mapper() {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Object object) {
            throw new UnsupportedOperationException("write not implemented");
        }

        @Nullable
        @Override
        public Object read(@Nonnull TreeReader reader) {
            throw new UnsupportedOperationException("read not implemented");
        }
    };
    private final ObjectMapperProvider base;
    private final LoadingCache<Pair<Type, MappingContext>, Mapper> cache = CacheBuilder.<Type, Mapper>newBuilder().build(new CacheLoader());

    public CachingMapperProvider(ObjectMapperProvider base) {
        this.base = base;
    }

    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type clazz, @Nonnull MappingContext context) {
        Mapper mapper = cache.getUnchecked(new Pair<>(clazz, context));
        if (mapper == NULL_MAPPER) {
            return null;
        }
        return mapper;
    }

    private class CacheLoader extends com.google.common.cache.CacheLoader<Pair<Type, MappingContext>, Mapper> {
        @Override
        @Nonnull
        public Mapper load(@Nonnull Pair<Type, MappingContext> key) throws Exception {
            Mapper mapper = base.mapperFor(key.getValue0(), key.getValue1());
            if (mapper == null) {
                return NULL_MAPPER;
            }
            return mapper;
        }
    }
}
