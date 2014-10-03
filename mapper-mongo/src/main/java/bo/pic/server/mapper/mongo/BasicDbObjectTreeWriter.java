package bo.pic.server.mapper.mongo;

import bo.pic.server.mapper.Mapper;
import bo.pic.server.mapper.TreeWriter;
import bo.pic.server.mapper.tokens.*;
import com.mongodb.BasicDBObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BasicDbObjectTreeWriter implements TreeWriter {
    private TokenVisitor state = new InitState();

    @Nonnull
    @Override
    public TreeWriter append(@Nonnull Token token) {
        token.accept(state);
        return this;
    }

    public BasicDBObject getResult() {
        if (!(state instanceof ResultState)) {
            throw new IllegalStateException("Illegal state: " + state);
        }
        return ((ResultState) state).getObject();
    }

    private class InitState extends AbstractTokenVisitor {
        @Override
        public void dispatch(BeginObjectToken token) {
            BasicDBObject object = new BasicDBObject();
            state = new FillObjectState(object, new ResultState(object));
        }
    }

    private class FillObjectState extends AbstractTokenVisitor {
        private final BasicDBObject object;
        private final TokenVisitor  parentState;
        private       String        name;

        private FillObjectState(@Nonnull BasicDBObject object, @Nonnull TokenVisitor parentState) {
            this.object = object;
            this.parentState = parentState;
        }

        @Override
        public void dispatch(BeginObjectToken token) {
            BasicDBObject o = new BasicDBObject();
            object.append(popName(), o);
            state = new FillObjectState(o, this);
        }

        @Override
        public void dispatch(EndObjectToken token) {
            if (name != null) {
                throw new IllegalStateException("Name specified at the end of object: " + name);
            }
            state = parentState;
        }

        @Override
        public void dispatch(BooleanToken token) {
            object.append(popName(), token.getValue());
        }

        @Override
        public void dispatch(NullObjectToken token) {
            object.append(popName(), null);
        }

        @Override
        public void dispatch(StringToken token) {
            object.append(popName(), token.getValue());
        }

        @Override
        public void dispatch(BigIntegerToken token) {
            object.append(popName(), token.getValue().longValue());//TODO fix
        }

        @Override
        public void dispatch(BeginArrayToken token) {
            List<Object> a = new ArrayList<>();
            object.append(popName(), a);
            state = new FillArrayState(a, this);
        }

        @Override
        public void dispatch(NameToken token) {
            if (name != null) {
                throw new IllegalStateException("Name already specified: " + name);
            }
            name = token.getName();
        }

        private String popName() {
            if (name == null) {
                throw new IllegalStateException("Name not specified");
            }
            String _name = name;
            name = null;
            return _name;
        }
    }

    private class FillArrayState extends AbstractTokenVisitor {
        private final List<Object> array;
        private final TokenVisitor parentState;

        private FillArrayState(@Nonnull List<Object> array, @Nonnull TokenVisitor parentState) {
            this.array = array;
            this.parentState = parentState;
        }

        @Override
        public void dispatch(BeginObjectToken token) {
            BasicDBObject o = new BasicDBObject();
            array.add(o);
            state = new FillObjectState(o, this);
        }

        @Override
        public void dispatch(BooleanToken token) {
            array.add(token.getValue());
        }

        @Override
        public void dispatch(NullObjectToken token) {
            array.add(null);
        }

        @Override
        public void dispatch(StringToken token) {
            array.add(token.getValue());
        }

        @Override
        public void dispatch(BigIntegerToken token) {
            array.add(token.getValue());
        }

        @Override
        public void dispatch(EndArrayToken token) {
            state = parentState;
        }

        @Override
        public void dispatch(BeginArrayToken token) {
            List<Object> a = new ArrayList<>();
            array.add(a);
            state = new FillArrayState(a, this);
        }
    }

    private class ResultState extends AbstractTokenVisitor {
        private final BasicDBObject object;

        private ResultState(BasicDBObject object) {
            this.object = object;
        }

        public BasicDBObject getObject() {
            return object;
        }
    }

    public static <T> BasicDBObject toBasicDBObject(Mapper<T> mapper, T object) {
        BasicDbObjectTreeWriter writer = new BasicDbObjectTreeWriter();
        mapper.write(writer, object);
        return writer.getResult();
    }
}
