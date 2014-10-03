package bo.pic.server.mapper.mongo;

import bo.pic.server.mapper.Mapper;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.mongodb.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MappedMongoCollection<T> {
    private final DBCollection collection;
    private final Mapper<T>    mapper;

    public MappedMongoCollection(DBCollection collection, Mapper<T> mapper) {
        this.collection = collection;
        this.mapper = mapper;
    }

    public void save(T t) {
        BasicDBObject dbObject = BasicDbObjectTreeWriter.toBasicDBObject(mapper, t);
        collection.insert(dbObject);
    }

    public void upsert(T t) {
        BasicDBObject dbObject = BasicDbObjectTreeWriter.toBasicDBObject(mapper, t);
        collection.update(dbObject, dbObject, true, false);
    }

    public void save(T t, BasicDBObjectConverter converter) {
        BasicDBObject dbObject = BasicDbObjectTreeWriter.toBasicDBObject(mapper, t);
        dbObject = converter.convert(dbObject);
        collection.insert(dbObject);
    }

    public T load(String id) throws NotFoundException {
        return load(new BasicDBObject("_id", id));
    }

    public T load(DBObject query) throws NotFoundException {
        DBObject dbObject = collection.findOne(query);
        if (dbObject == null) {
            throw new NotFoundException();
        }
        return BasicDbObjectTreeReader.fromDBObject(mapper, dbObject);
    }

    public Iterable<T> select(DBObject query) {
        final DBCursor cursor = collection.find(query);
        return Iterables.transform(cursor, new Function<DBObject, T>() {
            @Nullable
            @Override
            public T apply(@Nullable DBObject input) {
                assert input != null;
                return BasicDbObjectTreeReader.fromDBObject(mapper, input);
            }
        });
    }

    public Iterable<T> select(DBObject query, DBObject sort) {
        final DBCursor cursor = collection.find(query).sort(sort);
        return Iterables.transform(cursor, new Function<DBObject, T>() {
            @Nullable
            @Override
            public T apply(@Nullable DBObject input) {
                assert input != null;
                return BasicDbObjectTreeReader.fromDBObject(mapper, input);
            }
        });
    }

    public void remove(BasicDBObject dbObject) {
        collection.remove(dbObject);
    }

    public int count(BasicDBObject dbObject) {
        return collection.find(dbObject).count();
    }

    public static class AddValueConverter implements BasicDBObjectConverter {
        private final String key;
        private final String value;

        public AddValueConverter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Nonnull
        @Override
        public BasicDBObject convert(@Nonnull BasicDBObject dbObject) {
            return dbObject.append(key, value);
        }
    }

    public static class NotFoundException extends RuntimeException {
    }

    public interface BasicDBObjectConverter {
        @Nonnull
        BasicDBObject convert(@Nonnull BasicDBObject dbObject);
    }

    private static DBCollection collectionFor(DB db, Class<?> clazz) {
        MongoCollection collectionName = clazz.getAnnotation(MongoCollection.class);
        if (collectionName == null) {
            throw new IllegalArgumentException("Class " + clazz + " missing " + MongoCollection.class.getName() + " annotation.");
        }
        return db.getCollection(collectionName.value());
    }
}
