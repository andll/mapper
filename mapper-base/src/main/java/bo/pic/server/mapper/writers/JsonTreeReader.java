package bo.pic.server.mapper.writers;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import bo.pic.server.mapper.TreeReader;
import bo.pic.server.mapper.tokens.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;

public class JsonTreeReader implements TreeReader {
    private final JsonReader jsonReader;

    public JsonTreeReader(@Nonnull String value) {
        jsonReader = new JsonReader(new StringReader(value));
    }

    @Nonnull
    @Override
    public Token nextToken() {
        try {
            switch (jsonReader.peek()) {
                case BEGIN_OBJECT:
                    jsonReader.beginObject();
                    return new BeginObjectToken();
                case BEGIN_ARRAY:
                    jsonReader.beginArray();
                    return new BeginArrayToken();
                case BOOLEAN:
                    return new BooleanToken(jsonReader.nextBoolean());
                case END_ARRAY:
                    jsonReader.endArray();
                    return new EndArrayToken();
                case END_DOCUMENT:
                    throw new UnsupportedOperationException();
                case END_OBJECT:
                    jsonReader.endObject();
                    return new EndObjectToken();
                case NAME:
                    return new NameToken(jsonReader.nextName());
                case NULL:
                    jsonReader.nextNull();
                    return NullObjectToken.INSTANCE;
                case NUMBER:
                    return new BigIntegerToken(new BigInteger(jsonReader.nextString()));
                case STRING:
                    return new StringToken(jsonReader.nextString());
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Nonnull
    public Class<? extends Token> peekTokenClass() {
        JsonToken peek;
        try {
            peek = jsonReader.peek();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        switch (peek) {
            case BEGIN_OBJECT:
                return BeginObjectToken.class;
            case BEGIN_ARRAY:
                return BeginArrayToken.class;
            case BOOLEAN:
                return BooleanToken.class;
            case END_ARRAY:
                return EndArrayToken.class;
            case END_DOCUMENT:
                throw new UnsupportedOperationException();
            case END_OBJECT:
                return EndObjectToken.class;
            case NAME:
                return NameToken.class;
            case NULL:
                return NullObjectToken.class;
            case NUMBER:
                return BigIntegerToken.class;
            case STRING:
                return StringToken.class;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
