package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;

public class Nested implements AutoSerializable {
    private final Simple simple;
    private final String z;

    public Nested(Simple simple, String z) {
        this.simple = simple;
        this.z = z;
    }

    public Simple getSimple() {
        return simple;
    }

    public String getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nested nested = (Nested) o;

        if (simple != null ? !simple.equals(nested.simple) : nested.simple != null) return false;
        if (z != null ? !z.equals(nested.z) : nested.z != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = simple != null ? simple.hashCode() : 0;
        result = 31 * result + (z != null ? z.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Nested{");
        sb.append("simple=").append(simple);
        sb.append(", z='").append(z).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
