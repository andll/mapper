package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;

public class Simple implements AutoSerializable {
    private final String x;

    public Simple(String x) {
        this.x = x;
    }

    public String getX() {
        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Simple simple = (Simple) o;

        if (x != null ? !x.equals(simple.x) : simple.x != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return x != null ? x.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Simple{");
        sb.append("x='").append(x).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
