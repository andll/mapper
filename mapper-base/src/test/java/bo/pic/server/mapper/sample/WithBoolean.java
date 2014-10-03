package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;

public class WithBoolean implements AutoSerializable {
    private final Boolean a;
    private final boolean b;

    public WithBoolean(Boolean a, boolean b) {
        this.a = a;
        this.b = b;
    }

    public Boolean getA() {
        return a;
    }

    public boolean isB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WithBoolean that = (WithBoolean) o;

        if (b != that.b) return false;
        if (a != null ? !a.equals(that.a) : that.a != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b ? 1 : 0);
        return result;
    }

    @Override

    public String toString() {
        final StringBuilder sb = new StringBuilder("WithBoolean{");
        sb.append("a=").append(a);
        sb.append(", b=").append(b);
        sb.append('}');
        return sb.toString();
    }
}
