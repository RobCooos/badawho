package me.hapyl.fight.game.heroes.archive.alchemist;

import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Effect {

	private final String suffix;
	private final PotionEffect effect;
	private final boolean isPositive;

	public void affect(Player player, Player victim) {
	}

	public Effect(String suffix, PotionEffectType type, int duration, int level) {
		this.suffix = suffix;
		this.effect = type == null ? null : new PotionEffect(type, duration * 20, level);
		this.isPositive = duration == 30;
	}

	public String getSuffix() {
		return suffix;
	}

	public Effect(String suffix, int duration) {
		this(suffix, null, duration, 0);
	}

	public void applyEffects(Player player, Player victim) {
		this.affect(player, victim);

		if (effect != null) {
			PlayerLib.addEffect(victim, effect.getType(), effect.getDuration(), effect.getAmplifier());
		}

		Chat.sendMessage(victim, isPositive ?
				"&a&l☘ &aAlchemical Madness %s&a!".formatted(suffix) :
				"&c☠ Alchemical Madness %s&c!".formatted(suffix));
	}

}
