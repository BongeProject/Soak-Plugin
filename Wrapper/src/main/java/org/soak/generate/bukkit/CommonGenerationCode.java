package org.soak.generate.bukkit;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.jar.asm.Opcodes;
import org.spongepowered.api.ResourceKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class CommonGenerationCode {

    static String toName(ResourceKey key) {
        var prefix = key.namespace().equals(ResourceKey.MINECRAFT_NAMESPACE) ? "" : toEnumName(key.namespace() + "_");
        return prefix + toEnumName(key.value());
    }

    static String toEnumName(String name) {
        return name.toUpperCase().replaceAll(" ", "_").replaceAll(Pattern.quote("."), "_");
    }


    static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> callMethod(Class<?> thisClass, DynamicType.Builder<? extends Enum<?>> builder, String method, Class<?> returnType, Class<?>... arguments) throws NoSuchMethodException {
        return callMethod(thisClass, builder, method, returnType, extra -> extra, arguments);
    }

    static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> callMethod(Class<?> thisClass, DynamicType.Builder<? extends Enum<?>> builder, String method, Class<?> returnType, Function<MethodCall, MethodCall> extra, Class<?>... parameters) throws NoSuchMethodException {
        List<Class<?>> arguments = new ArrayList<>();
        arguments.add(Enum.class);
        arguments.addAll(Arrays.asList(parameters));
        var call = extra.apply(MethodCall
                .invoke(thisClass
                        .getMethod(method, arguments.toArray(Class[]::new)))
                .withThis());

        return builder.defineMethod(method, returnType, Opcodes.ACC_PUBLIC)
                .withParameters(parameters)
                .intercept(call);
    }

    static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> callStaticMethodReturnSelf(Class<?> thisClass, DynamicType.Builder<? extends Enum<?>> builder, String method, Class<?>... arguments) throws NoSuchMethodException {
        return callStaticMethodReturnSelf(thisClass, builder, method, extra -> extra, arguments);
    }

    static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> callStaticMethodReturnSelf(Class<?> thisClass, DynamicType.Builder<? extends Enum<?>> builder, String method, Function<MethodCall, MethodCall> extra, Class<?>... parameters) throws NoSuchMethodException {

        var call = extra.apply(MethodCall
                .invoke(thisClass
                        .getMethod(method, parameters))).withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);
        return builder.defineMethod(method, builder.toTypeDescription(), Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC)
                .withParameters(parameters)
                .intercept(call);
    }
}
