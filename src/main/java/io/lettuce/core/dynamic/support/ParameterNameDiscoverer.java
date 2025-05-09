package io.lettuce.core.dynamic.support;

import io.lettuce.core.dynamic.parameter.Parameter;
import io.lettuce.core.internal.LettuceAssert;
import io.lettuce.core.internal.LettuceClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Interface to discover parameter names for methods and constructors.
 *
 * <p>
 * Parameter name discovery is not always possible, but various strategies are available to try, such as looking for debug
 * information that may have been emitted at compile time, and looking for argname annotation values.
 */
public interface ParameterNameDiscoverer {

    /**
     * Return parameter names for this method, or {@code null} if they cannot be determined.
     *
     * @param method method to find parameter names for
     * @return an array of parameter names if the names can be resolved, or {@code null} if they cannot
     */
    String[] getParameterNames(Method method);

    /**
     * Return parameter names for this constructor, or {@code null} if they cannot be determined.
     *
     * @param ctor constructor to find parameter names for
     * @return an array of parameter names if the names can be resolved, or {@code null} if they cannot
     */
    String[] getParameterNames(Constructor<?> ctor);

    /**
     * Check whether the parameter is assignable to {@code target}.
     *
     * @param target    must not be {@code null}.
     * @param parameter
     * @return
     */
    default boolean isAssignableTo(Class<?> target, Parameter parameter) {

        LettuceAssert.notNull(target, "Target type must not be null");

        return LettuceClassUtils.isAssignable(target, parameter.getParameterType());
    }
}
