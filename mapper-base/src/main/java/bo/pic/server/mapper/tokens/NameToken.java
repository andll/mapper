package bo.pic.server.mapper.tokens;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public class NameToken extends Token {
    private static final Pattern PATTERN = Pattern.compile("[A-Za-z0-9_$]+");
    private final String name;

    public NameToken(String name) {
        if (!validName(name)) {
            throw new IllegalArgumentException("Illegal name: " + name);
        }
        this.name = name;
    }

    private static boolean validName(String name) {return PATTERN.matcher(name).matches();}

    @Nullable
    public static NameToken tryCreate(String name) {
        if (!validName(name)) {
            return null;
        }
        return new NameToken(name);
    }

    @Override
    public void accept(TokenVisitor visitor) {
        visitor.dispatch(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NameToken{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
