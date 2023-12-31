package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.Material;
import org.bukkit.Particle;

public class BleedEffect extends GameEffect {

    private final double damage = 2.0d;

    public BleedEffect() {
        super("Bleed");
        setPositive(false);
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        entity.sendMessage("&c&l∲ &7You are bleeding!");
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        entity.sendMessage("&c&l∲ &aThe bleeding has stopped!");
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
        if (tick == 0) {
            entity.damage(damage, EnumDamageCause.BLEED);
            entity.getWorld()
                    .spawnParticle(
                            Particle.BLOCK_CRACK,
                            entity.getLocation(),
                            10,
                            0.5d,
                            0.5d,
                            0.5d,
                            0.0d,
                            Material.REDSTONE_BLOCK.createBlockData()
                    );
        }
    }
}
