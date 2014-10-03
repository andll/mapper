package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;

import java.util.Set;

public class WithSet implements AutoSerializable {
    private final Set<String> set;

    public WithSet(Set<String> set) {
        this.set = set;
    }

    public Set<String> getSet() {
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WithSet withSet = (WithSet) o;

        if (set != null ? !set.equals(withSet.set) : withSet.set != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return set != null ? set.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WithSet{");
        sb.append("set=").append(set);
        sb.append('}');
        return sb.toString();
    }
}
