package bo.pic.server.mapper;

import bo.pic.server.mapper.tokens.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class BasicMapperProvider implements ObjectMapperProvider {
    @Nullable
    @Override
    public Mapper mapperFor(@Nonnull Type clazz, @Nonnull MappingContext context) {
        if (clazz == String.class) {
            return new StringMapper();
        }
        if (clazz == BigInteger.class) {
            return new BigIntegerMapper();
        }
        if (clazz == Integer.class || clazz == int.class) {
            return new IntMapper();
        }
        if (clazz == Long.class || clazz == long.class) {
            return new LongMapper();
        }
        if (clazz == Double.class || clazz == double.class) {
            return new DoubleMapper();
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return new BooleanMapper();
        }
        if (clazz == Date.class) {
            return new DateMapper();
        }
        return null;
    }

    private static class DateMapper implements Mapper<Date> {

        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Date object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
            } else {
                writer.append(new StringToken(dateFormatter().format(object)));
            }
        }

        @Nullable
        @Override
        public Date read(@Nonnull TreeReader reader) {
            Token token = reader.nextToken();
            if (token instanceof NullObjectToken) {
                return null;
            }
            if (token instanceof StringToken) {
                try {
                    return dateFormatter().parse(((StringToken) token).getValue());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new UnexpectedTokenException(token);
        }
    }

    private static class StringMapper implements Mapper<String> {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable String object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
            } else {
                writer.append(new StringToken(object));
            }
        }

        @Nullable
        @Override
        public String read(@Nonnull TreeReader reader) {
            Token token = reader.nextToken();
            if (token instanceof NullObjectToken) {
                return null;
            }
            if (token instanceof StringToken) {
                return ((StringToken) token).getValue();
            }
            throw new IllegalArgumentException("Unexpected token: " + token);
        }
    }

    private static class BigIntegerMapper implements Mapper<BigInteger> {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable BigInteger object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
            } else {
                writer.append(new BigIntegerToken(object));
            }
        }

        @Nullable
        @Override
        public BigInteger read(@Nonnull TreeReader reader) {
            Token token = reader.nextToken();
            if (token instanceof NullObjectToken) {
                return null;
            }
            if (token instanceof BigIntegerToken) {
                return ((BigIntegerToken) token).getValue();
            }
            throw new IllegalArgumentException("Unexpected token: " + token);
        }
    }

    private static class LongMapper implements Mapper<Long> {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Long object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
            } else {
                writer.append(new BigIntegerToken(BigInteger.valueOf(object)));
            }
        }

        @Nullable
        @Override
        public Long read(@Nonnull TreeReader reader) {
            Token token = reader.nextToken();
            if (token instanceof NullObjectToken) {
                return null;
            }
            if (token instanceof BigIntegerToken) {
                return toLong(((BigIntegerToken) token).getValue());
            }
            throw new IllegalArgumentException("Unexpected token: " + token);
        }

        private Long toLong(BigInteger value) {
            long v = value.longValue();
            if (!BigInteger.valueOf(v).equals(value)) {
                throw new IllegalArgumentException("Can not convert big integer " + value + " to long");
            }
            return v;
        }
    }

    //TODO save as double
    private static class DoubleMapper implements Mapper<Double> {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Double object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
            } else {
                writer.append(new StringToken(String.valueOf(object)));
            }
        }

        @Nullable
        @Override
        public Double read(@Nonnull TreeReader reader) {
            Token token = reader.nextToken();
            if (token instanceof NullObjectToken) {
                return null;
            }
            if (token instanceof StringToken) {
                return Double.parseDouble(((StringToken) token).getValue());
            }
            throw new IllegalArgumentException("Unexpected token: " + token);
        }
    }

    private static class IntMapper implements Mapper<Integer> {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Integer object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
            } else {
                writer.append(new BigIntegerToken(BigInteger.valueOf(object)));
            }
        }

        @Nullable
        @Override
        public Integer read(@Nonnull TreeReader reader) {
            Token token = reader.nextToken();
            if (token instanceof NullObjectToken) {
                return null;
            }
            if (token instanceof BigIntegerToken) {
                return toInt(((BigIntegerToken) token).getValue());
            }
            throw new IllegalArgumentException("Unexpected token: " + token);
        }

        private Integer toInt(BigInteger value) {
            int v = value.intValue();
            if (!BigInteger.valueOf(v).equals(value)) {
                throw new IllegalArgumentException("Can not convert big integer " + value + " to int");
            }
            return v;
        }
    }

    private static class BooleanMapper implements Mapper<Boolean> {
        @Override
        public void write(@Nonnull TreeWriter writer, @Nullable Boolean object) {
            if (object == null) {
                writer.append(NullObjectToken.INSTANCE);
            } else {
                writer.append(new BooleanToken(object));
            }
        }

        @Nullable
        @Override
        public Boolean read(@Nonnull TreeReader reader) {
            Token token = reader.nextToken();
            if (token instanceof NullObjectToken) {
                return null;
            }
            if (token instanceof BooleanToken) {
                return ((BooleanToken) token).getValue();
            }
            throw new IllegalArgumentException("Unexpected token: " + token);
        }
    }

    private static final String DATE_FORMATTER = "d MMM yyyy HH:mm:ss Z";

    public static SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat(DATE_FORMATTER, Locale.ENGLISH);
    }
}
