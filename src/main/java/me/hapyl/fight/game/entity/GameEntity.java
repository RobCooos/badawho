package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class GameEntity {

    public final UUID uuid;
    @Nonnull
    protected LivingEntity entity;

    // By default, GameEntity is a 'base' class, which allows for
    // faster and better checks for if entity is valid.
    protected boolean base = false;

    public GameEntity(@Nonnull LivingEntity entity) {
        this.uuid = entity.getUniqueId();
        this.entity = entity;
    }

    @Nonnull
    public LivingEntity getEntity() {
        return entity;
    }

    @Nonnull
    public GameEntity getGameEntity() {
        return this;
    }

    public void teleport(Location location) {
        entity.teleport(location);
    }

    @Nonnull
    public Location getLocation() {
        return entity.getLocation();
    }

    public <T extends LivingEntity> boolean is(@Nonnull Class<T> clazz) {
        return clazz.isInstance(entity);
    }

    public boolean is(@Nullable LivingEntity entity) {
        return this.entity == entity;
    }

    public boolean isNot(@Nonnull LivingEntity entity) {
        return this.entity != entity;
    }

    public <T extends LivingEntity> boolean isNot(@Nonnull Class<T> clazz) {
        return !is(clazz);
    }

    public void addPassenger(@Nonnull GameEntity entity) {
        this.entity.addPassenger(entity.getEntity());
    }

    public void addPassenger(@Nonnull Entity entity) {
        this.entity.addPassenger(entity);
    }

    @Nonnull
    public String getName() {
        return entity.getName();
    }

    @Nonnull
    public final String getNameUnformatted() {
        return Utils.stripColor(getName());
    }

    @Nonnull
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }

    public void asPlayer(Consumer<Player> consumer) {
        if (!(entity instanceof Player player)) {
            return;
        }

        consumer.accept(player);
    }

    public final UUID getUUID() {
        return uuid;
    }

    @Nonnull
    public World getWorld() {
        final World world = getLocation().getWorld();

        if (world != null) {
            return world;
        }

        throw new IllegalArgumentException("unloaded world!!!");
    }

    public boolean isValid() {
        return isValid(null);
    }

    public boolean isValid(Player player) {
        // null entities, self or armor stands are not valid
        if (is(player) || entity instanceof ArmorStand) {
            return false;
        }

        // dead or base entities are not valid
        if (entity.isDead() || base) { // entity.isInvisible()
            return false;
        }

        // players are only valid if they are alive and not on the same team
        if (entity instanceof Player targetPlayer) {
            // creative players should not be valid!
            if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
                return false;
            }

            if (Manager.current().isGameInProgress() && !CF.getOrCreatePlayer(targetPlayer).isAlive()) {
                return false;
            }
            return !GameTeam.isTeammate(player, targetPlayer);
        }

        // Dummy check
        if (entity.getScoreboardTags().contains("dummy")) {
            return true;
        }

        return entity.hasAI() && !entity.isInvulnerable();
    }

    public boolean hasLineOfSight(@Nonnull LivingEntity entity) {
        return this.entity.hasLineOfSight(entity);
    }

    public void setVelocity(Vector vector) {
        entity.setVelocity(vector);
    }

    public boolean hasTag(String s) {
        return entity.getScoreboardTags().contains(s);
    }

    public void removeTag(String s) {
        entity.getScoreboardTags().remove(s);
    }

    public void setInvulnerable(boolean b) {
        entity.setInvulnerable(b);
    }

    public double getEyeHeight() {
        return entity.getEyeHeight();
    }

    public void setFreezeTicks(int tick) {
        entity.setFreezeTicks(tick);
    }

    public int getNoDamageTicks() {
        return entity.getNoDamageTicks();
    }

    public void setFireTicks(int tick) {
        entity.setFireTicks(tick);
    }

    public void remove() {
        entity.remove();
    }

    /**
     * Calls entity removal without the death animation.
     * Does <b>nothing</b> to players.
     */
    public final void forceRemove() {
        if (entity instanceof Player) {
            return;
        }

        entity.remove();
        remove();
    }

    @Event
    public void onStart(@Nonnull GameInstance instance) {
    }

    @Event
    public void onStop(@Nonnull GameInstance instance) {
        forceRemove();
    }

    @Event
    public void onDeath() {
        remove();

    }

    public <T extends GameEntity> void as(@Nonnull Class<T> clazz, @Nonnull Consumer<T> consumer) {
        if (clazz.isInstance(entity)) {
            consumer.accept(clazz.cast(entity));
        }
    }

    public void sendWarning(String warning, int stay) {
        asPlayer(player -> Chat.sendTitle(player, "&4&l⚠", warning, 0, stay, 5));
    }

    public void sendMessage(String message, Object... objects) {
        Chat.sendMessage(entity, message, objects);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        asPlayer(player -> Chat.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut));
    }

    public void sendSubtitle(String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle("", subtitle, fadeIn, stay, fadeOut);
    }

    public void sendActionbar(String text, Object... objects) {
        asPlayer(player -> Chat.sendActionbar(player, text, objects));
    }

    public void playSound(Sound sound, final float pitch) {
        asPlayer(player -> PlayerLib.playSound(player, sound, Numbers.clamp(pitch, 0.0f, 2.0f)));
    }

    public void playSound(Location location, Sound sound, float pitch) {
        asPlayer(player -> PlayerLib.playSound(player, location, sound, pitch));
    }

    @Override
    public String toString() {
        return "GameEntity{" + uuid.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GameEntity that = (GameEntity) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public int getId() {
        return entity.getEntityId();
    }

    public void setCustomName(@Nullable String name) {
        this.entity.setCustomName(name);
    }

    public void setCustomNameVisible(boolean visible) {
        this.entity.setCustomNameVisible(visible);
    }

    @Nullable
    public String getCustomName() {
        return this.entity.getCustomName();
    }

    public void flip() {
        final String name = getCustomName();
        if (name == null || !name.equals("Dinnerbone")) {
            setCustomName("Dinnerbone");
        }
        else {
            setCustomName(null);
        }
    }

    public void setAI(boolean b) {
        this.entity.setAI(false);
    }

    public boolean isProjectile() {
        return entity instanceof Projectile;
    }

    @Nonnull
    public Vector getVelocity() {
        return entity.getVelocity();
    }
}
