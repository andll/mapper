package bo.pic.server.mapper.tokens;

public class BooleanToken extends Token {
    private final Boolean value;

    public BooleanToken(Boolean value) {
        super();
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BooleanToken{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
