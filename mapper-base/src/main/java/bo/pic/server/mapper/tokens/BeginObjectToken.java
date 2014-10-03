package bo.pic.server.mapper.tokens;

public class BeginObjectToken extends Token {
    public BeginObjectToken() {
        super();
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }
}
