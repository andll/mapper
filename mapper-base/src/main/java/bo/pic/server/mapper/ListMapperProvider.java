package bo.pic.server.mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ListMapperProvider implements ObjectMapperProvider {
    private final List<ObjectMapperProvider> providers;

    public ListMapperProvider(ObjectMapperProvider... providers) {
        this.providers = Arrays.asList(providers);
    }

    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type clazz, @Nonnull MappingContext context) {
        for (ObjectMapperProvider provider : providers) {
            Mapper mapper = provider.mapperFor(clazz, context);
            if (mapper != null) {
                return mapper;
            }
        }
        return null;
    }
}
