package bo.pic.server.mapper.tokens;

import java.math.BigInteger;

public class BigIntegerToken extends Token {
    private final BigInteger value;

    public BigIntegerToken(BigInteger value) {
        super();
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BigIntegerToken{");
        sb.append("value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
