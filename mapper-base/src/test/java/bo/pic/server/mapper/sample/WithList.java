package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;

import java.util.List;

public class WithList implements AutoSerializable {
    private final List<String> list;

    public WithList(List<String> list) {
        this.list = list;
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WithList withSet = (WithList) o;

        if (list != null ? !list.equals(withSet.list) : withSet.list != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return list != null ? list.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WithSet{");
        sb.append("list=").append(list);
        sb.append('}');
        return sb.toString();
    }
}
