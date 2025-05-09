package io.lettuce.core.dynamic;

import java.util.function.Function;

public interface IConversionService {
    @SuppressWarnings("rawtypes")
    void addConverter(Function<?, ?> converter);

    @SuppressWarnings("unchecked")
    <S, T> T convert(S source, Class<T> targetType);

    <S, T> boolean canConvert(Class<S> sourceType, Class<T> targetType);
}
