package bo.pic.server.mapper.collections;

import bo.pic.server.mapper.MappingContext;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ListMapper extends CollectionMapper<List> {
    ListMapper(@Nonnull MappingContext context, @Nonnull ParameterizedType type) {
        super(context, type);
    }

    @Override
    protected List createNew() {
        return new ArrayList<>();
    }

    @Override
    protected List makeUnmodifiable(List set) {
        return Collections.unmodifiableList(set);
    }
}
