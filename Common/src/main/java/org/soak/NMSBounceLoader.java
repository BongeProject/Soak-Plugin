package org.soak;

import org.jetbrains.annotations.Nullable;
import org.soak.plugin.SoakManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class NMSBounceLoader {

    private static final File EXTRACTED_PATH = new File("libraries/org/soak/nms/NMSBounce.jar");
    private static final String INTERNAL_PATH = "NMSBounce.jar";
    private static @Nullable NMSBounceLoader loader;
    private @Nullable URLClassLoader classLoader;
    private Set<Class<?>> classes = new HashSet<>();

    public static NMSBounceLoader getLoader() {
        if (loader == null) {
            loader = new NMSBounceLoader();
        }
        return loader;
    }

    public boolean hasNMSBounce() {
        return SoakManager.getManager().getOwnContainer().openResource(INTERNAL_PATH).isPresent();
    }

    public boolean canLoad() {
        return EXTRACTED_PATH.exists();
    }

    public synchronized void extractNmsBounce() throws IOException {
        var is = SoakManager.getManager().getOwnContainer().openResource(INTERNAL_PATH).orElseThrow();
        EXTRACTED_PATH.getParentFile().mkdirs();
        if (!EXTRACTED_PATH.exists()) {
            Files.createFile(EXTRACTED_PATH.toPath());
        }
        var os = new FileOutputStream(EXTRACTED_PATH);
        is.transferTo(os);
    }

    public Optional<URLClassLoader> classLoader() {
        return Optional.ofNullable(this.classLoader);
    }

    public void loadNMSBounce() throws IOException {
        if (!canLoad()) {
            throw new IllegalStateException("run extractNmsBounce() first");
        }
        Optional<URLClassLoader> opLoader = classLoader();
        if (opLoader.isPresent()) {
            throw new IllegalStateException("classLoader already active");
        }
        URLClassLoader loader = new NMSBounceClassLoader(EXTRACTED_PATH.toURI().toURL());
        JarFile file = new JarFile(EXTRACTED_PATH);
        var classes = file.stream().filter(entry -> !entry.isDirectory()).map(ZipEntry::getName).filter(name -> name.endsWith(".class")).map(name -> {
            try {
                return loader.loadClass(name.substring(0, name.length() - 6).replaceAll("/", "."));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
        this.classes.addAll(classes);
        file.close();
    }

    public Collection<Class<?>> classes() {
        return Collections.unmodifiableCollection(this.classes);
    }
}
