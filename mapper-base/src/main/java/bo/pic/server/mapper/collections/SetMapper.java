package bo.pic.server.mapper.collections;

import bo.pic.server.mapper.MappingContext;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class SetMapper extends CollectionMapper<Set> {
    SetMapper(@Nonnull MappingContext context, @Nonnull ParameterizedType type) {
        super(context, type);
    }

    @Override
    protected Set createNew() {
        return new HashSet();
    }

    @Override
    protected Set makeUnmodifiable(Set set) {
        return Collections.unmodifiableSet(set);
    }
}
