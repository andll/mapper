package bo.pic.server.mapper.tokens;

public class EndArrayToken extends Token {
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }
}
