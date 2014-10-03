package bo.pic.server.mapper;

import bo.pic.server.mapper.sample.*;
import bo.pic.server.mapper.tokens.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class MapperTest {
    @Test
    public void writesCompositeObject() {
        final Composite object = new Composite("X", "Y");
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, Composite.class, object);
        Assert.assertEquals(ImmutableMap.of("x", "X", "y", "Y"), writer.getRoot());
    }

    @Test
    public void writesNestedObject() {
        final Nested object = new Nested(new Simple("X"), "Z");
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, Nested.class, object);
        Assert.assertEquals(ImmutableMap.of("z", "Z", "simple", ImmutableMap.of("x", "X")), writer.getRoot());
    }

    @Test
    public void writesObjectWithNullField() {
        final Simple object = new Simple(null);
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, Simple.class, object);
        Assert.assertEquals(Collections.singletonMap("x", null), writer.getRoot());
    }

    @Test
    public void writesLong() {
        final WithLong object = new WithLong(1l, 2l);
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, WithLong.class, object);
        Assert.assertEquals(ImmutableMap.of("a", BigInteger.ONE, "b", BigInteger.valueOf(2)), writer.getRoot());
    }

    @Test
    public void writesBoolean() {
        final WithBoolean object = new WithBoolean(true, false);
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, WithBoolean.class, object);
        Assert.assertEquals(ImmutableMap.of("a", Boolean.TRUE, "b", Boolean.FALSE), writer.getRoot());
    }

    @Test
    public void writesSet() {
        final WithSet object = new WithSet(Sets.newHashSet("a"));
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, WithSet.class, object);
        Assert.assertEquals(ImmutableMap.of("set", Lists.newArrayList("a")), writer.getRoot());
    }

    @Test
    public void writesMap() {
        final WithMap object = new WithMap(ImmutableMap.of("a", 7l));
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, WithMap.class, object);
        Map root = writer.getRoot();
        Assert.assertEquals(ImmutableMap.of("map", Lists.newArrayList("a", BigInteger.valueOf(7))), root);
    }

    @Test
    public void writesSortedMap() {
        final WithSortedMap object = new WithSortedMap(new TreeMap<>(ImmutableMap.of("a", 7l)));
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, WithSortedMap.class, object);
        Map root = writer.getRoot();
        Assert.assertEquals(ImmutableMap.of("map", Lists.newArrayList("a", BigInteger.valueOf(7))), root);
    }

    @Test
    public void writesList() {
        final WithList object = new WithList(Arrays.asList("a", "b"));
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, WithList.class, object);
        Map root = writer.getRoot();
        Assert.assertEquals(ImmutableMap.of("list", Lists.newArrayList("a", "b")), root);
    }

    @Test
    public void writesWithAnnotation() {
        final WithAnnotation object = WithAnnotation.CONST;
        final HashMapTreeWriter writer = new HashMapTreeWriter();
        new MapperFacade().write(writer, WithAnnotation.class, object);
        Map root = writer.getRoot();
        Assert.assertEquals(ImmutableMap.of("name", "XYZ"), root);
    }


    @Test
    public void readsCompositeObject() {
        Composite b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("x"),
            new StringToken("X"),
            new NameToken("y"),
            new StringToken("Y"),
            new EndObjectToken()
        ), Composite.class);
        Assert.assertEquals(new Composite("X", "Y"), b);
    }

    @Test
    public void readsCompositeObjectWithRedundantValueInSource() {
        Composite b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("x"),
            new StringToken("X"),
            new NameToken("z"),
            new StringToken("Z"),
            new NameToken("y"),
            new StringToken("Y"),
            new EndObjectToken()
        ), Composite.class);
        Assert.assertEquals(new Composite("X", "Y"), b);
    }

    @Test
    public void readsCompositeObjectWithRedundantObjectInSource() {
        Composite b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("x"),
            new StringToken("X"),
            new NameToken("z"),
            new BeginObjectToken(),
            new NameToken("z"),
            new StringToken("Z"),
            new EndObjectToken(),
            new NameToken("y"),
            new StringToken("Y"),
            new EndObjectToken()
        ), Composite.class);
        Assert.assertEquals(new Composite("X", "Y"), b);
    }

    @Test
    public void readsCompositeObjectWithRedundantEmptyObjectInSource() {
        Composite b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("x"),
            new StringToken("X"),
            new NameToken("z"),
            new BeginObjectToken(),
            new EndObjectToken(),
            new NameToken("y"),
            new StringToken("Y"),
            new EndObjectToken()
        ), Composite.class);
        Assert.assertEquals(new Composite("X", "Y"), b);
    }

    @Test
    public void readsCompositeObjectWithRedundantArrayInSource() {
        Composite b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("x"),
            new StringToken("X"),
            new NameToken("z"),
            new BeginArrayToken(),
            new StringToken("C"),
            new StringToken("Z"),
            new EndArrayToken(),
            new NameToken("y"),
            new StringToken("Y"),
            new EndObjectToken()
        ), Composite.class);
        Assert.assertEquals(new Composite("X", "Y"), b);
    }

    @Test
    public void readsCompositeObjectWithRedundantEmptyArrayInSource() {
        Composite b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("x"),
            new StringToken("X"),
            new NameToken("z"),
            new BeginArrayToken(),
            new EndArrayToken(),
            new NameToken("y"),
            new StringToken("Y"),
            new EndObjectToken()
        ), Composite.class);
        Assert.assertEquals(new Composite("X", "Y"), b);
    }

    @Test
    public void readsNestedObject() {
        Nested b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("simple"),
            new BeginObjectToken(),
            new NameToken("x"),
            new StringToken("A"),
            new EndObjectToken(),
            new NameToken("z"),
            new StringToken("B"),
            new EndObjectToken()
        ), Nested.class);
        Assert.assertEquals(new Nested(new Simple("A"), "B"), b);
    }

    @Test
    public void readsObjectWithNullField() {
        Simple b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("x"),
            NullObjectToken.INSTANCE,
            new EndObjectToken()
        ), Simple.class);
        Assert.assertEquals(new Simple(null), b);
    }

    @Test
    public void readsLong() {
        WithLong b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("a"),
            new BigIntegerToken(BigInteger.valueOf(1)),
            new NameToken("b"),
            new BigIntegerToken(BigInteger.valueOf(2)),
            new EndObjectToken()
        ), WithLong.class);
        Assert.assertEquals(new WithLong(1l, 2l), b);
    }

    @Test
    public void readsBoolean() {
        WithBoolean b = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("a"),
            new BooleanToken(true),
            new NameToken("b"),
            new BooleanToken(false),
            new EndObjectToken()
        ), WithBoolean.class);
        Assert.assertEquals(new WithBoolean(true, false), b);
    }

    @Test
    public void readsSet() {
        final WithSet object = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("set"),
            new BeginArrayToken(),
            new StringToken("a"),
            new EndArrayToken(),
            new EndObjectToken()
        ),
                                                       WithSet.class);
        Assert.assertEquals(new WithSet(Sets.newHashSet("a")), object);
    }

    @Test
    public void readsList() {
        final WithList object = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("list"),
            new BeginArrayToken(),
            new StringToken("a"),
            new StringToken("b"),
            new EndArrayToken(),
            new EndObjectToken()
        ),
                                                        WithList.class);
        Assert.assertEquals(new WithList(Arrays.asList("a", "b")), object);
    }

    @Test
    public void readsMap() {
        final WithMap object = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("map"),
            new BeginArrayToken(),
            new StringToken("a"),
            new BigIntegerToken(BigInteger.valueOf(7)),
            new EndArrayToken(),
            new EndObjectToken()
        ),
                                                       WithMap.class);
        Assert.assertEquals(new WithMap(ImmutableMap.of("a", 7l)), object);
    }

    @Test
    public void readsSortedMap() {
        final WithSortedMap object = new MapperFacade().read(new ListTreeReader(
            new BeginObjectToken(),
            new NameToken("map"),
            new BeginArrayToken(),
            new StringToken("a"),
            new BigIntegerToken(BigInteger.valueOf(7)),
            new EndArrayToken(),
            new EndObjectToken()
        ),
                                                             WithSortedMap.class);
        Assert.assertEquals(new WithSortedMap(new TreeMap<>(ImmutableMap.of("a", 7l))), object);
    }

    @Test
    public void readsWithAnnotation() {
        final WithAnnotation object = new MapperFacade().read(new ListTreeReader(),
                                                              WithAnnotation.class);
        Assert.assertEquals(WithAnnotation.CONST, object);
    }
}
