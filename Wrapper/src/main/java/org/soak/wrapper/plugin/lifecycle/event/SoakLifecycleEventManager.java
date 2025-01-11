package org.soak.wrapper.plugin.lifecycle.event;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.LifecycleEventHandlerConfiguration;
import org.soak.exception.NotImplementedException;

public class SoakLifecycleEventManager<O extends LifecycleEventOwner> implements LifecycleEventManager<O> {
    @Override
    public void registerEventHandler(LifecycleEventHandlerConfiguration<? super O> lifecycleEventHandlerConfiguration) {
        throw NotImplementedException.createByLazy(LifecycleEventManager.class, "registerEventHandler", LifecycleEventHandlerConfiguration.class);
    }
}
