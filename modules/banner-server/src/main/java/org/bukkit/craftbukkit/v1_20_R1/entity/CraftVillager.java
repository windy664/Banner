package org.bukkit.craftbukkit.v1_20_R1.entity;

import com.google.common.base.Preconditions;
import com.mohistmc.banner.bukkit.BukkitMethodHooks;
import com.mohistmc.banner.fabric.BukkitRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Locale;

public class CraftVillager extends CraftAbstractVillager implements Villager {

    public CraftVillager(CraftServer server, net.minecraft.world.entity.npc.Villager entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.npc.Villager getHandle() {
        return (net.minecraft.world.entity.npc.Villager) entity;
    }

    @Override
    public String toString() {
        return "CraftVillager";
    }

    @Override
    public void remove() {
        getHandle().releaseAllPois();

        super.remove();
    }

    @Override
    public Profession getProfession() {
        return CraftVillager.nmsToBukkitProfession(getHandle().getVillagerData().getProfession());
    }

    @Override
    public void setProfession(Profession profession) {
        Preconditions.checkArgument(profession != null, "Profession cannot be null");
        getHandle().setVillagerData(getHandle().getVillagerData().setProfession(CraftVillager.bukkitToNmsProfession(profession)));
    }

    @Override
    public Type getVillagerType() {
        return Type.valueOf(BuiltInRegistries.VILLAGER_TYPE.getKey(getHandle().getVillagerData().getType()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerType(Type type) {
        Preconditions.checkArgument(type != null, "Type cannot be null");
        getHandle().setVillagerData(getHandle().getVillagerData().setType(BuiltInRegistries.VILLAGER_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()))));
    }

    @Override
    public int getVillagerLevel() {
        return getHandle().getVillagerData().getLevel();
    }

    @Override
    public void setVillagerLevel(int level) {
        Preconditions.checkArgument(1 <= level && level <= 5, "level (%s) must be between [1, 5]", level);

        getHandle().setVillagerData(getHandle().getVillagerData().setLevel(level));
    }

    @Override
    public int getVillagerExperience() {
        return getHandle().getVillagerXp();
    }

    @Override
    public void setVillagerExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "Experience (%s) must be positive", experience);

        getHandle().setVillagerXp(experience);
    }

    @Override
    public boolean sleep(Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");
        Preconditions.checkState(!getHandle().bridge$generation(), "Cannot sleep during world generation");

        BlockPos position = CraftLocation.toBlockPosition(location);
        BlockState iblockdata = getHandle().level().getBlockState(position);
        if (!(iblockdata.getBlock() instanceof BedBlock)) {
            return false;
        }

        getHandle().startSleeping(position);
        return true;
    }

    @Override
    public void wakeup() {
        Preconditions.checkState(isSleeping(), "Cannot wakeup if not sleeping");
        Preconditions.checkState(!getHandle().bridge$generation(), "Cannot wakeup during world generation");

        getHandle().stopSleeping();
    }

    @Override
    public void shakeHead() {
        getHandle().setUnhappy();
    }

    @Override
    public org.bukkit.entity.ZombieVillager zombify() {
        ZombieVillager entityzombievillager = BukkitMethodHooks.zombifyVillager(getHandle().level().getMinecraftWorld(), getHandle(), getHandle().blockPosition(), isSilent(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (entityzombievillager != null) ? (org.bukkit.entity.ZombieVillager) entityzombievillager.getBukkitEntity() : null;
    }

    public static Profession nmsToBukkitProfession(VillagerProfession nms) {
        return BuiltInRegistries.VILLAGER_PROFESSION.getKey(nms).getNamespace().equals(NamespacedKey.MINECRAFT)
                ? Profession.valueOf(BuiltInRegistries.VILLAGER_PROFESSION.getKey(nms).getPath().toUpperCase(Locale.ROOT))
                : Profession.valueOf(BukkitRegistry.normalizeName(BuiltInRegistries.VILLAGER_PROFESSION.getKey(nms).toString()));
    }

    public static VillagerProfession bukkitToNmsProfession(Profession bukkit) {
        return !BukkitRegistry.profession.containsKey(bukkit) ? BuiltInRegistries.VILLAGER_PROFESSION.get(CraftNamespacedKey.toMinecraft(bukkit.getKey())) : BuiltInRegistries.VILLAGER_PROFESSION.get(BukkitRegistry.profession.get(bukkit));
    }
}
