package me.hapyl.fight.util;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.annotate.TestedNMS;
import me.hapyl.spigotutils.module.annotate.Version;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import net.minecraft.world.entity.boss.wither.EntityWither;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Utilities for the plugin
 */
public class Utils {

    public static String colorString(String str, String defColor) {
        final StringBuilder builder = new StringBuilder();
        final String[] strings = str.split(" ");
        for (final String string : strings) {
            if (string.endsWith("%")) {
                builder.append(ChatColor.RED);
            }
            else if (string.endsWith("s") && string.contains("[0-9]")) {
                builder.append(ChatColor.AQUA);
            }
            else {
                builder.append(defColor);
            }
            builder.append(string).append(" ");
        }
        return builder.toString().trim();
    }

    public static void setEquipment(LivingEntity entity, Consumer<EntityEquipment> consumer) {
        Nulls.runIfNotNull(entity.getEquipment(), consumer);
    }

    public static double scaleParticleOffset(double v) {
        return v * v / 8.0d;
    }

    /**
     * Gets a loaded world from location or throws an error if the world is null.
     *
     * @param location - Location.
     * @return a loaded world from location or throws an error if the world is null.
     */
    @Nonnull
    public static World getWorld(Location location) {
        final World world = location.getWorld();
        if (world == null) {
            throw new NullPointerException("world is unloaded");
        }

        return world;
    }

    @Nullable
    public static Team getEntityTeam(Entity entity) {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
        if (scoreboard == null) {
            return null;
        }

        return getEntityTeam(entity, scoreboard);
    }

    @Nullable
    public static Team getEntityTeam(Entity entity, Scoreboard scoreboard) {
        return scoreboard.getEntryTeam(entity instanceof Player ? entity.getName() : entity.getUniqueId().toString());
    }

