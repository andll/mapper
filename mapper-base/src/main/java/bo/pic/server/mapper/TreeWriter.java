package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.Token;

import javax.annotation.Nonnull;

public interface TreeWriter {
    @Nonnull
    TreeWriter append(@Nonnull Token token);
}
