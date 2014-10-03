package bo.pic.server.mapper.tokens;

public class NullObjectToken extends Token {

    /** Gof Flyweight for {@link NullObjectToken} class. */
    public static final NullObjectToken INSTANCE = new NullObjectToken();

    /** Uae {@link #INSTANCE}. */
    private NullObjectToken() {
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }
}
