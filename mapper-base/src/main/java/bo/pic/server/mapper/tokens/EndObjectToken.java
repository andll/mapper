package bo.pic.server.mapper.tokens;

public class EndObjectToken extends Token {
    public EndObjectToken() {
        super();
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }
}
