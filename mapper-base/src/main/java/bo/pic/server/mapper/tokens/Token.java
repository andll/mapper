package bo.pic.server.mapper.tokens;

public abstract class Token {

    Token() {
    }

    public abstract void accept(TokenVisitor visitor);
}
