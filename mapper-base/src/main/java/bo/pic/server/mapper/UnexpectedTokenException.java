package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.Token;

public class UnexpectedTokenException extends IllegalArgumentException {
    private static final long serialVersionUID = -2047000826535316017L;

    public UnexpectedTokenException(Token token) {
        super("Unexpected token: " + token);
    }
}
