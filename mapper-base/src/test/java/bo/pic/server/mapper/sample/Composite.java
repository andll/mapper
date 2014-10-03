package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;
import bo.pic.server.mapper.cglib.DeserializingConstructor;
import bo.pic.server.mapper.cglib.FieldRef;

public class Composite implements AutoSerializable {
    private final String x;
    private final String y;

    @DeserializingConstructor
    public Composite(@FieldRef("x") String x,
                     @FieldRef("y") String y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Composite composite = (Composite) o;

        if (x != null ? !x.equals(composite.x) : composite.x != null) return false;
        if (y != null ? !y.equals(composite.y) : composite.y != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Composite{");
        sb.append("x='").append(x).append('\'');
        sb.append(", y='").append(y).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
