package org.soak.data.type;

import java.util.HashMap;
import java.util.Map;

public class BukkitDataTypes {

    public static final Map<Class<?>, BukkitDataType<?>> TYPES = new HashMap<>();

    public static final BukkitStringDataType STRING = register(new BukkitStringDataType());
    public static final BukkitByteDataType BYTE = register(new BukkitByteDataType());
    public static final BukkitBooleanDataType BOOLEAN = register(new BukkitBooleanDataType());


    private static <T extends BukkitDataType<?>> T register(T type) {
        TYPES.put(type.typeClass(), type);
        return type;
    }
}
