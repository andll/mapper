package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.AbstractTokenVisitor;
import bo.pic.server.mapper.tokens.NullObjectToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConstantMapper<T> implements Mapper<T> {
    private final T value;

    public ConstantMapper(@Nonnull T value) {this.value = value;}

    @Override
    public void write(@Nonnull TreeWriter writer, @Nullable T object) {
        if (!value.equals(object)) {
            throw new IllegalArgumentException("Can not serialize: " + object + " expected value was " + value);
        }
        writer.append(NullObjectToken.INSTANCE);
    }

    @Nullable
    @Override
    public T read(@Nonnull TreeReader reader) {
        reader.nextToken().accept(new AbstractTokenVisitor() {
            @Override
            public void dispatch(NullObjectToken token) {
            }
        });
        return value;
    }
}
