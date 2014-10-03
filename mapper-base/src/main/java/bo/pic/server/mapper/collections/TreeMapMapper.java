package bo.pic.server.mapper.collections;

import bo.pic.server.mapper.MappingContext;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@SuppressWarnings("unchecked")
class TreeMapMapper extends MapMapper<SortedMap> {

    TreeMapMapper(@Nonnull MappingContext context, @Nonnull ParameterizedType type) {
        super(context, type);
    }

    @Override
    protected SortedMap createMap() {return new TreeMap();}

    @Override
    protected SortedMap makeUnmodifiable(SortedMap t) {return Collections.unmodifiableSortedMap(t);}
}
