package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class BlindingCurse extends Talent {
	public BlindingCurse() {
		super("Blinding Curse", "Applies blinding curse to target player.", Material.INK_SAC);
		this.setCd(100);
	}

	@Override
	protected Response execute(Player player) {
		if (HeroHandle.DARK_MAGE.isUsingUltimate(player)) {
			return Response.error("Unable to use while in ultimate form!");
		}

		final Player target = Utils.getTargetPlayer(player, 35);
		final Location location = player.getLocation();
		if (target == null) {
			return Response.error("No valid target!");
		}

		Geometry.drawLine(
				location.add(0, 1, 0),
				target.getLocation().add(0, 1, 0),
				0.5, new WorldParticle(Particle.SQUID_INK)
		);

		PlayerLib.playSound(location, Sound.ENTITY_GLOW_SQUID_SQUIRT, 1.8f);
		PlayerLib.spawnParticle(location, Particle.SQUID_INK, 1, 0.3d, 0.3d, 0.3, 3f);

		PlayerLib.addEffect(target, PotionEffectType.BLINDNESS, 60, 10);
		PlayerLib.addEffect(target, PotionEffectType.BLINDNESS, 40, 1);

		Chat.sendMessage(target, "&c%s has cursed you with the Dark Magic!", player.getName());
		Chat.sendMessage(player, "&aYou have cursed %s with Dark Magic!", target.getName());

		return Response.OK;
	}
}