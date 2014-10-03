package bo.pic.server.mapper.cglib;

import com.google.common.collect.BiMap;
import net.sf.cglib.reflect.FastConstructor;

import javax.annotation.Nonnull;

public class ConstructorBinding {
    private final FastConstructor        constructor;
    private final BiMap<String, Integer> fieldToArgIndex;

    public ConstructorBinding(@Nonnull BiMap<String, Integer> fieldToArgIndex, @Nonnull FastConstructor constructor) {
        this.constructor = constructor;
        this.fieldToArgIndex = fieldToArgIndex;
    }

    @Nonnull
    public BiMap<String, Integer> getFieldToArgIndex() {
        return fieldToArgIndex;
    }

    @Nonnull
    public FastConstructor getConstructor() {
        return constructor;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConstructorBinding{");
        sb.append("matchedConstructor=").append(constructor.getJavaConstructor());
        sb.append(", fieldToArgIndex=").append(fieldToArgIndex);
        sb.append('}');
        return sb.toString();
    }
}
