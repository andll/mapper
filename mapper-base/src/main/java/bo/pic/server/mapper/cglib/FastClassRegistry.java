package bo.pic.server.mapper.cglib;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.sf.cglib.reflect.FastClass;

public class FastClassRegistry {
    private final static LoadingCache<Class, FastClass> CACHE = CacheBuilder.newBuilder()
            .build(new CacheLoader<Class, FastClass>() {
                @Override
                public FastClass load(Class key) throws Exception {
                    return FastClass.create(key);
                }
            });

    public static FastClass asFastClass(Class clazz) {
        return CACHE.getUnchecked(clazz);
    }
}
