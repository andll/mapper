package bo.pic.server.mapper.mongo;

import bo.pic.server.mapper.BasicMapperProvider;
import bo.pic.server.mapper.Mapper;
import bo.pic.server.mapper.TreeReader;
import bo.pic.server.mapper.tokens.*;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BasicDbObjectTreeReader implements TreeReader {
    private TreeReader state;

    public BasicDbObjectTreeReader(DBObject object) {
        this.state = new BeginObjectState(nextReadingObjectState(object, new FinalState()));
    }

    @Nonnull
    @Override
    public Token nextToken() {
        return state.nextToken();
    }

    @Nonnull
    @Override
    public Class<? extends Token> peekTokenClass() {
        return state.peekTokenClass();
    }

    private class FinalState implements TreeReader {
        @Nonnull
        @Override
        public Token nextToken() {
            throw new UnsupportedOperationException("nextToken not supported in final state");
        }

        @Nonnull
        @Override
        public Class<? extends Token> peekTokenClass() {
            throw new UnsupportedOperationException("peekTokenClass not supported in final state");
        }
    }

    private class ReadNameState implements TreeReader {
        private final TreeReader parentState;
        private final Iterator<Map.Entry<String, Object>> iterator;
        private final NameToken name;
        private final Object value;

        private ReadNameState(TreeReader parentState, Iterator<Map.Entry<String, Object>> iterator, NameToken name, Object value) {
            this.parentState = parentState;
            this.iterator = iterator;
            this.name = name;
            this.value = value;
        }

        @Nonnull
        @Override
        public Token nextToken() {
            state = new ReadValueState(parentState, iterator, value);
            return name;
        }

        @Nonnull
        @Override
        public Class<? extends Token> peekTokenClass() {
            return NameToken.class;
        }
    }

    private class OnEndObjectState implements TreeReader {
        private final TreeReader nextState;

        private OnEndObjectState(TreeReader nextState) {
            this.nextState = nextState;
        }

        @Nonnull
        @Override
        public Token nextToken() {
            state = nextState;
            return new EndObjectToken();
        }

        @Nonnull
        @Override
        public Class<? extends Token> peekTokenClass() {
            return EndObjectToken.class;
        }
    }

    private class OnEndArrayState implements TreeReader {
        private final TreeReader nextState;

        private OnEndArrayState(TreeReader nextState) {
            this.nextState = nextState;
        }

        @Nonnull
        @Override
        public Token nextToken() {
            state = nextState;
            return new EndArrayToken();
        }

        @Nonnull
        @Override
        public Class<? extends Token> peekTokenClass() {
            return EndArrayToken.class;
        }
    }

    private class ReadArrayState extends AbstractReadingState {
        private final TreeReader nextState;
        private final Iterator<Object> iterator;
        private final Object value;

        private ReadArrayState(TreeReader nextState, Iterator<Object> iterator, Object value) {
            this.nextState = nextState;
            this.iterator = iterator;
            this.value = value;
        }

        @Override
        protected TreeReader nextState() {
            if (iterator.hasNext()) {
                return new ReadArrayState(nextState, iterator, iterator.next());
            }
            return new OnEndArrayState(nextState);

        }

        @Override
        @Nullable
        protected Object getValue() {
            return value;
        }
    }

    private class ReadValueState extends AbstractReadingState {
        private final TreeReader nextState;
        private final Iterator<Map.Entry<String, Object>> iterator;
        private final Object value;

        private ReadValueState(TreeReader nextState, Iterator<Map.Entry<String, Object>> iterator, Object value) {
            this.nextState = nextState;
            this.iterator = iterator;
            this.value = value;
        }

        @Override
        protected TreeReader nextState() {
            return nextReadingObjectState(nextState, iterator);
        }

        @Override
        @Nullable
        protected Object getValue() {
            return value;
        }
    }

    private abstract class AbstractReadingState implements TreeReader {
        @Nonnull
        public Token nextToken() {
            Object value = getValue();
            if (value == null) {
                state = nextState();
                return NullObjectToken.INSTANCE;
            }
            if (value instanceof String) {
                state = nextState();
                return new StringToken((String) value);
            }
            if (value instanceof Number) {
                state = nextState();
                return new BigIntegerToken(BigInteger.valueOf(((Number) value).longValue())); //TODO fix
            }
            if (value instanceof ObjectId) {
                state = nextState();
                return new BigIntegerToken(new BigInteger(((ObjectId) value).toByteArray()));
            }
            if (value instanceof Boolean) {
                state = nextState();
                return new BooleanToken((Boolean) value);
            }
            if (value instanceof Date) {
                state = nextState();
                return new StringToken(BasicMapperProvider.dateFormatter().format((Date) value));
            }
            if (value instanceof List) {
                @SuppressWarnings("unchecked") Iterator<Object> newIterator = ((List<Object>) value).iterator();
                if (newIterator.hasNext()) {
                    state = new ReadArrayState(nextState(), newIterator, newIterator.next());
                } else {
                    state = new OnEndArrayState(nextState());
                }
                return new BeginArrayToken();
            }
            if (value instanceof DBObject) {
                state = nextReadingObjectState((DBObject) value, nextState());
                return new BeginObjectToken();
            }
            throw new UnsupportedOperationException("Unknown value of type " + value.getClass() + ": " + value);
        }

        @Nonnull
        @Override
        public Class<? extends Token> peekTokenClass() {
            Object value = getValue();
            if (value == null) {
                return NullObjectToken.class;
            }
            if (value instanceof String) {
                return StringToken.class;
            }
            if (value instanceof Number) {
                return BigIntegerToken.class;
            }
            if (value instanceof Boolean) {
                return BooleanToken.class;
            }
            if (value instanceof List) {
                return BeginArrayToken.class;
            }
            if (value instanceof DBObject) {
                return BeginObjectToken.class;
            }
            throw new UnsupportedOperationException("Unknown value: " + value);
        }

        protected abstract TreeReader nextState();

        @Nullable
        protected abstract Object getValue();
    }

    private class BeginObjectState implements TreeReader {
        private final TreeReader nextState;

        private BeginObjectState(TreeReader nextState) {
            this.nextState = nextState;
        }

        @Nonnull
        @Override
        public Token nextToken() {
            state = nextState;
            return new BeginObjectToken();
        }

        @Nonnull
        @Override
        public Class<? extends Token> peekTokenClass() {
            return BeginObjectToken.class;
        }
    }

    @SuppressWarnings("unchecked")
    private TreeReader nextReadingObjectState(DBObject value, TreeReader nextState) {
        return nextReadingObjectState(nextState, value.toMap().entrySet().iterator());
    }

    private TreeReader nextReadingObjectState(TreeReader nextState, Iterator<Map.Entry<String, Object>> iterator) {
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            NameToken name = NameToken.tryCreate(next.getKey());
            if (name != null) {
                return new ReadNameState(nextState, iterator, name, next.getValue());
            }
        }
        return new OnEndObjectState(nextState);
    }

    public static <T> T fromDBObject(Mapper<T> mapper, DBObject dbObject) {
        return mapper.read(new BasicDbObjectTreeReader(dbObject));
    }
}
