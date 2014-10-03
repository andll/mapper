package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.Token;

import javax.annotation.Nonnull;

public interface TreeReader {
    @Nonnull
    Token nextToken();

    @Nonnull
    Class<? extends Token> peekTokenClass();
}
