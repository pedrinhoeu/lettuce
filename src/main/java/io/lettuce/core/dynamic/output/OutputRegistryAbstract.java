package io.lettuce.core.dynamic.output;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.dynamic.support.ResolvableType;
import io.lettuce.core.dynamic.support.TypeInformation;
import io.lettuce.core.output.CommandOutput;

import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface OutputRegistryAbstract {
    Map<OutputType, CommandOutputFactory> BUILTIN = new LinkedHashMap<>();

    <T extends CommandOutput<?, ?, ?>> void register(Class<T> commandOutputClass,
                                                     CommandOutputFactory commandOutputFactory);

    @SuppressWarnings("serial")
    public static class CodecVariableTypeResolver implements ResolvableType.VariableResolver {

        private final TypeInformation<?> codecType;

        private final List<TypeInformation<?>> typeArguments;

        public CodecVariableTypeResolver(TypeInformation<?> codecType) {

            this.codecType = codecType.getSuperTypeInformation(RedisCodec.class);
            this.typeArguments = this.codecType.getTypeArguments();
        }

        @Override
        public Object getSource() {
            return codecType;
        }

        @Override
        public ResolvableType resolveVariable(TypeVariable<?> variable) {

            if (variable.getName().equals("K")) {
                return ResolvableType.forClass(typeArguments.get(0).getType());
            }

            if (variable.getName().equals("V")) {
                return ResolvableType.forClass(typeArguments.get(1).getType());
            }
            return null;
        }

    }
}
