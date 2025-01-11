package org.soak.exception;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NotImplementedException extends RuntimeException {

    private final Method interfaceMethod;

    public NotImplementedException(Class<?> from, String methodName, Class<?>... parameters) throws NoSuchMethodException {
        this(from.getDeclaredMethod(methodName, parameters));
    }

    public NotImplementedException(Method interfaceMethod) {
        super("The method is not implemented: " + interfaceMethod.getDeclaringClass().getName() + "." + interfaceMethod.getName() + "(" + Arrays.stream(interfaceMethod.getParameters()).map(parameter -> parameter.getType().getSimpleName() + " " + parameter.getName()).collect(Collectors.joining(", ")) + ")");
        this.interfaceMethod = interfaceMethod;
    }

    public static NotImplementedException createByLazy(Object fromObject, String name, Class<?>... parameters) {
        Class<?> clazz = fromObject.getClass();
        while (!clazz.equals(Object.class)) {
            clazz = clazz.getSuperclass();
            try {
                return new NotImplementedException(clazz.getDeclaredMethod(name, parameters));
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        //fail safe
        throw new RuntimeException("The method is not implemented " + fromObject.getClass().getName() + "." + name + "(" + Arrays.stream(parameters).map(parameter -> parameter.getSimpleName() + " arg").collect(Collectors.joining(", ")) + ")");
    }

    public static NotImplementedException createByLazy(Class<?> from, String name, Class<?>... parameters) {
        try {
            throw new NotImplementedException(from, name, parameters);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create NotImplementedException for " + from.getSimpleName() + "#" + name + "(" + Arrays.stream(parameters).map(parameter -> parameter.getSimpleName() + " arg").collect(Collectors.joining(", ")) + ")", e);
        }
    }

    @Deprecated(forRemoval = true)
    public static NotImplementedException createByLazy(Class<?>... parameterTypes) {
        StackTraceElement stacktrace = Thread.currentThread().getStackTrace()[0];
        String className = stacktrace.getClassName();
        String methodName = stacktrace.getMethodName();
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find class when creating NotImplementedException", e);
        }

        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            return new NotImplementedException(method);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find method when creating NotImplementedException: " + methodName + " With " + Arrays.stream(parameterTypes).map(Class::getSimpleName).collect(Collectors.joining(", ")), e);
        }
    }

    public Method unimplementedMethod() {
        return this.interfaceMethod;
    }
}
