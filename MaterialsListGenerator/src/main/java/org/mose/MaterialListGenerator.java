package org.mose;

import org.bukkit.Material;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.lang.RuntimeException;

public class MaterialListGenerator {

    public static final String PATH_TO_CURRENT_MATERIAL_LIST = "../VanillaMaterials/src/main/java/org/bukkit/Material.java";

    public static boolean shouldPrint(Material material) {
        if (PATH_TO_CURRENT_MATERIAL_LIST == null) {
            return true;
        }
        try {
            return Files.lines(new File(PATH_TO_CURRENT_MATERIAL_LIST).toPath()).noneMatch(line -> line.trim().startsWith(material.name() + "("));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        for (Material material : Material.values()) {
            if (material.isLegacy()) {
                continue;
            }
            /*String blockType = "null";
            if (material.isBlock()) {
                blockType = "BlockTypes." + material.name();
            }

            String itemType = "null";
            if (material.isItem()) {
                itemType = "ItemTypes." + material.name();
            }*/

            String blockType = "BlockTypes." + material.name();
            String itemType = "ItemTypes." + material.name();

            if (MaterialListGenerator.shouldPrint(material)) {
                System.out.println(material.name() + "(" + blockType + ", " + itemType + "),");
            }
        }
    }
}
