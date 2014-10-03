package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.AbstractTokenVisitor;
import bo.pic.server.mapper.tokens.NullObjectToken;
import bo.pic.server.mapper.tokens.StringToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

public class EnumMapperProvider implements ObjectMapperProvider {
    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type type, @Nonnull MappingContext context) {
        if (!(type instanceof Class)) {
            return null;
        }
        final Class clazz = (Class) type;
        if (!clazz.isEnum()) {
            return null;
        }
        return new Mapper<Enum>() {
            @Override
            public void write(@Nonnull TreeWriter writer, @Nullable Enum object) {
                if (object == null) {
                    writer.append(NullObjectToken.INSTANCE);
                } else {
                    writer.append(new StringToken(object.name()));
                }
            }

            @Nullable
            @Override
            public Enum read(@Nonnull TreeReader reader) {
                final AtomicReference<Enum> ret = new AtomicReference<>();
                reader.nextToken().accept(new AbstractTokenVisitor() {
                    @Override
                    public void dispatch(NullObjectToken token) {
                        ret.set(null);
                    }

                    @Override
                    public void dispatch(StringToken token) {
                        ret.set(Enum.valueOf(clazz, token.getValue()));
                    }
                });
                return ret.get();
            }
        };
    }
}
