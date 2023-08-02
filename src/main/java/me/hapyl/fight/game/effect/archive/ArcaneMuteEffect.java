package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

public class ArcaneMuteEffect extends GameEffect {
    public ArcaneMuteEffect() {
        super("Arcane Mute");
        setDescription(Talents.ARCANE_MUTE.getTalent().getDescription());
        setPositive(false);
        setTalentBlocking(true);
    }

    @Override
    public void onStart(LivingGameEntity entity) {
        if (!(entity instanceof Player player)) {
            return;
        }

        Chat.sendTitle(player, "&e&l☠", "&7Shhhhh...", 10, 20, 10);
    }

    @Override
    public void onStop(LivingGameEntity entity) {
        entity.sendMessage("&e&l☠ &aArcane Mute is gone.");
    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {
    }
}