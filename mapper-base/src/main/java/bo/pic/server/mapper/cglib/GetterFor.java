package bo.pic.server.mapper.cglib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If getter name does not follow naming convention, this annotation may be used to indicate that given method is getter for given field
 * Annotation on child-class methods has higher priority then on parent-class methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetterFor {
    String value();
}
