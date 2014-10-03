package bo.pic.server.mapper.sample;

import bo.pic.server.mapper.cglib.AutoSerializable;

import java.util.SortedMap;

public class WithSortedMap implements AutoSerializable {
    private final SortedMap<String, Long> map;

    public WithSortedMap(SortedMap<String, Long> map) {
        this.map = map;
    }

    public SortedMap<String, Long> getMap() {
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WithSortedMap withmap = (WithSortedMap) o;

        if (map != null ? !map.equals(withmap.map) : withmap.map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WithSortedMap{");
        sb.append("map=").append(map);
        sb.append('}');
        return sb.toString();
    }
}
