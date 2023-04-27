package com.mohistmc.banner.mixin.commands;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R3.command.ServerCommandSender;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/commands/CommandSource$1")
public class MixinCommandSource1 {

    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return new ServerCommandSender() {
            private final boolean isOp = wrapper.hasPermission(wrapper.getServer().getOperatorUserPermissionLevel());

            @Override
            public boolean isOp() {
                return isOp;
            }

            @Override
            public void setOp(boolean value) {
            }

            @Override
            public void sendMessage(@NotNull String message) {

            }

            @Override
            public void sendMessage(@NotNull String[] messages) {

            }

            @NotNull
            @Override
            public String getName() {
                return "NULL";
            }

            @Override
            public @NotNull Spigot spigot() {
                return null;
            }
        };
    }
}
