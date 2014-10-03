package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;

public class WithLong implements AutoSerializable {
    private final Long a;
    private final long b;

    public WithLong(Long a, long b) {
        this.a = a;
        this.b = b;
    }

    public Long getA() {
        return a;
    }

    public long getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WithLong withLong = (WithLong) o;

        if (b != withLong.b) return false;
        if (a != null ? !a.equals(withLong.a) : withLong.a != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (int) (b ^ (b >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WithLong{");
        sb.append("a=").append(a);
        sb.append(", b=").append(b);
        sb.append('}');
        return sb.toString();
    }
}
