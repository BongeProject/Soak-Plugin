package org.soak.wrapper.scheduler;

import net.kyori.adventure.util.Ticks;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;
import org.jetbrains.annotations.NotNull;
import org.mose.collection.stream.builder.CollectionStreamBuilder;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.utils.ListMappingUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scheduler.TaskFuture;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class SoakBukkitScheduler implements BukkitScheduler {

    private void apply(Consumer<Scheduler> consumer) {
        consumer.accept(Sponge.asyncScheduler());
        consumer.accept(Sponge.server().scheduler());
    }

    private <T> Stream<T> get(Function<Scheduler, Stream<T>> function) {
        Stream<T> asynced = function.apply(Sponge.asyncScheduler());
        Stream<T> synced = function.apply(Sponge.server().scheduler());
        return Stream.concat(asynced, synced);
    }

    @Override
    public boolean isQueued(int arg0) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "isQueued", int.class);
    }

    @Deprecated
    @Override
    public int scheduleSyncRepeatingTask(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1, long arg2, long arg3) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "scheduleSyncRepeatingTask", Plugin.class, BukkitRunnable.class, long.class, long.class);
    }

    @Override
    public int scheduleSyncRepeatingTask(@NotNull Plugin arg0, @NotNull Runnable arg1, long arg2, long arg3) {
        return scheduleRepeatingTask(Sponge.server().scheduler(), arg0, arg1, arg2, arg3);
    }

    private int scheduleRepeatingTask(Scheduler scheduler, Plugin plugin, Runnable runner, long delay, long period) {
        SoakPluginContainer container = SoakManager.getManager().getContainer(plugin);

        Task task = Task.builder()
                .delay(Ticks.duration(delay))
                .interval(Ticks.duration(period))
                .execute(new SoakRunnerWrapper(plugin, runner))
                .plugin(container)
                .build();
        var spongeTask = scheduler.submit(task);
        return new SoakBukkitTask(spongeTask).getTaskId();
    }

    @Override
    public @NotNull <T> Future<T> callSyncMethod(@NotNull Plugin plugin, @NotNull Callable<T> task) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "callSyncMethod", Plugin.class, Callable.class);
    }

    @Override
    public void cancelTask(int arg0) {
        get(sch -> sch.tasks().stream())
                .map(SoakBukkitTask::new)
                .filter(task -> task.getTaskId() == arg0)
                .findAny()
                .ifPresent(SoakBukkitTask::cancel);
    }

    @Override
    public void cancelTasks(@NotNull Plugin arg0) {
        SoakPluginContainer container = SoakManager.getManager().getContainer(arg0);
        apply(sch -> sch.tasks(container).forEach(ScheduledTask::cancel));
    }

    @Override
    public @NotNull List<BukkitWorker> getActiveWorkers() {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "getActiveWorkers");
    }

    @Override
    public @NotNull List<BukkitTask> getPendingTasks() {
        var builder = CollectionStreamBuilder.builder().stream(get(sch -> sch.tasks().stream())).basicMap(t -> (BukkitTask) new SoakBukkitTask(t));
        return ListMappingUtils.fromStream(builder, () -> get((sch) -> sch.tasks().stream()), (scheduledTask, soakBukkitTask) -> ((SoakBukkitTask) soakBukkitTask).spongeTask().equals(scheduledTask), Comparator.comparing(task -> task.name())).buildList();
    }

    @Deprecated
    @Override
    public @NotNull BukkitTask runTask(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTask", Plugin.class, BukkitRunnable.class);
    }

    @Override
    public @NotNull BukkitTask runTask(@NotNull Plugin arg0, @NotNull Runnable arg1) {
        return runTask(Sponge.server().scheduler(), arg0, arg1);
    }

    private BukkitTask runTask(Scheduler scheduler, Plugin plugin, Runnable runner) {
        SoakPluginContainer container = SoakManager.getManager().getContainer(plugin);
        TaskFuture<?> task = scheduler.executor(container).submit(new SoakRunnerWrapper(plugin, runner));
        return new SoakBukkitTask(task.task());
    }

    @Override
    public void runTask(@NotNull Plugin plugin, @NotNull Consumer<? super BukkitTask> task) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTask", Plugin.class, Consumer.class);
    }

    @Deprecated
    @Override
    public @NotNull BukkitTask runTaskAsynchronously(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskAsynchronously", Plugin.class, BukkitRunnable.class);
    }

    @Override
    public void runTaskAsynchronously(@NotNull Plugin plugin, @NotNull Consumer<? super BukkitTask> task) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskAsynchronously", Plugin.class, Consumer.class);
    }

    @Override
    public @NotNull BukkitTask runTaskAsynchronously(@NotNull Plugin arg0, @NotNull Runnable arg1) {
        return runTask(Sponge.asyncScheduler(), arg0, arg1);
    }

    @Override
    public @NotNull BukkitTask runTaskLater(@NotNull Plugin arg0, @NotNull Runnable arg1, long arg2) {
        return runTaskLater(Sponge.server().scheduler(), arg0, arg1, arg2);
    }

    private @NotNull BukkitTask runTaskLater(Scheduler scheduler, Plugin plugin, Runnable runner, long delay) {
        SoakPluginContainer container = SoakManager.getManager().getContainer(plugin);
        TaskFuture<?> task = scheduler.executor(container).schedule(new SoakRunnerWrapper(plugin, runner), delay / Ticks.SINGLE_TICK_DURATION_MS, TimeUnit.MILLISECONDS);
        return new SoakBukkitTask(task.task());
    }

    @Override
    public void runTaskLater(@NotNull Plugin plugin, @NotNull Consumer<? super BukkitTask> task, long delay) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskLater", Plugin.class, Consumer.class, long.class);
    }

    @Deprecated
    @Override
    public @NotNull BukkitTask runTaskLater(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1, long arg2) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskLater", Plugin.class, BukkitRunnable.class, long.class);
    }

    @Deprecated
    @Override
    public @NotNull BukkitTask runTaskLaterAsynchronously(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1, long arg2) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskLaterAsynchronously", Plugin.class, BukkitRunnable.class, long.class);
    }

    @Override
    public @NotNull BukkitTask runTaskLaterAsynchronously(@NotNull Plugin arg0, @NotNull Runnable arg1, long arg2) {
        return this.runTaskLater(Sponge.asyncScheduler(), arg0, arg1, arg2);
    }

    @Override
    public void runTaskLaterAsynchronously(@NotNull Plugin plugin, @NotNull Consumer<? super BukkitTask> task, long delay) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskLaterAsynchronously", Plugin.class, Consumer.class, long.class);
    }

    @Override
    public void runTaskTimer(@NotNull Plugin plugin, @NotNull Consumer<? super BukkitTask> task, long delay, long period) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskTimer", Plugin.class, Consumer.class, long.class, long.class);
    }

    @Deprecated
    @Override
    public @NotNull BukkitTask runTaskTimer(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1, long arg2, long arg3) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskTimer", Plugin.class, BukkitRunnable.class, long.class, long.class);
    }

    @Override
    public @NotNull BukkitTask runTaskTimer(@NotNull Plugin arg0, @NotNull Runnable arg1, long arg2, long arg3) {
        return runTaskTimer(Sponge.server().scheduler(), arg0, arg1, arg2, arg3);
    }

    private @NotNull BukkitTask runTaskTimer(@NotNull Scheduler scheduler, @NotNull Plugin plugin, @NotNull Runnable runner, long delay, long interval) {
        if (interval == 0) {
            //sponge takes a 0 interval as a delay, however this should always create a repeating task
            interval = 1;
        }
        SoakPluginContainer container = SoakManager.getManager().getContainer(plugin);
        Task task = Task.builder()
                .plugin(container)
                .execute(new SoakRunnerWrapper(plugin, runner))
                .delay(Ticks.duration(delay))
                .interval(Ticks.duration(interval))
                .build();
        ScheduledTask schTask = scheduler.submit(task);
        return new SoakBukkitTask(schTask);
    }

    @Deprecated
    @Override
    public @NotNull BukkitTask runTaskTimerAsynchronously(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1, long arg2, long arg3) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskTimerAsynchronously", Plugin.class, BukkitRunnable.class, long.class, long.class);
    }

    @Override
    public @NotNull BukkitTask runTaskTimerAsynchronously(@NotNull Plugin arg0, @NotNull Runnable arg1, long arg2, long arg3) {
        return runTaskTimer(Sponge.asyncScheduler(), arg0, arg1, arg2, arg3);
    }

    @Override
    public void runTaskTimerAsynchronously(@NotNull Plugin plugin, @NotNull Consumer<? super BukkitTask> task, long delay, long period) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "runTaskTimerAsynchronously", Plugin.class, Consumer.class, long.class, long.class);
    }

    @Override
    public @NotNull Executor getMainThreadExecutor(@NotNull Plugin arg0) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "getMainThreadExecutor", Plugin.class);
    }

    public SoakBukkitTask scheduleDelayTask(Scheduler scheduler, Plugin plugin, Runnable runnable, long delay) {
        var pluginContainer = SoakManager.getManager().getContainer(plugin);
        var ticks = Ticks.duration(delay);
        var spongeTask = scheduler.executor(pluginContainer).schedule(runnable, ticks.toMillis(), TimeUnit.MILLISECONDS);
        return new SoakBukkitTask(spongeTask.task());
    }

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin arg0, @NotNull Runnable arg1, long arg2) {
        return scheduleDelayTask(Sponge.server().scheduler(), arg0, arg1, arg2).getTaskId();
    }

    @Deprecated
    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "scheduleSyncDelayedTask", Plugin.class, BukkitRunnable.class);
    }

    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin arg0, @NotNull Runnable arg1) {
        return scheduleDelayTask(Sponge.server().scheduler(), arg0, arg1, 0).getTaskId();
    }

    @Deprecated
    @Override
    public int scheduleSyncDelayedTask(@NotNull Plugin arg0, @NotNull BukkitRunnable arg1, long arg2) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "scheduleSyncDelayedTask", Plugin.class, BukkitRunnable.class, long.class);
    }

    @Override
    public boolean isCurrentlyRunning(int arg0) {
        throw NotImplementedException.createByLazy(BukkitScheduler.class, "isCurrentlyRunning", int.class);
    }

    @Deprecated
    @Override
    public int scheduleAsyncRepeatingTask(@NotNull Plugin arg0, @NotNull Runnable arg1, long arg2, long arg3) {
        return this.scheduleRepeatingTask(Sponge.asyncScheduler(), arg0, arg1, arg2, arg3);
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable exec, long ticks) {
        return this.scheduleDelayTask(Sponge.asyncScheduler(), plugin, exec, ticks).getTaskId();
    }

    @Deprecated
    @Override
    public int scheduleAsyncDelayedTask(@NotNull Plugin plugin, @NotNull Runnable exec) {
        return this.scheduleAsyncDelayedTask(plugin, exec);
    }

    private static class SoakRunnerWrapper implements Runnable {

        private final Runnable runnable;
        private final Plugin plugin;

        private SoakRunnerWrapper(Plugin plugin, Runnable runnable) {
            this.runnable = runnable;
            this.plugin = plugin;
        }

        @Override
        public void run() {
            try {
                this.runnable.run();
            } catch (Throwable e) {
                SoakManager.<WrapperManager>getManager().displayError(e, plugin);

            }
        }
    }

}