package bo.pic.server.mapper.tokens;

public interface TokenVisitor {
    void dispatch(BeginObjectToken token);

    void dispatch(EndObjectToken token);

    void dispatch(BooleanToken token);

    void dispatch(NullObjectToken token);

    void dispatch(StringToken token);

    void dispatch(BigIntegerToken token);

    void dispatch(NameToken nameToken);

    void dispatch(EndArrayToken endArrayToken);

    void dispatch(BeginArrayToken beginArrayToken);
}
