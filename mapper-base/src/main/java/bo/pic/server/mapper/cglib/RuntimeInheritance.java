package bo.pic.server.mapper.cglib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is not transitive over class inheritance, e.g. if A has @RuntimeInheritance annotation,
 * and B inherits A but do not have explicit @RuntimeInheritance annotation, then fields of type B won't be treated for RuntimeInheritance
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeInheritance {
}
