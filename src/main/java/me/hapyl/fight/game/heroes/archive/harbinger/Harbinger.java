package me.hapyl.fight.game.heroes.archive.harbinger;

import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.archive.harbinger.StanceData;
import me.hapyl.fight.game.talents.archive.harbinger.TidalWaveTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Harbinger extends Hero implements Listener, UIComponent {

    private final double ultimateRadius = 4.0d;
    private final PlayerMap<RiptideStatus> riptideStatus = PlayerMap.newMap();
    //private final Map<Player, Set<LivingEntity>> riptideAffected = new HashMap<>();

    /**
     * Changes to riptide:
     * - Is now a status effect that lasts for certain duration regardless.
     * - Riptide Slash can now be executed every 2.5 seconds.
     * - Riptide can be executed with a fully charged bow as well as melee.
     */

    public Harbinger() {
        super("Harbinger", "She is a harbinger of unknown organization. Nothing else is known.", Material.ANVIL);

        setArchetype(Archetype.STRATEGY);

        setMinimumLevel(5);
        setItem("22a1ac2a8dd48c371482806b3963571952997a5712806e2c8060b8e7777754");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(82, 82, 76);
        equipment.setLeggings(54, 48, 48);
        equipment.setBoots(183, 183, 180);

        setWeapon(new Weapon(Material.BOW).setDamage(2.0d).setName("Bow").setDescription("Just a normal bow."));

        setUltimate(new UltimateTalent(
                "Crowned Mastery",
                "Gather the surrounding energy to execute a fatal slash:____While in &e&lRange Stance&7, shoot a magic arrow in front of you that explodes on impact, dealing AoE damage and applying &bRiptide &7effect to opponents.____While in &e&lMelee Stance&7, perform a slash around you that deals AoE damage and executes &bRiptide Slash &7if opponent is affected by &bRiptide&7.",
                70
        ).setItem(Material.DIAMOND).setDuration(40));
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();

        if (player == null || !Talents.STANCE.getTalent(MeleeStance.class).isActive(player)) {
            return null;
        }

        executeRiptideSlashIfPossible(player, entity);
        return null;
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamagerProjectile(DamageInput input, Projectile projectile) {
        if (!(projectile instanceof Arrow arrow) || !arrow.isCritical()) {
            return null;
        }

        final GamePlayer player = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();

        if (player == null) {
            return DamageOutput.OK;
        }

        executeRiptideSlashIfPossible(player, entity);
        addRiptide(player, entity, 150, false);

        return DamageOutput.OK;
    }

    public void executeRiptideSlashIfPossible(GamePlayer player, LivingGameEntity entity) {
        getRiptideStatus(player).executeRiptideSlash(entity);
    }

    public void addRiptide(GamePlayer player, LivingGameEntity entity, long amount, boolean force) {
        getRiptideStatus(player).setRiptide(entity, amount, force);
    }

    public RiptideStatus getRiptideStatus(GamePlayer player) {
        return this.riptideStatus.computeIfAbsent(player, r -> new RiptideStatus(player));
    }

    @Override
    public void onStop() {
        riptideStatus.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        riptideStatus.remove(player);
    }

    @Override
    public void onDeathGlobal(@Nonnull GamePlayer gamePlayer, @Nullable GameEntity killer, @Nullable EnumDamageCause cause) {
        for (RiptideStatus value : riptideStatus.values()) {
            if (value.isAffected(gamePlayer)) {
                value.stop(gamePlayer);
            }
        }
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                riptideStatus.forEach((p, r) -> r.tick());
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final Location playerLocation = player.getLocation();
        player.addPotionEffect(PotionEffectType.SLOW, 20, 2);
        player.playWorldSound(Sound.BLOCK_CONDUIT_AMBIENT, 2.0f);

        // Stance Check
        final boolean isMeleeStance = getFirstTalent().isActive(player);

        if (isMeleeStance) {
            // Melee Stance
            new GameTask() {
                @Override
                public void run() {
                    new GameTask() {
                        private double d = 0.0d;

                        @Override
                        public void run() {
                            if (d < Math.PI * 2) {
                                final Location location = player.getEyeLocation();

                                final double x = ultimateRadius * Math.sin(d);
                                final double z = ultimateRadius * Math.cos(d);

                                location.add(x, 0, z);

                                Collect.nearbyEntities(location, 2.0d).forEach(entity -> {
                                    if (entity.equals(player)) {
                                        return;
                                    }

                                    entity.damage(40.0d, player, EnumDamageCause.RIPTIDE);
                                });

                                PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, 0);
                                PlayerLib.spawnParticle(location, Particle.FALLING_WATER, 3, 0.5, 0, 0.5, 0);

                                d += Math.PI / 8;
                                return;
                            }

                            this.cancel();
                        }
                    }.runTaskTimer(0, 1);
                }
            }.runTaskLater(15);
        }
        // Ranged Stance
        else {
            final Location location = playerLocation.add(playerLocation.getDirection().setY(0.0d).multiply(1.5d));
            final Arrow arrow = player.getWorld()
                    .spawnArrow(
                            location.clone().add(0.0d, 3.0d, 0.0d),
                            playerLocation.getDirection().normalize().multiply(0.75d).setY(-0.25d),
                            0,
                            0
                    );

            arrow.setShooter(player.getPlayer());
            arrow.setCritical(false);
            arrow.setColor(Color.AQUA);

            new GameTask() {
                @Override
                public void run() {
                    Collect.nearbyEntities(location, ultimateRadius).forEach(entity -> {
                        if (entity.equals(player)) {
                            return;
                        }

                        entity.damage(25.0d, player, EnumDamageCause.RIPTIDE);
                        entity.playWorldSound(Sound.ENTITY_GENERIC_BIG_FALL, 0.75f);
                        entity.playWorldSound(Sound.ENTITY_GENERIC_HURT, 1.25f);

                        addRiptide(player, entity, 500, false);
                    });

                    // Fx
                    Geometry.drawSphere(location, 10, ultimateRadius, new WorldParticle(Particle.BUBBLE_POP));
                    PlayerLib.playSound(location, Sound.AMBIENT_UNDERWATER_EXIT, 0.0f);

                }
            }.runTaskLater(10);
        }

        return UltimateCallback.OK;
    }

    @Override
    public MeleeStance getFirstTalent() {
        return (MeleeStance) Talents.STANCE.getTalent();
    }

    @Override
    public TidalWaveTalent getSecondTalent() {
        return (TidalWaveTalent) Talents.TIDAL_WAVE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.RIPTIDE.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final StanceData data = getFirstTalent().getData(player);

        if (data == null) {
            return "";
        }

        return "&f⚔: &l%ss&f/&l%ss".formatted(
                BukkitUtils.roundTick(data.getDurationTick()),
                BukkitUtils.roundTick(getFirstTalent().getMaxDuration())
        );
    }
}
