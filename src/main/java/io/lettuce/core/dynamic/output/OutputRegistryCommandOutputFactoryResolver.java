package io.lettuce.core.dynamic.output;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.lettuce.core.dynamic.support.ClassTypeInformation;
import io.lettuce.core.dynamic.support.ResolvableType;
import io.lettuce.core.internal.LettuceAssert;
import io.lettuce.core.output.CommandOutput;

/**
 * {@link CommandOutputFactoryResolver} using {@link OutputRegistry} to resolve a {@link CommandOutputFactory}.
 * <p>
 * Types registered in {@link OutputRegistry} are inspected for the types they produce and matched with the declared repository
 * method. If resolution yields multiple {@link CommandOutput}s, the first matched output is used.
 *
 * @author Mark Paluch
 * @since 5.0
 * @see OutputRegistry
 */
public class OutputRegistryCommandOutputFactoryResolver extends CommandOutputResolverSupport
        implements CommandOutputFactoryResolver {

    @SuppressWarnings("rawtypes")
    private static final ClassTypeInformation<CommandOutput> COMMAND_OUTPUT = ClassTypeInformation.from(CommandOutput.class);

    private final OutputRegistry outputRegistry;

    /**
     * Create a new {@link OutputRegistryCommandOutputFactoryResolver} given {@link OutputRegistry}.
     *
     * @param outputRegistry must not be {@code null}.
     */
    public OutputRegistryCommandOutputFactoryResolver(OutputRegistry outputRegistry) {

        LettuceAssert.notNull(outputRegistry, "OutputRegistry must not be null");

        this.outputRegistry = outputRegistry;
    }

    @Override
    public CommandOutputFactory resolveCommandOutput(OutputSelector outputSelector) {

        Map<OutputType, CommandOutputFactory> registry = outputRegistry.getRegistry();

        List<OutputType> outputTypes = registry.keySet().stream().filter((outputType) -> !outputType.isStreaming())
                .collect(Collectors.toList());

        List<OutputType> candidates = getCandidates(outputTypes, outputSelector);

        if (candidates.isEmpty()) {
            return null;
        }

        return registry.get(candidates.get(0));
    }

    @Override
    public CommandOutputFactory resolveStreamingCommandOutput(OutputSelector outputSelector) {

        Map<OutputType, CommandOutputFactory> registry = outputRegistry.getRegistry();

        List<OutputType> outputTypes = registry.keySet().stream().filter(OutputType::isStreaming).collect(Collectors.toList());

        List<OutputType> candidates = getCandidates(outputTypes, outputSelector);

        if (candidates.isEmpty()) {
            return null;
        }

        return registry.get(candidates.get(0));
    }

    private List<OutputType> getCandidates(Collection<OutputType> outputTypes, OutputSelector outputSelector) {

        return outputTypes.stream().filter(outputType -> {

            if (COMMAND_OUTPUT.getType().isAssignableFrom(outputSelector.getOutputType().getRawClass())) {

                if (outputSelector.getOutputType().getRawClass().isAssignableFrom(outputType.getCommandOutputClass())) {
                    return true;
                }
            }

            return isAssignableFrom(outputSelector, outputType);
        }).collect(Collectors.toList());
    }

    /**
     * Overridable hook to check whether {@code selector} can be assigned from the provider type {@code provider}.
     * <p>
     * This method descends the component type hierarchy and considers primitive/wrapper type conversion.
     *
     * @param selector must not be {@code null}.
     * @param provider must not be {@code null}.
     * @return {@code true} if selector can be assigned from its provider type.
     */
    protected boolean isAssignableFrom(OutputSelector selector, OutputType provider) {

        ResolvableType selectorType = selector.getOutputType();
        ResolvableType resolvableType = provider.withCodec(selector.getRedisCodec());

        return selectorType.isAssignableFrom(resolvableType);
    }
}
