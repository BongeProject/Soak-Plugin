package org.soak.wrapper.permissions;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.Set;

public class SoakPermissible implements Permissible {

    protected Subject subject;

    public SoakPermissible(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return this.subject;
    }

    @Override
    public void recalculatePermissions() {
        throw NotImplementedException.createByLazy(Permissible.class, "recalculatePermissions");
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment arg0) {
        throw NotImplementedException.createByLazy(Permissible.class, "removeAttachment", PermissionAttachment.class);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission arg0) {
        return this.isPermissionSet(arg0.getName());
    }

    @Override
    public boolean isPermissionSet(@NotNull String arg0) {
        return this.subject.permissionValue(arg0) != Tristate.UNDEFINED;
    }

    @Override
    public boolean hasPermission(@NotNull Permission arg0) {
        return this.hasPermission(arg0.getName());
    }

    @Override
    public boolean hasPermission(@NotNull String arg0) {
        var permissionService  = Sponge.server().serviceProvider().permissionService();
        return this.subject.hasPermission(arg0);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin arg0, int arg1) {
        throw NotImplementedException.createByLazy(Permissible.class, "addAttachment", Plugin.class, int.class);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin arg0, @NotNull String arg1, boolean arg2) {
        throw NotImplementedException.createByLazy(Permissible.class,
                "addAttachment",
                Plugin.class,
                String.class,
                boolean.class);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin arg0) {
        throw NotImplementedException.createByLazy(Permissible.class, "addAttachment", Plugin.class);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin arg0, @NotNull String arg1, boolean arg2, int arg3) {
        throw NotImplementedException.createByLazy(Permissible.class,
                "addAttachment",
                Plugin.class,
                String.class,
                boolean.class,
                int.class);
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        throw NotImplementedException.createByLazy(Permissible.class, "getEffectivePermissions");

    }

    @Override
    public boolean isOp() {
        throw NotImplementedException.createByLazy(Permissible.class, "isOp");
    }

    @Override
    public void setOp(boolean value) {
        throw NotImplementedException.createByLazy(Permissible.class, "setOp", boolean.class);
    }
}
