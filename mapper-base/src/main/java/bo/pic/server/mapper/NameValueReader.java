package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.*;

import java.math.BigInteger;

public abstract class NameValueReader {
    protected final TreeReader reader;

    public NameValueReader(TreeReader reader) {
        this.reader = reader;
    }

    public void readUntilEndOfObject() {
        Token token;
        while (!((token = reader.nextToken()) instanceof EndObjectToken)) {
            if (!(token instanceof NameToken)) {
                throw new IllegalArgumentException("Unexpected token: " + token);
            }
            handle(((NameToken) token).getName());
        }

    }

    protected String nextString() {
        Token token = reader.nextToken();
        if (!(token instanceof StringToken)) {
            throw new UnexpectedTokenException(token);
        }
        return ((StringToken) token).getValue();
    }

    protected BigInteger nextBigInteger() {
        Token token = reader.nextToken();
        if (!(token instanceof BigIntegerToken)) {
            throw new UnexpectedTokenException(token);
        }
        return ((BigIntegerToken) token).getValue();
    }

    protected void skipValue() {
        reader.nextToken().accept(new TokenVisitor() {
            @Override
            public void dispatch(BeginObjectToken token) {
                reader.nextToken().accept(new InObjectVisitor());
            }

            @Override
            public void dispatch(BeginArrayToken token) {
                reader.nextToken().accept(new InArrayVisitor());
            }

            @Override
            public void dispatch(EndObjectToken token) {
                throw new UnsupportedOperationException("EndObjectToken unexpected here");
            }

            @Override
            public void dispatch(EndArrayToken token) {
                throw new UnsupportedOperationException("EndArrayToken unexpected here");
            }

            @Override
            public void dispatch(NameToken token) {
                throw new UnsupportedOperationException("NameToken unexpected here");
            }

            @Override
            public void dispatch(BooleanToken token) {
            }

            @Override
            public void dispatch(NullObjectToken token) {
            }

            @Override
            public void dispatch(StringToken token) {
            }

            @Override
            public void dispatch(BigIntegerToken token) {
            }
        });
    }

    protected abstract void handle(String name);

    private class InObjectVisitor implements TokenVisitor {
        @Override
        public void dispatch(BeginObjectToken token) {
            reader.nextToken().accept(new InObjectVisitor());
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(EndObjectToken token) {
        }

        @Override
        public void dispatch(BooleanToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(NullObjectToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(StringToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(BigIntegerToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(NameToken nameToken) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(EndArrayToken endArrayToken) {
            throw new IllegalStateException("EndArrayToken not supported here");
        }

        @Override
        public void dispatch(BeginArrayToken beginArrayToken) {
            reader.nextToken().accept(new InArrayVisitor());
            reader.nextToken().accept(this);
        }
    }

    private class InArrayVisitor implements TokenVisitor {
        @Override
        public void dispatch(BeginObjectToken token) {
            reader.nextToken().accept(new InObjectVisitor());
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(EndObjectToken token) {
            throw new IllegalStateException("EndObjectToken not supported here");
        }

        @Override
        public void dispatch(BooleanToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(NullObjectToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(StringToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(BigIntegerToken token) {
            reader.nextToken().accept(this);
        }

        @Override
        public void dispatch(NameToken nameToken) {
            throw new IllegalStateException("NameToken not supported here");
        }

        @Override
        public void dispatch(EndArrayToken endArrayToken) {
        }

        @Override
        public void dispatch(BeginArrayToken beginArrayToken) {
            reader.nextToken().accept(new InArrayVisitor());
            reader.nextToken().accept(this);
        }
    }
}
