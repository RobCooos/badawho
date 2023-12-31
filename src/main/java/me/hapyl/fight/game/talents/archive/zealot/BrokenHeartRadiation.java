package me.hapyl.fight.game.talents.archive.zealot;

import me.hapyl.fight.fx.beam.Quadrant;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BrokenHeartRadiation extends Talent {

    @DisplayField private final double beamLength = 4;
    @DisplayField private final double beamDamage = 10.0d;

    @DisplayField(scaleFactor = 100.0d, suffix = "%", suffixSpace = false)
    private final double mendingReduction = 0.5d;
    @DisplayField(scaleFactor = 100.0d, suffix = "%", suffixSpace = false)
    private final double defenseReduction = 0.33d;

    @DisplayField private final int effectDuration = 250;

    public BrokenHeartRadiation() {
        super("Broken Heart Radiation");

        setDescription("""
                Create 4 radiation beams that spin around you for {duration}.
                                
                If a beam touches an enemy, it deals &c{beamDamage} ❤&7 damage and reduces %s by &c{mendingReduction}&7 and %s by &c{defenseReduction}&7 for &b{effectDuration}.
                """, AttributeType.MENDING, AttributeType.DEFENSE);

        setDurationSec(3);
        setCooldownSec(20);

        setItem(Material.TWISTING_VINES);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Quadrant quadrant = new Quadrant(player.getLocation()) {
            @Override
            public void onTouch(@Nonnull LivingGameEntity entity) {
                if (entity.equals(player)) {
                    return;
                }
                final EntityAttributes attributes = entity.getAttributes();

                entity.damage(beamDamage, player, EnumDamageCause.RADIATION);

                attributes.decreaseTemporary(Temper.RADIATION, AttributeType.MENDING, mendingReduction, effectDuration);
                attributes.decreaseTemporary(Temper.RADIATION, AttributeType.DEFENSE, defenseReduction, effectDuration);
            }

            @Override
            public void onTick() {
                teleport(player.getLocation());

                if (getTick() >= getDuration()) {
                    remove();
                }
            }
        };

        quadrant.setDistance(beamLength);
        quadrant.runTaskTimer(0, 1);

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.0f);

        return Response.OK;
    }

}
