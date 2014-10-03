package bo.pic.server.mapper.collections;

import bo.pic.server.mapper.Mapper;
import bo.pic.server.mapper.ObjectMapperProvider;
import bo.pic.server.mapper.MappingContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class CollectionsMapperProvider implements ObjectMapperProvider {
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Mapper mapperFor(@Nonnull Type clazz, @Nonnull MappingContext context) {
        if (clazz instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) clazz;
            if (type.getRawType() == Set.class) {
                return new SetMapper(context, type);
            }
            if (type.getRawType() == List.class) {
                return new ListMapper(context, type);
            }
            if (type.getRawType() == Map.class) {
                return new HashMapMapper(context, type);
            }
            if (type.getRawType() == SortedMap.class) {
                return new TreeMapMapper(context, type);
            }
        }
        return null;
    }
}
