package org.soak.plugin.loader.neo;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.soak.plugin.SoakPlugin;
import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.loader.common.SoakPluginInjector;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class NeoPluginInjector implements SoakPluginInjector {

    public static void injectPluginToPlatform(SoakPluginContainer container) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        var modContainer = new NeoSoakModContainer(container);
        var modFileInfo = NeoSoakFileModInfo.MOD_INFO;
        var pluginFileScanData = new ModFileScanData();
        pluginFileScanData.addModFileInfo(modFileInfo);

        var modListAccessor = ModList.get();

        var modsField = modListAccessor.getClass().getDeclaredField("mods");
        modsField.setAccessible(true);
        var mods = (List<ModContainer>) modsField.get(modListAccessor);
        mods.add(modContainer);
        modsField.set(modListAccessor, mods);
    }
}
