package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.EffectParticle;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Particle;

public class RespawnResistance extends GameEffect {

    public RespawnResistance() {
        super("Respawn Resistance");
        setEffectParticle(new EffectParticle(Particle.CRIT_MAGIC, 5, 0.25d, 0.5d, 0.25d, 0.1f));
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        entity.setInvulnerable(true);
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        entity.setInvulnerable(false);
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
        if (tick == 5) {
            displayParticles(entity.getLocation(), entity.getEntity());
        }
    }
}
