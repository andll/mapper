package bo.pic.server.mapper;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Mapper<T> {
    void write(@Nonnull TreeWriter writer, @Nullable T object);

    @Nullable
    T read(@Nonnull TreeReader reader);
}
