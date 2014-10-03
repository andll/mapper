package bo.pic.server.mapper.writers;

import com.google.gson.stream.JsonWriter;
import bo.pic.server.mapper.TreeWriter;
import bo.pic.server.mapper.tokens.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;

public class JsonTreeWriter implements TreeWriter {
    private final StringWriter stringWriter = new StringWriter();
    private final JsonWriter jsonWriter = new JsonWriter(stringWriter);

    @Nonnull
    @Override
    public TreeWriter append(@Nonnull Token token) {
        token.accept(new MyTokenVisitor());
        return this;
    }

    public String toJsonString() {
        try {
            jsonWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }

    private class MyTokenVisitor implements TokenVisitor {
        @Override
        public void dispatch(BeginObjectToken token) {
            try {
                jsonWriter.beginObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(EndObjectToken token) {
            try {
                jsonWriter.endObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(BooleanToken token) {
            try {
                jsonWriter.value(token.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(NullObjectToken token) {
            try {
                jsonWriter.nullValue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(StringToken token) {
            try {
                jsonWriter.value(token.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(BigIntegerToken token) {
            try {
                jsonWriter.value(token.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(NameToken nameToken) {
            try {
                jsonWriter.name(nameToken.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(EndArrayToken endArrayToken) {
            try {
                jsonWriter.endArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatch(BeginArrayToken beginArrayToken) {
            try {
                jsonWriter.beginArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
