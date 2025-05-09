package io.lettuce.core;

public class ILettuceVersion {
    /**
     * Return the library name.
     */
    public static String getName() {
        return "Lettuce";
    }

    /**
     * Return the full version string of the present Lettuce codebase, or {@code null} if it cannot be determined.
     *
     * @see Package#getImplementationVersion()
     */
    public static String getVersion() {
        Package pkg = LettuceVersion.class.getPackage();
        return (pkg != null ? pkg.getImplementationVersion() : null);
    }
}
