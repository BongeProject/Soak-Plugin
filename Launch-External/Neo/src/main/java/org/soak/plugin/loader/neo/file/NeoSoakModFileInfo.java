package org.soak.plugin.loader.neo.file;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforgespi.language.IConfigurable;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.utils.Singleton;

import java.util.Collection;

public class NeoSoakModFileInfo extends ModFileInfo {

    public NeoSoakModFileInfo(NeoSoakModFile file, Collection<SoakPluginContainer> plugins) {
        this(file, new NeoSoakModFileLoaderConfig(plugins));
    }

    private NeoSoakModFileInfo(NeoSoakModFile file, IConfigurable config) {
        super(file, config, (info) -> {
        });
    }
}
