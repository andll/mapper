package bo.pic.server.mapper.cglib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that target object to (de)serialize is assumed to be stored at spring context and not static but runtime object
 * type should be processed.
 * <p/>
 * Example:
 * <pre>
 *     &#064;SpringRuntimeLookup interface Service {}
 *     class Service1 implements Service {}
 *     class Service2 implements Service {}
 *     class ToSerialize {
 *         private final Service service;
 *
 *         // ...
 *     }
 * </pre>
 * Here we mark <code>Service</code> interface by this annotation thus instructing serialization engine that all its implementations
 * which might be serialized are assumed to be kept at spring context. That means that information about target implementation type
 * is serialized and corresponding bean is looked up by it on deserialization.
 *
 * @author Denis Zhdanov
 * @since 09/12/13 17:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringRuntimeLookup {
}
