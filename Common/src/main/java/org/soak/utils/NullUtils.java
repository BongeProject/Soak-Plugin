package org.soak.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class NullUtils {

    public static <T, I> T mapTo(@Nullable I incoming, @NotNull Function<I, T> present, @NotNull Supplier<T> to) {
        if (incoming == null) {
            return to.get();
        }
        return present.apply(incoming);
    }
}
