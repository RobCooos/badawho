package me.hapyl.fight.game.heroes.archive.ender;

import me.hapyl.fight.event.custom.EnderPearlTeleportEvent;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.ender.EnderPassive;
import me.hapyl.fight.game.talents.archive.ender.TransmissionBeacon;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class Ender extends Hero implements Listener {

    public Ender() {
        super("Ender");

        setArchetype(Archetype.MOBILITY);

        setItem("aacb357709d8cdf1cd9c9dbe313e7bab3276ae84234982e93e13839ab7cc5d16");
        setMinimumLevel(5);

        setDescription(
                "Weird enderman-like looking warrior with teleportation abilities. He hits you with his arm, but it hurts like a brick."
        );

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(85, 0, 102);
        equipment.setLeggings(128, 0, 128);
        equipment.setBoots(136, 0, 204);

        setWeapon(new EnderWeapon());

        setUltimate(new UltimateTalent(
                "Transmission!",
                "Instantly teleport to your &b&lTransmission &b&lBeacon &7and collect it for further use.",
                50
        )
                .setType(Talent.Type.MOVEMENT)
                .setItem(Material.SHULKER_SHELL)
                .setCooldownSec(20)
                .setSound(Sound.ENTITY_GUARDIAN_HURT_LAND, 0.75f));

    }

    @Override
    public boolean predicateUltimate(@Nonnull GamePlayer player) {
        return getSecondTalent().hasBeacon(player);
    }

    @Override
    public String predicateMessage(@Nonnull GamePlayer player) {
        return "Transmission Beacon is not placed!";
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        getSecondTalent().teleportToBeacon(player);
        return UltimateCallback.OK;
    }

    @EventHandler()
    public void handleTeleportEvent(EnderPearlTeleportEvent ev) {
        final GamePlayer player = ev.getPlayer();

        if (!validatePlayer(player.getPlayer())) {
            return;
        }

        getPassiveTalent().handleTeleport(player);
    }

    @Override
    public void onStart() {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                // Damage players in water
                Heroes.ENDER.getAlivePlayers().forEach(player -> {
                    if (!player.getPlayer().isInWater()) {
                        return;
                    }

                    player.damage(2.0d);
                    player.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 1.2f);
                });
            }
        }.runTaskTimer(0, 15);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TELEPORT_PEARL.getTalent();
    }

    @Override
    public TransmissionBeacon getSecondTalent() {
        return (TransmissionBeacon) Talents.TRANSMISSION_BEACON.getTalent();
    }

    @Override
    public EnderPassive getPassiveTalent() {
        return (EnderPassive) Talents.ENDER_PASSIVE.getTalent();
    }
}
