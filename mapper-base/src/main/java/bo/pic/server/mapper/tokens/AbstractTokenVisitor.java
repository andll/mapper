package bo.pic.server.mapper.tokens;

import bo.pic.server.mapper.UnexpectedTokenException;

public abstract class AbstractTokenVisitor implements TokenVisitor {
    @Override
    public void dispatch(BeginObjectToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(EndObjectToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(BooleanToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(NullObjectToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(StringToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(BigIntegerToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(NameToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(EndArrayToken token) {
        defaultAction(token);
    }

    @Override
    public void dispatch(BeginArrayToken token) {
        defaultAction(token);
    }

    protected void defaultAction(Token token) {
        throw new UnexpectedTokenException(token);
    }
}
