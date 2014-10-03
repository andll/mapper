package bo.pic.server.mapper.collections;

import bo.pic.server.mapper.MappingContext;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
class HashMapMapper extends MapMapper<Map> {

    HashMapMapper(@Nonnull MappingContext context, @Nonnull ParameterizedType type) {
        super(context, type);
    }

    @Override
    protected Map createMap() {return new HashMap();}

    @Override
    protected Map makeUnmodifiable(Map t) {return Collections.unmodifiableMap(t);}
}
