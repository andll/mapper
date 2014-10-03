package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.*;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings("unchecked")
class HashMapTreeWriter implements TreeWriter {
    private final Stack<Object> stack = new Stack<>();
    private String name;
    private HashMap root;

    @Nonnull
    @Override
    public HashMapTreeWriter append(@Nonnull Token token) {
        token.accept(new TokenVisitor() {
            @Override
            public void dispatch(BeginObjectToken token) {
                HashMap value = new HashMap();
                if (!stack.isEmpty()) {
                    addValue(value);
                }
                stack.push(value);
            }

            @Override
            public void dispatch(EndObjectToken token) {
                if (stack.isEmpty()) {
                    throw new IllegalStateException("endObject without beginObject");
                }
                Object pop = stack.pop();
                if (!(pop instanceof HashMap)) {
                    throw new IllegalStateException("Illegal endObject, currently in stack: " + pop);
                }
                if (stack.isEmpty()) {
                    root = (HashMap) pop; //In test we always assume object as root
                }
            }

            @Override
            public void dispatch(BooleanToken token) {
                Object value = token.getValue();
                addValue(value);
            }

            @Override
            public void dispatch(NullObjectToken token) {
                addValue(null);
            }

            @Override
            public void dispatch(StringToken token) {
                addValue(token.getValue());
            }

            @Override
            public void dispatch(BigIntegerToken token) {
                addValue(token.getValue());
            }

            private void addValue(Object value) {
                Object peek = stack.peek();
                if (peek instanceof Map) {
                    ((Map) peek).put(resetName(), value);
                } else {
                    ((Collection) peek).add(value);
                }
            }

            @Override
            public void dispatch(NameToken nameToken) {
                if (name != null) {
                    throw new IllegalStateException("Name already specified: " + name);
                }
                name = nameToken.getName();
            }

            @Override
            public void dispatch(EndArrayToken endArrayToken) {
                if (stack.isEmpty()) {
                    throw new IllegalStateException("endArray without beginArray");
                }
                Object pop = stack.pop();
                if (!(pop instanceof Collection)) {
                    throw new IllegalStateException("Illegal endArray, currently in stack: " + pop);
                }
            }

            @Override
            public void dispatch(BeginArrayToken beginArrayToken) {
                Collection collection = new ArrayList();
                addValue(collection);
                stack.push(collection);
            }
        });
        return this;
    }

    private String resetName() {
        if (name == null) {
            throw new IllegalStateException("No name specified");
        }
        String name = this.name;
        this.name = null;
        return name;
    }

    public Map getRoot() {
        if (!stack.isEmpty()) {
            throw new IllegalStateException("Last object was not closed");
        }
        if (root == null) {
            throw new IllegalStateException("Object was never begun");
        }
        return root;
    }
}
