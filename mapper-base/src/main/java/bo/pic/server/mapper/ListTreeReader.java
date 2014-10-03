package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.Token;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ListTreeReader implements TreeReader {
    private final List<Token> tokens;
    private int i = 0;

    public ListTreeReader(Token... tokens) {
        this.tokens = Arrays.asList(tokens);
    }

    @Nonnull
    @Override
    public Token nextToken() {
        return tokens.get(i++);
    }

    @Nonnull
    @Override
    public Class<? extends Token> peekTokenClass() {
        return tokens.get(i).getClass();
    }
}
