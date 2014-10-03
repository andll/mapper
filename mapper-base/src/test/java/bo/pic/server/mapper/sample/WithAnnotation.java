package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.TreeReader;
import bo.pic.server.mapper.TreeWriter;
import bo.pic.server.mapper.WithMapper;
import bo.pic.server.mapper.tokens.BeginObjectToken;
import bo.pic.server.mapper.tokens.EndObjectToken;
import bo.pic.server.mapper.tokens.NameToken;
import bo.pic.server.mapper.tokens.StringToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WithMapper(WithAnnotation.Mapper.class)
public class WithAnnotation {
    public static final WithAnnotation CONST = new WithAnnotation();

    public static class Mapper implements bo.pic.server.mapper.Mapper<WithAnnotation> {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable WithAnnotation object) {
            writer.append(new BeginObjectToken());
            writer.append(new NameToken("name"));
            writer.append(new StringToken("XYZ"));
            writer.append(new EndObjectToken());
        }

        @Nullable
        @Override
        public WithAnnotation read(@Nonnull TreeReader reader) {
            return CONST;
        }
    }
}
