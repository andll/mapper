package bo.pic.server.mapper.tokens;

public class BeginArrayToken extends Token {
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }
}
