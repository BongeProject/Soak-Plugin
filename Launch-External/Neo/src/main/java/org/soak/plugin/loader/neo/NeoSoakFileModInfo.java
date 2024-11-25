package org.soak.plugin.loader.neo;

import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NeoSoakFileModInfo implements IModFileInfo {

    public static final NeoSoakFileModInfo MOD_INFO = new NeoSoakFileModInfo();

    private final List<IModInfo> soakPluginsCache = new LinkedList<>();

    @Override
    public List<IModInfo> getMods() {
        return Collections.unmodifiableList(this.soakPluginsCache);
    }

    @Override
    public List<LanguageSpec> requiredLanguageLoaders() {
        return Collections.emptyList();
    }

    public void addMod(NeoSoakModInfo info) {
        this.soakPluginsCache.add(info);
    }

    /*@Override
    public String getModLoader() {
        return "soak";
    }

    @Override
    public VersionRange getModLoaderVersion() {
        return VersionRange.createFromVersion("0+");
    }*/

    @Override
    public boolean showAsResourcePack() {
        return false;
    }

    @Override
    public Map<String, Object> getFileProperties() {
        return Collections.emptyMap();
    }

    @Override
    public String getLicense() {
        return "MIT";
    }

    @Override
    public String moduleName() {
        return "soak";
    }

    @Override
    public String versionString() {
        return "1.0.0";
    }

    @Override
    public List<String> usesServices() {
        return Collections.emptyList();
    }

    @Override
    public IModFile getFile() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public IConfigurable getConfig() {
        throw new RuntimeException("not implemented yet");
    }
}
