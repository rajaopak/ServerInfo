package id.rajaopak.serverinfo.util;

import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.reflection.qual.ForName;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class Nms {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final String PREFIX_NMS = "net.minecraft.server";
    private static final String PREFIX_CRAFTBUKKIT = "org.bukkit.craftbukkit";
    private static final String CRAFT_SERVER = "CraftServer";
    private static final @Nullable String VERSION;

    static {
        final Class<?> serverClass = Bukkit.getServer().getClass(); // TODO: use reflection here too?
        if (!serverClass.getSimpleName().equals(CRAFT_SERVER)) {
            VERSION = null;
        } else if (serverClass.getName().equals(PREFIX_CRAFTBUKKIT + "." + CRAFT_SERVER)) {
            VERSION = ".";
        } else {
            String name = serverClass.getName();
            name = name.substring(PREFIX_CRAFTBUKKIT.length());
            name = name.substring(0, name.length() - CRAFT_SERVER.length());
            VERSION = name;
        }
    }

    public static @NonNull Class<?> needNMSClassOrElse(
            final @NonNull String nms,
            final @NonNull String... classNames
    ) throws RuntimeException {
        final Class<?> nmsClass = findNmsClass(nms);
        if (nmsClass != null) {
            return nmsClass;
        }
        for (final String name : classNames) {
            final Class<?> maybe = findClass(name);
            if (maybe != null) {
                return maybe;
            }
        }
        throw new IllegalStateException(String.format(
                "Couldn't find a class! NMS: '%s' or '%s'.",
                nms,
                Arrays.toString(classNames)
        ));
    }

    @ForName
    public static @Nullable Class<?> findClass(final @NonNull String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }

    public static @Nullable String findNmsClassName(final @NonNull String className) {
        return isCraftBukkit() ? PREFIX_NMS + VERSION + className : null;
    }

    @ForName
    public static @Nullable Class<?> findNmsClass(final @NonNull String className) {
        final String nmsClassName = findNmsClassName(className);
        if (nmsClassName == null) {
            return null;
        }

        return findClass(nmsClassName);
    }

    public static @NonNull MethodHandle needStaticMethod(final @NonNull Class<?> holderClass, final @NonNull String methodName, final @NonNull Class<?> returnClass, final @NonNull Class<?> @NonNull ... parameterClasses) {
        return Objects.requireNonNull(
                findStaticMethod(holderClass, methodName, returnClass, parameterClasses),
                String.format(
                        "Could not locate static method '%s' in class '%s'",
                        methodName,
                        holderClass.getCanonicalName()
                )
        );
    }

    public static @NonNull Field needField(final @NonNull Class<?> holderClass, final @NonNull String fieldName) {
        final Field field;
        try {
            field = holderClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (final NoSuchFieldException e) {
            throw new IllegalStateException(String.format("Unable to find field '%s' in class '%s'", fieldName, holderClass.getCanonicalName()), e);
        }
    }

    public static @Nullable Object invokeOrThrow(final @NonNull MethodHandle methodHandle, final @Nullable Object @NonNull ... params) {
        try {
            if (params.length == 0) {
                return methodHandle.invoke();
            }
            return methodHandle.invokeWithArguments(params);
        } catch (final Throwable throwable) {
            throw new IllegalStateException(String.format("Unable to invoke method with args '%s'", Arrays.toString(params)), throwable);
        }
    }

    public static @Nullable MethodHandle findStaticMethod(final @Nullable Class<?> holderClass, final String methodName, final @Nullable Class<?> returnClass, final Class<?>... parameterClasses) {
        if (holderClass == null || returnClass == null) return null;
        for (final Class<?> parameterClass : parameterClasses) {
            if (parameterClass == null) return null;
        }

        try {
            return LOOKUP.findStatic(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
    }

    public static boolean isCraftBukkit() {
        return VERSION != null;
    }

}
