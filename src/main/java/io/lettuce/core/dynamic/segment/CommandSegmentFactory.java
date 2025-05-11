package io.lettuce.core.dynamic.segment;

import io.lettuce.core.dynamic.CommandMethod;
import io.lettuce.core.dynamic.annotation.CommandNaming;

/**
 * Strategy interface to create {@link CommandSegments} for a {@link CommandMethod}.
 *
 * @author Mark Paluch
 * @since 5.0
 */
public interface CommandSegmentFactory {

    /**
     * Create {@link CommandSegments} for a {@link CommandMethod}.
     *
     * @param commandMethod must not be {@code null}.
     * @return the {@link CommandSegments}.
     */
    CommandSegments createCommandSegments(CommandMethod commandMethod);

    default CommandNaming.LetterCase getLetterCase(CommandMethod commandMethod) {

        if (commandMethod.hasAnnotation(CommandNaming.class)) {
            CommandNaming.LetterCase letterCase = commandMethod.getMethod().getAnnotation(CommandNaming.class).letterCase();
            if (letterCase != CommandNaming.LetterCase.DEFAULT) {
                return letterCase;
            }
        }

        Class<?> declaringClass = commandMethod.getMethod().getDeclaringClass();
        CommandNaming annotation = declaringClass.getAnnotation(CommandNaming.class);
        if (annotation != null && annotation.letterCase() != CommandNaming.LetterCase.DEFAULT) {
            return annotation.letterCase();
        }

        return CommandNaming.LetterCase.UPPERCASE;
    }
}
