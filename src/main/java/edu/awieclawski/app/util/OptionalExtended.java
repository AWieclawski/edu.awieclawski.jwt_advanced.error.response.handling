package edu.awieclawski.app.util;


import java.util.Objects;
import java.util.function.Consumer;

public final class OptionalExtended<T> {
    private static final OptionalExtended<?> EMPTY = new OptionalExtended(null);
    private final T value;

    public static <T> OptionalExtended<T> empty() {
        OptionalExtended<T> t = (OptionalExtended<T>) EMPTY;
        return t;
    }

    private OptionalExtended(T value) {
        this.value = value;
    }

    public static <T> OptionalExtended<T> of(T value) {
        return new OptionalExtended<T>(Objects.requireNonNull(value));
    }

    public static <T> OptionalExtended<T> ofNullable(T value) {
        return value == null ? (OptionalExtended<T>) EMPTY : new OptionalExtended(value);
    }


    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (this.value != null) {
            action.accept(this.value);
        } else {
            emptyAction.run();
        }

    }
}
