package io.lettuce.core.dynamic.output;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.dynamic.support.ResolvableType;

public interface IOutputSelector {
    ResolvableType getOutputType();

    RedisCodec<?, ?> getRedisCodec();
}
