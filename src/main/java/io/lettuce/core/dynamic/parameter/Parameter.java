package io.lettuce.core.dynamic.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.lettuce.core.dynamic.support.*;

/**
 * Abstracts a method parameter and exposes access to type and parameter information.
 *
 * @author Mark Paluch
 * @since 5.0
 */
public class Parameter {

    public final ParameterNameDiscoverer discoverer = new CompositeParameterNameDiscoverer(
            new StandardReflectionParameterNameDiscoverer(), new AnnotationParameterNameDiscoverer());

    private final Method method;

    private final String name;

    private final int parameterIndex;

    private final TypeInformation<?> typeInformation;

    private final MethodParameter methodParameter;

    private final Map<Class<? extends Annotation>, Annotation> annotationCache = new ConcurrentHashMap<>();

    private final Set<Class<? extends Annotation>> absentCache = ConcurrentHashMap.newKeySet();

    private final List<Annotation> annotations;

    public Parameter(Method method, int parameterIndex) {

        this.method = method;
        this.parameterIndex = parameterIndex;
        this.methodParameter = new MethodParameter(method, parameterIndex);
        this.methodParameter.initParameterNameDiscovery(discoverer);
        this.name = methodParameter.getParameterName();
        this.typeInformation = ClassTypeInformation.fromMethodParameter(method, parameterIndex);

        Annotation[] annotations = method.getParameterAnnotations()[parameterIndex];
        List<Annotation> allAnnotations = new ArrayList<>(annotations.length);

        for (Annotation annotation : annotations) {
            this.annotationCache.put(annotation.getClass(), annotation);
            allAnnotations.add(annotation);
        }
        this.annotations = Collections.unmodifiableList(allAnnotations);
    }

    /**
     * Return the parameter annotation of the given type, if available.
     *
     * @param annotationType the annotation type to look for
     * @return the annotation object, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {

        if (absentCache.contains(annotationType)) {
            return null;
        }

        A result = (A) annotationCache.computeIfAbsent(annotationType,
                key -> methodParameter.getParameterAnnotation(annotationType));

        if (result == null) {
            absentCache.add(annotationType);
        }

        return result;
    }

    /**
     * Return all parameter annotations.
     *
     * @return the {@link List} of annotation objects.
     */
    public List<? extends Annotation> getAnnotations() {
        return annotations;
    }

    /**
     *
     * @return the parameter index.
     */
    public int getParameterIndex() {
        return parameterIndex;
    }

    /**
     *
     * @return the parameter type.
     */
    public Class<?> getParameterType() {
        return method.getParameterTypes()[parameterIndex];
    }

    /**
     *
     * @return the parameter {@link TypeInformation}.
     */
    public TypeInformation<?> getTypeInformation() {
        return typeInformation;
    }

    /**
     *
     * @return {@code true} if the parameter is a special parameter.
     */
    public boolean isSpecialParameter() {
        return false;
    }

    /**
     * @return {@code true} if the {@link Parameter} can be bound to a command.
     */
    boolean isBindable() {
        return !isSpecialParameter();
    }

    /**
     * @return the parameter name or {@code null} if not available.
     */
    public String getName() {
        return name;
    }

}