    public static void playSoundAndCut(Location location, Sound sound, float pitch, int cutAt) {
        final Set<Player> playingTo = new HashSet<>();
        Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
            final Player player = gp.getPlayer();
            player.playSound(location, sound, SoundCategory.RECORDS, 20f, pitch);
            playingTo.add(player);
        });
        new GameTask() {
            @Override
            public void run() {
                playingTo.forEach(player -> {
                    player.stopSound(sound, SoundCategory.RECORDS);
                });
                playingTo.clear();
            }
        }.runTaskLater(cutAt);
    }

    public static void playSoundAndCut(Player player, Sound sound, float pitch, int cutAt) {
        PlayerLib.playSound(player, sound, pitch);
        new GameTask() {
            @Override
            public void run() {
                player.stopSound(sound, SoundCategory.RECORDS);
            }
        }.runTaskLater(cutAt);
    }

    /**
     * Compares 2 objects.
     *
     * <pre>
     *     a && b == null -> TRUE
     *     a || b == null -> FALSE
     *     a == b         -> TRUE
     * </pre>
     *
     * @param a - First object.
     * @param b - Second object.
     * @return if two objects either both null or equal; false otherwise.
     */
    public static boolean compare(@Nullable Object a, @Nullable Object b) {
        // true if both objects are null
        if (a == null && b == null) {
            return true;
        }
        // false if only one object is null
        if (a == null || b == null) {
            return false;
        }

        return a.equals(b);
    }

    public static <E> void clearCollectionAnd(Collection<E> collection, Consumer<E> action) {
        collection.forEach(action);
        collection.clear();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * @deprecated use {@link me.hapyl.fight.game.effect.GameEffectType#INVISIBILITY}
     */
    @Deprecated
    public static void hidePlayer(Player player) {
        Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
            if (gp.getPlayer() == player || gp.isSpectator() || GameTeam.isTeammate(gp.getPlayer(), player)) {
                return;
            }

            gp.getPlayer().hidePlayer(Main.getPlugin(), player);
        });
    }

    /**
     * @deprecated use {@link me.hapyl.fight.game.effect.GameEffectType#INVISIBILITY}
     */
    @Deprecated
    public static void showPlayer(Player player) {
        Manager.current().getCurrentGame().getPlayers().forEach((uuid, gp) -> {
            if (gp.getPlayer() != player) {
                gp.getPlayer().showPlayer(Main.getPlugin(), player);
            }
        });
    }

    public static void rayTracePath(@Nonnull Location start, @Nonnull Location end, double shift, double searchRange, @Nullable Consumer<LivingEntity> funcLiving, @Nullable Consumer<Location> funcLoc) {
        final double maxDistance = start.distance(end);
        final Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(shift);

        new GameTask() {
            private double tick = maxDistance;

            @Override
            public void run() {
                if (tick < 0) {
                    this.cancel();
                    return;
                }

                start.add(vector);

                Nulls.runIfNotNull(funcLiving, f -> Collect.nearbyLivingEntities(start, searchRange).forEach(f));
                Nulls.runIfNotNull(funcLoc, f -> f.accept(start));

                tick -= shift;
            }
        }.runTaskTimer(0, 1);

    }

    public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, @Nullable EnumDamageCause cause, @Nullable Consumer<Location> onMove, @Nullable Consumer<LivingEntity> onHit) {
        final Location location = shooter.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();

        main:
        for (double i = 0; i < maxDistance; i += shift) {

            double x = vector.getX() * i;
            double y = vector.getY() * i;
            double z = vector.getZ() * i;
            location.add(x, y, z);

            // check for the hitting a block and an entity
            if (location.getBlock().getType().isOccluding()) {
                break;
            }

            for (final LivingEntity living : Collect.nearbyLivingEntities(location, 0.5)) {
                if (living == shooter || living instanceof Player player && !Manager.current().isPlayerInGame(player)) {
                    continue;
                }

                if (onHit != null) {
                    onHit.accept(living);
                }

                if (damage > 0.0d) {
                    GamePlayer.damageEntity(living, damage, shooter, cause);
                }
                break main;
            }

            if (i > 1.0) {
                if (onMove != null) {
                    onMove.accept(location);
                }
            }
            location.subtract(x, y, z);

        }
    }

    /**
     * Roots to the actual damager from projectile, owner etc.
     *
     * @param entity the entity to root
     * @return the root damager
     */
    @Nonnull
    public static LivingEntity rootDamager(@Nonnull LivingEntity entity) {
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        else if (entity instanceof Tameable tameable && tameable.getOwner() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        return entity;
    }

    public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, @Nullable Consumer<Location> onMove, @Nullable Consumer<LivingEntity> onHit) {
        rayTraceLine(shooter, maxDistance, shift, damage, null, onMove, onHit);
    }

    /**
     * Performs a "smart" collection clear, with removing entities and updating block states.
     *
     * @param collection - Collection.
     */
    public static <E> void clearCollection(Collection<E> collection) {
        for (final E entry : collection) {
            if (entry == null) {
                continue;
            }
            if (entry instanceof Entity entity) {
                entity.remove();
            }
            if (entry instanceof Block block) {
                block.getState().update(true, false);
            }
        }
        collection.clear();
    }

    @Nonnull
    public static <K, V> V getElementOrThrowErrorIfNull(Map<K, V> map, K key, String errorMessage) {
        final V v = map.get(key);

        if (v != null) {
            return v;
        }

        throw new IllegalArgumentException(errorMessage);
    }

    @TestedNMS(version = Version.V1_20)
    public static void setWitherInvul(Wither wither, int invul) {
        //Reflect.setDataWatcherValue(
        //        Objects.requireNonNull(Reflect.getMinecraftEntity(wither)),
        //        DataWatcherType.INT,
        //        19,
        //        invul,
        //        Bukkit.getOnlinePlayers().toArray(new Player[] {})
        //);
        ((EntityWither) Objects.requireNonNull(Reflect.getMinecraftEntity(wither))).s(invul);
    }

    /**
     * Forces entity to look at provided location.
     *
     * @param entity - Entity.
     * @param at     - Look at.
     */
    public static void lookAt(@Nonnull LivingEntity entity, @Nonnull Location at) {
        final Vector dirBetweenLocations = at.toVector().subtract(entity.getLocation().toVector());
        final Location location = entity.getLocation();

        location.setDirection(dirBetweenLocations);
        entity.teleport(location);
    }

    public static boolean isEntityValid(Entity entity) {
        return isEntityValid(entity, null);
    }

    public static boolean isEntityValid(Entity entity, @Nullable Player player) {
        // null entities, self or armor stands are not valid
        if (entity == null || (player != null && entity == player) || entity instanceof ArmorStand) {
            return false;
        }

        // dead or invisible entities are not valid
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isDead() || livingEntity.isInvisible()) {
                return false;
            }

            // players are only valid if they are alive and not on the same team
            if (entity instanceof Player targetPlayer) {
                // creative players should not be valid!
                if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
                    return false;
                }

                if (Manager.current().isGameInProgress() && !GamePlayer.getPlayer(targetPlayer).isAlive()) {
                    return false;
                }
                return !GameTeam.isTeammate(player, targetPlayer);
            }

            // Dummy check
            if (livingEntity.getScoreboardTags().contains("dummy")) {
                return true;
            }

            return livingEntity.hasAI();
        }

        // other entities are valid
        return true;
    }

    public static void createExplosion(Location location, double range, double damage, Consumer<LivingEntity> consumer) {
        createExplosion(location, range, damage, null, null, consumer);
    }

    public static void createExplosion(Location location, double range, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
        createExplosion(location, range, damage, damager, cause, null);
    }

    public static void createExplosion(Location location, double range, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, @Nullable Consumer<LivingEntity> consumer) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        Collect.nearbyLivingEntities(location, range).forEach(entity -> {
            if (damage > 0.0d) {
                GamePlayer.damageEntity(entity, damage, damager, cause);
            }
            if (consumer != null) {
                consumer.accept(entity);
            }
        });

        // Fx
        Geometry.drawCircle(location, range, Quality.NORMAL, new WorldParticle(Particle.CRIT));
        Geometry.drawCircle(location, range + 0.5d, Quality.NORMAL, new WorldParticle(Particle.ENCHANTMENT_TABLE));
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_HUGE, 1, 1, 0, 1, 0);
        PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f);
    }

    public static void createExplosion(Location location, double range, double damage) {
        createExplosion(location, range, damage, null);
    }

    public static void lockArmorStand(ArmorStand stand) {
        for (final EquipmentSlot value : EquipmentSlot.values()) {
            for (final ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
                stand.addEquipmentLock(value, lockType);
            }
        }
    }

    public static void unlockArmorStand(ArmorStand stand) {
        for (final EquipmentSlot value : EquipmentSlot.values()) {
            for (final ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
                stand.removeEquipmentLock(value, lockType);
            }
        }
    }

    public static <E> E fetchFirstFromLinkedMap(LinkedHashMap<?, E> map, E def) {
        for (E value : map.values()) {
            return value;
        }

        return def;
    }

    public static <E> List<String> collectionToStringList(Collection<E> e, java.util.function.Function<E, String> fn) {
        final List<String> list = new ArrayList<>();
        e.forEach(el -> list.add(fn.apply(el)));
        return list;
    }
}
