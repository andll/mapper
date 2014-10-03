package bo.pic.server.mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

public interface ObjectMapperProvider {
    @Nullable
    Mapper mapperFor(@Nonnull Type type, @Nonnull MappingContext context);
}
