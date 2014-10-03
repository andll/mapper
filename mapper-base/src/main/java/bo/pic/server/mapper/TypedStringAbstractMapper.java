package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.AbstractTokenVisitor;
import bo.pic.server.mapper.tokens.NullObjectToken;
import bo.pic.server.mapper.tokens.StringToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TypedStringAbstractMapper<T> implements Mapper<T> {
    @Override
    public void write(@Nonnull TreeWriter writer, @Nullable T object) {
        if (object == null) {
            writer.append(NullObjectToken.INSTANCE);
        } else {
            writer.append(new StringToken(toString(object)));
        }
    }

    @Nullable
    @Override
    public T read(@Nonnull TreeReader reader) {
        final AtomicReference<T> ret = new AtomicReference<>();
        reader.nextToken().accept(new AbstractTokenVisitor() {
            @Override
            public void dispatch(NullObjectToken token) {
                ret.set(null);
            }

            @Override
            public void dispatch(StringToken token) {
                ret.set(newValue(token.getValue()));
            }
        });
        return ret.get();
    }

    protected abstract T newValue(String value);

    protected String toString(T value) {
        return value.toString();
    }
}
