package bo.pic.server.mapper.cglib;

import bo.pic.server.mapper.*;
import bo.pic.server.mapper.tokens.*;
import com.google.common.base.Defaults;
import com.google.common.base.Function;
import com.google.common.collect.*;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class CglibMapper<T> implements Mapper<T> {
    public static final Object   NOT_SET     = new Object();
    public static final Object[] EMPTY_ARRAY = new Object[0];
    private final Map<String, FieldAccessor> accessors;
    private final FastClass                  fastClass;
    private final ConstructorBinding         constructorBinding;

    public CglibMapper(@Nonnull final Class<T> clazz, @Nonnull MappingContext context) {
        this.fastClass = FastClassRegistry.asFastClass(clazz);
        List<Field> fields = getDeclaredFields(clazz);
        this.constructorBinding = lookupBoundConstructor(clazz, fields);
        this.accessors = createAccessors(context, fields);
    }

    private Map<String, FieldAccessor> createAccessors(MappingContext context, List<Field> fields) {
        Map<String, FieldAccessor> accessors = new HashMap<>(fields.size());
        for (Field field : fields) {
            Type fieldType = field.getGenericType();
            Mapper mapper = context.mapperFor(fieldType);
            accessors.put(field.getName(), new FieldAccessor(field, mapper));
        }
        return Collections.unmodifiableMap(accessors);
    }

    public CglibMapper(@Nonnull final Class<T> clazz, @Nonnull MappingContext context, @Nonnull ConstructorBinding constructorBinding) {
        this.constructorBinding = constructorBinding;
        List<Field> fields = getDeclaredFields(clazz);
        this.fastClass = FastClassRegistry.asFastClass(clazz);
        this.accessors = createAccessors(context, fields);
    }

    @Nonnull
    private ConstructorBinding lookupBoundConstructor(@Nonnull Class<T> clazz, @Nonnull List<Field> fields) {
        Set<ConstructorBinding> argBindings = new HashSet<>();
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getAnnotation(DeserializingConstructor.class) != null) {
                return bindConstructorByAnnotations(constructor, fields);
            }
        }
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            ConstructorBinding constructorBinding = tryBindConstructorByArgType(constructor, fields);
            if (constructorBinding != null) {
                argBindings.add(constructorBinding);
            }
        }
        if (argBindings.isEmpty()) {
            throw new IllegalArgumentException("No argument matching binding found for : " + clazz);
        } else if (argBindings.size() == 1) {
            return argBindings.iterator().next();
        } else {
            throw new IllegalArgumentException("Multiple matching binding by arguments found: " + argBindings);
        }
    }

    @Nullable
    private ConstructorBinding tryBindConstructorByArgType(@Nonnull Constructor constructor, @Nonnull List<Field> fields) {
        BiMap<String, Integer> fieldToArgIndex = HashBiMap.create();
        Set<Type> types = Sets.newHashSet(constructor.getGenericParameterTypes());
        if (types.size() != constructor.getGenericParameterTypes().length) {
            return null;
        }
        List<Type> argList = Arrays.asList(constructor.getGenericParameterTypes());
        for (Field field : fields) {
            if (!types.remove(field.getGenericType())) {
                return null;
            }
            fieldToArgIndex.put(field.getName(), argList.indexOf(field.getGenericType()));
        }
        if (types.isEmpty()) {
            return new ConstructorBinding(fieldToArgIndex, fastClass.getConstructor(constructor));
        }
        return null;
    }

    @Nonnull
    private ConstructorBinding bindConstructorByAnnotations(@Nonnull Constructor constructor, @Nonnull List<Field> fields) {
        BiMap<String, Integer> fieldToArgIndex = HashBiMap.create();
        Set<String> fieldNames = Sets.newHashSet(Iterables.transform(fields, new Function<Field, String>() {
            @Override
            public String apply(Field input) {
                return input.getName();
            }
        }));
        for (int i = 0; i < constructor.getParameterAnnotations().length; i++) {
            Annotation[] annotations = constructor.getParameterAnnotations()[i];
            String fieldName = null;
            for (Annotation annotation : annotations) {
                if (annotation instanceof FieldRef) {
                    fieldName = ((FieldRef) annotation).value();
                    break;
                }
            }
            if (fieldName == null) {
                throw new IllegalArgumentException("FieldRef annotation was not found for one of parameters: " + constructor);
            }
            if (!fieldNames.remove(fieldName)) {
                throw new IllegalArgumentException("FieldRef of parameter references to '" + fieldName + "', which is not found in type " +
                                                   fastClass.getJavaClass().getName() + ", or duplicate parameter-field mapping found");
            }
            fieldToArgIndex.put(fieldName, i);
        }
        return new ConstructorBinding(fieldToArgIndex, fastClass.getConstructor(constructor));
    }

    @Nonnull
    private static List<Field> getDeclaredFields(@Nonnull Class clazz) {
        ArrayList<Field> fields = new ArrayList<>(clazz.getDeclaredFields().length);
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    @Override
    public void write(@Nonnull TreeWriter writer, @Nullable T object) {
        if (object == null) {
            writer.append(NullObjectToken.INSTANCE);
            return;
        }
        writer.append(new BeginObjectToken());
        for (Map.Entry<String, FieldAccessor> entry : accessors.entrySet()) {
            writer.append(new NameToken(entry.getKey()));
            entry.getValue().write(writer, object);
        }
        writer.append(new EndObjectToken());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public T read(@Nonnull TreeReader reader) {
        final Token token = reader.nextToken();
        if (token instanceof NullObjectToken) {
            return null;
        }
        if (!(token instanceof BeginObjectToken)) {
            throw new IllegalArgumentException("Unexpected token: " + token);
        }
        final Object[] valueMap = new Object[accessors.size()];
        Arrays.fill(valueMap, NOT_SET);
        new NameValueReader(reader) {
            @Override
            protected void handle(String name) {
                FieldAccessor accessor = accessors.get(name);
                if (accessor == null) {
                    skipValue();
                    return;
                }
                Integer idx = constructorBinding.getFieldToArgIndex().get(name);
                if (idx == null) {
                    throw new IllegalArgumentException(String.format("There is no accessor for %s in %s",
                                                                     name, fastClass.getJavaClass().getName()));
                }
                if (valueMap[idx] != NOT_SET) {
                    throw new IllegalArgumentException(String.format("Field %s occurred twice in stream", name));
                }
                valueMap[idx] = accessor.readValue(reader);
            }
        }.readUntilEndOfObject();
        for (int i = 0; i < valueMap.length; i++) {
            if (valueMap[i] == NOT_SET) {
                valueMap[i] = defaultValue(accessors.get(constructorBinding.getFieldToArgIndex().inverse().get(i)));
            }
        }
        try {
            return (T) constructorBinding.getConstructor().newInstance(valueMap);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Can not create instance of %s with args %s",
                                                     fastClass.getJavaClass().getName(), Arrays.toString(valueMap)), e);
        }
    }

    @Nullable
    private final Java8OptionalMapperProvider java8OptionalMapperProvider = Java8OptionalMapperProvider.tryCreate();

    private Object defaultValue(FieldAccessor fieldAccessor) {
        Type type = fieldAccessor.field.getGenericType();
        if (type instanceof Class) {
            Class clazz = (Class) type;
            if (clazz.isPrimitive()) {
                return Defaults.defaultValue(clazz);
            }
        }
        if (fieldAccessor.field.getAnnotation(Nullable.class) != null) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType == Set.class) {
                return Collections.emptySet();
            }
            if (rawType == List.class) {
                return Collections.emptyList();
            }
            if (rawType == Map.class) {
                return Collections.emptyMap();
            }
            if (rawType == SortedMap.class) {
                return ImmutableSortedMap.of();
            }
            if (java8OptionalMapperProvider != null && java8OptionalMapperProvider.isOptionalClass((Class) rawType)) {
                return java8OptionalMapperProvider.getDefaultValue();
            }
        }
        return null;
    }

    private class FieldAccessor {
        private final Field      field;
        private final FastMethod getter;
        private final Mapper     mapper;

        private FieldAccessor(Field field, Mapper mapper) {
            this.getter = findGetter(field);
            this.field = field;
            this.mapper = mapper;
        }

        @SuppressWarnings("unchecked")
        public void write(@Nonnull TreeWriter writer, @Nonnull T t) {
            Object value;
            try {
                value = getter.invoke(t, EMPTY_ARRAY);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            mapper.write(writer, value);
        }

        public Object readValue(@Nonnull TreeReader reader) {
            return mapper.read(reader);
        }

        @Override
        public String toString() {
            return field.getName();
        }
    }

    @SuppressWarnings("unchecked")
    private FastMethod findGetter(Field field) {
        String name = field.getName();
        char[] camelName = name.toCharArray();
        camelName[0] = Character.toUpperCase(camelName[0]);
        Set<String> candidates;
        if (field.getType() == boolean.class) {
            candidates = Sets.newHashSet("is" + new String(camelName), "get" + new String(camelName));
        } else {
            candidates = Sets.newHashSet("get" + new String(camelName));
        }
        Map<String, GetterFor> methodAnnotations = new HashMap<>();
        for (Method method : fastClass.getJavaClass().getMethods()) {
            GetterFor getterFor = method.getAnnotation(GetterFor.class);
            if (!matches(candidates, method, name, getterFor)) {
                continue;
            }
            //Record annotation, so if we found matching method in interface we can use annotation from derived class
            methodAnnotations.put(method.getName(), getterFor);
            if (fastClass.getIndex(method.getName(), method.getParameterTypes()) >= 0) {
                return fastClass.getMethod(method);
            }
        }
        //Iterate over parent classes to find appropriate getter, this is workaround to bug in cglib at EmitUtil.java:800
        for (Class f : collectParentClasses()) {
            for (Method method : f.getMethods()) {
                GetterFor getterFor = methodAnnotations.get(method.getName());
                if (getterFor == null) {
                    getterFor = method.getAnnotation(GetterFor.class);
                }
                if (!matches(candidates, method, name, getterFor)) {
                    continue;
                }
                FastClass fc = FastClassRegistry.asFastClass(f);
                if (fc.getIndex(method.getName(), method.getParameterTypes()) >= 0) {
                    return fc.getMethod(method);
                }
            }
        }
        throw new IllegalArgumentException("Can not find getter for " + field +
                                           ". Create getter with one of following names: " + candidates +
                                           ", or mark getter with @GetterFor(\"" + name + "\") annotation");
    }

    private Set<Class> collectParentClasses() {
        Set<Class> parents = Sets.newHashSet(fastClass.getJavaClass().getInterfaces());
        Class superClass = fastClass.getJavaClass().getSuperclass();
        while (superClass != Object.class) {
            parents.add(superClass);
            superClass = superClass.getSuperclass();
        }
        return parents;
    }

    private boolean matches(@Nonnull Set<String> candidates, @Nonnull Method method, @Nonnull String fieldName,
                            @Nullable GetterFor getterFor) {
        if (method.getParameterTypes().length != 0 || Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        if (getterFor != null && getterFor.value().equals(fieldName)) {
            return true;
        }
        return candidates.contains(method.getName());
    }
}
