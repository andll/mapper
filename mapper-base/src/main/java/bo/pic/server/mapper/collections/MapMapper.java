package bo.pic.server.mapper.collections;

import bo.pic.server.mapper.*;
import bo.pic.server.mapper.tokens.BeginArrayToken;
import bo.pic.server.mapper.tokens.EndArrayToken;
import bo.pic.server.mapper.tokens.Token;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;

public abstract class MapMapper<T extends Map> implements Mapper<T> {
    private final Mapper keyMapper;
    private final Mapper valueMapper;

    public MapMapper(@Nonnull MappingContext context, @Nonnull ParameterizedType type) {
        valueMapper = context.mapperFor(type.getActualTypeArguments()[1]);
        keyMapper = context.mapperFor(type.getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(@Nonnull TreeWriter writer, @Nullable Map object) {
        if (object == null) {
            throw new IllegalArgumentException("Collection can not be null");
        }
        writer.append(new BeginArrayToken());
        for (Map.Entry v : (Set<Map.Entry>) object.entrySet()) {
            if (v.getKey() == null) {
                throw new IllegalArgumentException("Map key can not be null");
            }
            if (v.getValue() == null) {
                throw new IllegalArgumentException("Map value can not be null");
            }
            keyMapper.write(writer, v.getKey());
            valueMapper.write(writer, v.getValue());
        }
        writer.append(new EndArrayToken());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public T read(@Nonnull TreeReader reader) {
        T t = createMap();
        Token token = reader.nextToken();
        if (!(token instanceof BeginArrayToken)) {
            throw new UnexpectedTokenException(token);
        }
        while (reader.peekTokenClass() != EndArrayToken.class) {
            Object key = keyMapper.read(reader);
            Object value = valueMapper.read(reader);
            if (t.put(key, value) != null) {
                throw new IllegalStateException("Duplicating key found: " + key);
            }
        }
        reader.nextToken();
        return makeUnmodifiable(t);
    }

    protected abstract T makeUnmodifiable(T t);

    protected abstract T createMap();
}
