package io.lettuce.core.dynamic.output;

/**
 * Base class for {@link CommandOutputFactory} resolution such as {@link OutputRegistryCommandOutputFactoryResolver}.
 * <p>
 * This class provides methods to check provider/selector type assignability. Subclasses are responsible for calling methods in
 * this class in the correct order.
 *
 * @author Mark Paluch
 */
public abstract class CommandOutputResolverSupport {

}
