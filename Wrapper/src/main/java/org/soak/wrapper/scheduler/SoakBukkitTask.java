package org.soak.wrapper.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.plugin.PluginContainer;

public class SoakBukkitTask implements BukkitTask {

    private final ScheduledTask spongeTask;

    public SoakBukkitTask(ScheduledTask task) {
        this.spongeTask = task;
    }

    public ScheduledTask spongeTask() {
        return this.spongeTask;
    }

    @Override
    public boolean isSync() {
        return this.spongeTask.scheduler().equals(Sponge.server().scheduler());
    }

    public boolean isBukkit() {
        return SoakManager.getManager().getSoakContainer(this.spongeTask.task().plugin()).isPresent();
    }

    @Override
    public @NotNull Plugin getOwner() {
        PluginContainer task = this.spongeTask.task().plugin();
        return SoakManager.getManager().getSoakContainer(task).orElseThrow(() -> new IllegalStateException("Task is a sponge task")).getBukkitInstance();
    }

    @Override
    public int getTaskId() {
        //ideally use the UUID somehow
        return this.spongeTask.name().hashCode();
    }

    @Override
    public boolean isCancelled() {
        return this.spongeTask.isCancelled();
    }

    @Override
    public void cancel() {
        this.spongeTask.cancel();
    }
}
