package org.soak.plugin;

import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.logging.log4j.Logger;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.soak.map.SoakResourceKeyMap;
import org.soak.wrapper.enchantment.SoakEnchantment;
import org.soak.wrapper.potion.SoakPotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryTypes;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

class SoakRegister {

    /*public static void startEnchantmentTypes(Logger logger) {
        logger.info("Registering Enchantment Types");
        Registry<EnchantmentType> registry = RegistryTypes.ENCHANTMENT_TYPE.get();
        registry.stream().map(SoakEnchantment::new).forEach(soak -> soak.);
        Enchantment.stopAcceptingRegistrations();
        Enchantment.

    }*/

    /*public static void startPotionEffects(Logger logger) {
        logger.info("Registering Potion Effect Types");
        Registry<PotionEffectType> registry = RegistryTypes.POTION_EFFECT_TYPE.get();

        Map<String, Integer> map = Arrays.stream(org.bukkit.potion.PotionEffectType.class.getDeclaredFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()))
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> field.getType().equals(org.bukkit.potion.PotionEffectType.class))
                .collect(Collectors.toMap(field -> {
                    String name = field.getName();
                    if (name.equals("SLOW")) {
                        return "SLOWNESS";
                    }
                    if (name.equals("FAST_DIGGING")) {
                        return "HASTE";
                    }
                    if (name.equals("SLOW_DIGGING")) {
                        return "MINING FATIGUE";
                    }
                    if (name.equals("HEAL")) {
                        return "INSTANT HEALTH";
                    }
                    if (name.equals("JUMP")) {
                        return "JUMP BOOST";
                    }
                    if (name.equals("DAMAGE_RESISTANCE")) {
                        return "RESISTANCE";
                    }
                    if (name.equals("INCREASE_DAMAGE")) {
                        return "INSTANT DAMAGE";
                    }
                    if (name.equals("CONFUSION")) {
                        return "NAUSEA";
                    }
                    return name.replaceAll("_", " ");
                }, field -> {
                    try {
                        //noinspection deprecation
                        return ((org.bukkit.potion.PotionEffectType) field.get(null)).getId();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                }));
        registry
                .stream()
                .map(spongeType -> {
                    double duration = -1; //need to work this one out
                    boolean isInstant = spongeType.isInstant();
                    Color color = Color.RED; //need to work this one out
                    String name = PlainTextComponentSerializer.plainText().serialize(spongeType.asComponent());
                    int id = map.getOrDefault(name.toUpperCase(), 0);
                    String translationkey = ((TranslatableComponent) spongeType.asComponent()).key();
                    NamespacedKey key = SoakResourceKeyMap.mapToBukkit(spongeType.key(RegistryTypes.POTION_EFFECT_TYPE));
                    return new SoakPotionEffectType(duration, isInstant, color, name, translationkey, id, key);
                })
                .forEach(effect -> {
                    while (true) {
                        try {
                            //org.bukkit.potion.PotionEffectType.registerPotionEffectType(effect);
                            break;
                        } catch (IllegalArgumentException e) {
                            //noinspection deprecation
                            effect = new SoakPotionEffectType(effect.getDurationModifier(),
                                    effect.isInstant(),
                                    effect.getColor(),
                                    effect.getName(),
                                    effect.translationKey(),
                                    effect.getId() + 1,
                                    effect.getKey());
                        }
                    }
                });

        //org.bukkit.potion.PotionEffectType.stopAcceptingRegistrations();
    }*/
}
