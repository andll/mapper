package bo.pic.server.mapper;

import bo.pic.server.mapper.cglib.AutoSerializable;

import java.lang.reflect.Type;

public class MapperNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -8207785430988940068L;

    public MapperNotFoundException(Type type) {
        super(String.format(
            "Mapper not found for: %s, have not you forgotten to implement %s to use generated serialization?",
            type, AutoSerializable.class.getSimpleName()));
    }
}
