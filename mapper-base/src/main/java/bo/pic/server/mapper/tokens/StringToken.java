package bo.pic.server.mapper.tokens;

public class StringToken extends Token {
    private final String value;

    public StringToken(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StringToken{");
        sb.append("value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
