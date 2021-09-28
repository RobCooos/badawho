package kz.hapyl.fight.event;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.AbstractGamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.heroes.Heroes;
import kz.hapyl.fight.game.talents.ChargedTalent;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.ui.DamageIndicator;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class PlayerEvent implements Listener {


	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerJoin(PlayerJoinEvent ev) {
		final Player player = ev.getPlayer();
		Main.getPlugin().handlePlayer(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleItemDropEntity(EntityDropItemEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleItemDropPlayer(PlayerDropItemEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleFoodLevel(FoodLevelChangeEvent ev) {
		// Auto-Generated
		ev.setCancelled(true);
		ev.setFoodLevel(20);
	}

	@EventHandler()
	public void handleBlockPlace(BlockPlaceEvent ev) {
		if (ev.getPlayer().getGameMode() != GameMode.CREATIVE) {
			ev.setCancelled(true);
			ev.setBuild(false);
		}
	}

	@EventHandler()
	public void handleBlockBreak(BlockBreakEvent ev) {
		if (ev.getPlayer().getGameMode() != GameMode.CREATIVE) {
			ev.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleEntityRegainHealthEvent(EntityRegainHealthEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleItemClick(PlayerInteractEvent ev) {
		final Player player = ev.getPlayer();
		if (!Manager.current().isGameInProgress() || ev.getAction() == Action.PHYSICAL || ev.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		final ItemStack itemStack = player.getInventory().getItemInMainHand();
		if (itemStack.getType().isAir()) {
			return;
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleProjectileLand(ProjectileHitEvent ev) {
		final Projectile entity = ev.getEntity();
		if (entity.getType() == EntityType.ARROW) {
			entity.remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerSwapEvent(PlayerSwapHandItemsEvent ev) {
		ev.setCancelled(true);
		final Player player = ev.getPlayer();
		final Heroes hero = Manager.current().getSelectedHero(player);
		if (Manager.current().isGameInProgress()) {
			final GamePlayer gp = GamePlayer.getAlivePlayer(player);
			if (gp == null) {
				return;
			}
			if (gp.isUltimateReady()) {
				final UltimateTalent ultimate = hero.getHero().getUltimate();

				if (ultimate.hasCd(player)) {
					sendUltimateFailureMessage(player, "&cUltimate on cooldown for %ss.", BukkitUtils.roundTick(ultimate.getCdTimeLeft(player)));
					return;
				}

				if (!ultimate.predicate(player)) {
					sendUltimateFailureMessage(player, "&cUnable to use ultimate! " + ultimate.predicateMessage());
					return;
				}

				ultimate.execute0(player);
				ultimate.startCd(player);
				gp.setUltPoints(0);

				for (final Player online : Bukkit.getOnlinePlayers()) {
					Chat.broadcast("&b&l※ &b%s used &l%s&7!".formatted(online == player ? "You" : player.getName(), ultimate.getName()));
					PlayerLib.playSound(online, ultimate.getSound(), ultimate.getPitch());
				}
			}
			else {
				Chat.sendTitle(player, "&4&l※", "&cYour ultimate isn't ready!", 5, 15, 5);
				sendUltimateFailureMessage(player, "&cYour ultimate isn't ready!");
			}
		}
	}

	private void sendUltimateSuccessMessage(Player player, String str, Object... objects) {
		Chat.sendMessage(player, "&b&l※ &a" + Chat.format(str, objects));
	}

	private void sendUltimateFailureMessage(Player player, String str, Object... objects) {
		Chat.sendMessage(player, "&4&l※ &c" + Chat.format(str, objects));
		PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleDamage(EntityDamageEvent ev) {
		final Entity entity = ev.getEntity();
		LivingEntity damagerFinal = null;
		double damage = ev.getDamage();

		// let void damage be ignored
		final EntityDamageEvent.DamageCause cause = ev.getCause();
		if (!(entity instanceof LivingEntity livingEntity) || cause == EntityDamageEvent.DamageCause.VOID) {
			return;
		}

		// TODO: 027. 09/27/2021 -> calculate charge and decrease damage if non-full charge

		if (entity instanceof Player && !Manager.current().isGameInProgress()) {
			ev.setCancelled(true);
			return;
		}

		/** Pre event tests */

		if (livingEntity instanceof Player player) {
			final AbstractGamePlayer gamePlayer = GamePlayer.getPlayer(player);

			// fall damage
			if (cause == EntityDamageEvent.DamageCause.FALL && gamePlayer.hasEffect(GameEffectType.FALL_DAMAGE_RESISTANCE)) {
				gamePlayer.removeEffect(GameEffectType.FALL_DAMAGE_RESISTANCE);
				ev.setCancelled(true);
				return;
			}
		}

		// Calculate base damage
		if (ev instanceof EntityDamageByEntityEvent event) {
			final Entity damager = event.getDamager();

			// ignore all this if self damage (fall damage, explosion etc.)
			if (damager != entity) {
				if (damager instanceof Player playerDamager) {
					final Heroes hero = Manager.current().getSelectedHero(playerDamager);

					damage = getHeroDamageOr(playerDamager, 1.0d);

					// decrease damage if hitting with a bow
					final Material type = hero.getHero().getWeapon().getItem().getType();
					if (type == Material.BOW || type == Material.CROSSBOW) {
						damage *= 0.4d;
					}

					// assign the damager
					damagerFinal = playerDamager;
				}

				else if (damager instanceof Projectile projectile) {
					if (projectile.getShooter() instanceof Player playerDamager) {
						damage = getHeroDamageOr(playerDamager, 2.0d);

						// increase damage if fully charged shot
						if (projectile instanceof Arrow arrow) {
							if (arrow.isCritical()) {
								damage *= 1.75d;
							}
						}

						// assign the damager
						damagerFinal = playerDamager;
					}
				}
			}

			/** Apply modifiers for damager */
			if (damager instanceof LivingEntity living) {
				final PotionEffect effectStrength = living.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
				final PotionEffect effectWeakness = living.getPotionEffect(PotionEffectType.WEAKNESS);

				// add 30% of damage per strength level
				if (effectStrength != null) {
					damage += ((damage * 3 / 10) * (effectStrength.getAmplifier() + 1));
				}

				// --------------------------------------------reduce damage by 13% for each weakness level
				// reduce damage by half is has weakness effect
				if (effectWeakness != null) {
					damage /= 2;
				}

				/** Apply GameEffect for damager */
				if (living instanceof Player player) {
					final AbstractGamePlayer gp = GamePlayer.getPlayer(player);
					if (gp.hasEffect(GameEffectType.STUN)) {
						damage = 0.0d;
					}
				}

			}

			/** Apply modifiers for victim */
			{
				final PotionEffect effectResistance = livingEntity.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				// reduce damage by 85% if we have resistance
				if (effectResistance != null) {
					damage *= 0.15d;
				}

				// negate all damage is blocking
				if (livingEntity instanceof Player player) {
					if (player.isBlocking()) {
						damage = 0.0d;
					}

					/** Apply GameEffect for victim */
					final AbstractGamePlayer gp = GamePlayer.getPlayer(player);
					if (gp.hasEffect(GameEffectType.STUN)) {
						gp.removeEffect(GameEffectType.STUN);
					}

					if (gp.hasEffect(GameEffectType.VULNERABLE)) {
						damage *= 2.0d;
					}

				}
			}
		}

		// Process damager and victims hero damage processors

		// victim
		boolean cancelEvent = false;
		if (livingEntity instanceof Player player) {
			final DamageOutput output = getDamageOutput(player, damagerFinal, cause, damage, false);
			if (output != null) {
				damage = output.getDamage();
				cancelEvent = output.isCancelDamage();
			}
		}

		// damager
		if (damagerFinal instanceof Player player) {
			final DamageOutput output = getDamageOutput(player, livingEntity, cause, damage, true);
			if (output != null) {
				damage = output.getDamage();
				if (!cancelEvent) {
					cancelEvent = output.isCancelDamage();
				}
			}
		}

		// only damage entities other that the player
		ev.setDamage(livingEntity instanceof Player ? 0.0d : damage);

		if (cancelEvent) {
			ev.setCancelled(true);
			return;
		}

		// make sure not to kill player but instead put them in spectator
		if (entity instanceof Player player) {
			final GamePlayer gamePlayer = GamePlayer.getAlivePlayer(player);
			// if game player is null means the game is not in progress
			if (gamePlayer != null) {

				if (damage >= gamePlayer.getHealth()) {
					gamePlayer.die(true);
					ev.setCancelled(true);
					return;
				}
				gamePlayer.decreaseHealth(damage, damagerFinal);
			}

			// fail safe for actual health
			if (player.getHealth() <= 0.0d) {
				ev.setCancelled(true);
			}

		}

		// display damage
		if (damage > 0.0d) {
			new DamageIndicator(entity.getLocation(), damage);
		}


	}

	private DamageOutput getDamageOutput(Player player, LivingEntity entity, EntityDamageEvent.DamageCause cause, double damage, boolean asDamager) {
		if (Manager.current().isPlayerInGame(player)) {
			final Hero hero = Manager.current().getSelectedHero(player).getHero();
			final DamageInput input = new DamageInput(player, entity, cause, damage);
			return asDamager ? hero.processDamageAsDamager(input) : hero.processDamageAsVictim(input);
		}
		return null;
	}

	private boolean validatePlayer(Player player) {
		return Manager.current().isPlayerInGame(player);
	}

	private double getHeroDamageOr(Player player, double or) {
		final Heroes hero = Manager.current().getSelectedHero(player);
		// ye fuck enum saving but idk the better way ¯\_(ツ)_/¯
		if (player.getInventory().getHeldItemSlot() != 0
				|| player.getInventory().getItemInMainHand().getType().isAir()
				|| hero == null
				|| hero.getHero() == null
				|| hero.getHero().getWeapon() == null
				|| hero.getHero().getWeapon().getItem().getType() != player.getInventory().getItemInMainHand().getType()) {
			return or;
		}
		return hero.getHero().getWeapon().getDamage();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void handleInventoryClickEvent(InventoryClickEvent ev) {
		if (Manager.current().isGameInProgress()) {
			ev.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerClick(PlayerItemHeldEvent ev) {
		if (!Manager.current().isGameInProgress()) {
			return;
		}

		// 1 -> ability first
		// 2 -> ability second
		// 3-5 -> complex abilities
		// 0 def weapon slot

		final int newSlot = ev.getNewSlot();
		if (newSlot <= 0 || newSlot > 5) {
			return;
		}

		final Player player = ev.getPlayer();
		final Hero hero = Manager.current().getSelectedHero(player).getHero();

		// don't care if talent is null, either not a talent or not complete
		if (Manager.current().getTalent(hero, newSlot) == null) {
			return;
		}

		// Execute talent
		checkAndExecuteTalent(player, Manager.current().getTalent(hero, newSlot), newSlot);

		ev.setCancelled(true);
		player.getInventory().setHeldItemSlot(0);

	}

	@EventHandler()
	public void handleInteraction(PlayerInteractEvent ev) {
		final Player player = ev.getPlayer();
		if (Manager.current().isGameInProgress() || player.getGameMode() != GameMode.CREATIVE) {
			ev.setCancelled(true);
		}
	}

	@EventHandler()
	public void handleMovement(PlayerMoveEvent ev) {
		final Player player = ev.getPlayer();
		final Location from = ev.getFrom();
		final Location to = ev.getTo();

		if (Manager.current().isGameInProgress()) {
			final AbstractGamePlayer gp = GamePlayer.getPlayer(player);

			// impl for amnesia
			if (gp.hasEffect(GameEffectType.AMNESIA)) {
				if (to == null || from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
					return;
				}
				final double pushSpeed = player.isSneaking() ? 0.05d : 0.1d;
				player.setVelocity(new Vector(new Random().nextBoolean() ? pushSpeed : -pushSpeed, -0.2723, new Random().nextBoolean() ?
						pushSpeed :
						-pushSpeed));
			}
		}

	}

	@EventHandler()
	public void handleSlotClick(InventoryClickEvent ev) {
		if (ev.getClick() == ClickType.DROP && ev.getWhoClicked() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) {
			ev.setCancelled(true);
			Chat.sendMessage(player, "&aClicked %s slot.", ev.getRawSlot());
			PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 2.0f);
		}
	}

	private void checkAndExecuteTalent(Player player, Talent talent, int slot) {
		// null check
		if (talent == null) {
			Chat.sendMessage(player, "&cNullPointerException: talent is null");
			return;
		}

		// cooldown check
		if (talent.hasCd(player)) {
			Chat.sendMessage(player, "&cTalent on cooldown for %ss.", BukkitUtils.roundTick(talent.getCdTimeLeft(player)));
			return;
		}

		// charge check
		if (talent instanceof ChargedTalent chargedTalent) {
			if (chargedTalent.getChargedAvailable(player) <= 0) {
				Chat.sendMessage(player, "&cOut of charges!");
				return;
			}
			chargedTalent.removeChargeAndStartCooldown(player, slot);
		}

		final Response response = talent.execute0(player);

		if (response.isError()) {
			Chat.sendMessage(player, "&cCannot use this! &l" + response.getReason());
			return;
		}

		talent.startCd(player);
	}

}
