package org.soak.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CachedClassMap<M> implements Map<Class<?>, M> {

    private final Map<Class<?>, M> classMap = new HashMap<>();
    private final Map<String, Class<?>> stringMap = new HashMap<>();

    @Override
    public int size() {
        return this.classMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.classMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        if (!(o instanceof Class<?>)) {
            return false;
        }
        String clazzName = ((Class<?>) o).getName();
        if (this.stringMap.containsKey(clazzName)) {
            return true;
        }
        Optional<Class<?>> opClazz = this.classMap.keySet().stream().filter(t -> ((Class<?>) o).isAssignableFrom(t)).findAny();
        if (opClazz.isPresent()) {
            this.stringMap.put(opClazz.get().getName(), opClazz.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        return this.classMap.containsValue(o);
    }

    @Override
    public M get(Object o) {
        if (!(o instanceof Class<?>)) {
            return null;
        }
        String className = getCachedKey(((Class<?>) o));
        if (className == null) {
            return null;
        }
        return this.classMap.get(className);
    }

    public @Nullable String getCachedKey(Class<?> o) {
        String className = o.getName();
        Class<?> classMap = this.stringMap.get(className);
        if (classMap != null) {
            return className;
        }
        Optional<Class<?>> opClass = this.classMap.keySet().stream().filter(v -> ((Class<?>) o).isAssignableFrom(v)).findAny();
        if (opClass.isEmpty()) {
            return null;
        }
        this.stringMap.put(className, opClass.get());
        return opClass.get().getName();
    }

    @Override
    public @Nullable M put(Class<?> aClass, M m) {
        M mapped = this.classMap.put(aClass, m);
        this.stringMap.put(aClass.getName(), aClass);
        return mapped;
    }

    @Override
    public M remove(Object o) {
        if (!(o instanceof Class<?>)) {
            return null;
        }
        String cachedKey = getCachedKey((Class<?>) o);
        Class<?> clazz = this.stringMap.get(cachedKey);
        this.stringMap.remove(cachedKey);
        return this.classMap.remove(clazz);
    }

    @Override
    public void putAll(@NotNull Map<? extends Class<?>, ? extends M> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        this.stringMap.clear();
        this.classMap.clear();
    }

    @Override
    public @NotNull Set<Class<?>> keySet() {
        return this.classMap.keySet();
    }

    @Override
    public @NotNull Collection<M> values() {
        return this.classMap.values();
    }

    @Override
    public @NotNull Set<Entry<Class<?>, M>> entrySet() {
        return this.classMap.entrySet();
    }
}
