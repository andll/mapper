package bo.pic.server.mapper.collections;

import bo.pic.server.mapper.*;
import bo.pic.server.mapper.tokens.BeginArrayToken;
import bo.pic.server.mapper.tokens.EndArrayToken;
import bo.pic.server.mapper.tokens.Token;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

@SuppressWarnings("unchecked")
abstract class CollectionMapper<T extends Collection> implements Mapper<T> {
    private final Mapper mapper;

    public CollectionMapper(@Nonnull MappingContext context, @Nonnull ParameterizedType type) {
        mapper = context.mapperFor(type.getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(@Nonnull TreeWriter writer, @Nullable T object) {
        if (object == null) {
            throw new IllegalArgumentException("Collection can not be null");
        }
        writer.append(new BeginArrayToken());
        for (Object v : object) {
            if (v == null) {
                throw new IllegalArgumentException("Collection element can not be null");
            }
            mapper.write(writer, v);
        }
        writer.append(new EndArrayToken());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public T read(@Nonnull TreeReader reader) {
        T t = createNew();
        Token token = reader.nextToken();
        if (!(token instanceof BeginArrayToken)) {
            throw new UnexpectedTokenException(token);
        }
        while (reader.peekTokenClass() != EndArrayToken.class) {
            t.add(mapper.read(reader));
        }
        reader.nextToken();
        return makeUnmodifiable(t);
    }

    protected abstract T createNew();

    protected abstract T makeUnmodifiable(T t);
}
