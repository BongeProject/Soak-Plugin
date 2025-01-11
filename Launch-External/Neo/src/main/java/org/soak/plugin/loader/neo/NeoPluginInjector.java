package org.soak.plugin.loader.neo;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.plugin.loader.neo.file.NeoSoakModFile;
import org.soak.utils.Singleton;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NeoPluginInjector {

    public static Singleton<ModFileInfo> SOAK_LOADER_FILE_INFO = new Singleton<>(() -> {
        var container = SoakManager.getManager().getOwnContainer();
        return (ModFileInfo) ModList
                .get()
                .getModFiles()
                .stream()
                .filter(info -> info
                        .getMods()
                        .stream()
                        .anyMatch(mod -> mod
                                .getModId()
                                .equals(container.metadata().id())))
                .findAny()
                .orElseThrow();
    });

    public static void injectPluginToPlatform(Collection<SoakPluginContainer> containers) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        var loader = new NeoSoakModFile(SOAK_LOADER_FILE_INFO.get().getFile().getFilePath(), containers);

        var modList = ModList.get();
        var modFiles = new ArrayList<>(modList.getSortedMods());

        var modContainers = loader
                .getModInfos()
                .stream()
                .map(modInfo -> {
                    var soakContainer = containers
                            .stream()
                            .filter(container -> container
                                    .metadata()
                                    .id()
                                    .equals(modInfo.getModId()))
                            .findAny()
                            .orElseThrow(() -> new RuntimeException("Soak loaded mod '" + modInfo.getModId() + "' but no plugin is attached"));
                    return new NeoSoakModContainer(modInfo, soakContainer);
                })
                .filter(container -> modFiles
                        .stream()
                        .map(ModContainer::getModId)
                        .noneMatch(modFileId -> container.metadata().id().equals(modFileId)))
                .toList();

        modFiles.addAll(modContainers);


        var method = ModList.class.getDeclaredMethod("setLoadedMods", List.class);
        method.setAccessible(true);
        method.invoke(modList, modFiles);
    }
}
