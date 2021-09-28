package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.talents.storage.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public enum Talents {

	// Archer
	TRIPLE_SHOT(new TripleShot()),
	SHOCK_DARK(new ShockDark()),
	HAWKEYE_ARROW(new PassiveTalent(
			"Hawkeye Arrow",
			"Fully charged shots while sneaking have 25% chance to fire hawkeye arrow which homes to nearby enemies.",
			Material.ENDER_EYE
	)),

	// Alchemist
	POTION(new RandomPotion()),
	CAULDRON(new CauldronAbility()),
	INTOXICATION(new PassiveTalent(
			"Intoxication",
			"Drinking potions will increase &eIntoxication &7level that will decrease constantly. Keeping an eye on &eIntoxication &7level is a good idea, who knows what can happen...",
			Material.DRAGON_BREATH
	)),

	// Moonwalker
	MOONSLITE_PILLAR(new MoonslitePillar()),
	MOONSLITE_BOMB(new MoonSliteBomb()),
	TARGET(new PassiveTalent(
			"Target",
			"Hold &e&lSNEAK &7to show your target block. Most of your abilities will spawn at the target block.",
			Material.SPECTRAL_ARROW
	)),

	// Hercules
	HERCULES_DASH(new HerculesShift()),
	HERCULES_UPDRAFT(new HerculesJump()),
	PLUNGE(new PassiveTalent(
			"Plunge",
			"While airborne, &e&lSNEAK &7to perform plunging attack, dealing damage to nearby enemies.",
			Material.COARSE_DIRT
	)),

	// Mage
	MAGE_TRANSMISSION(new MageTransmission()),
	MAGE_TODO(null),
	SOUL_HARVEST(new PassiveTalent(
			"Soul Harvest",
			"Deal &bmelee &7damage to gain soul fragment as fuel for your &e&lSoul &e&lEater&7's range attacks.",
			Material.SKELETON_SPAWN_EGG
	)),

	// Pytaria
	FLOWER_ESCAPE(new FlowerEscape()),
	FLOWER_BREEZE(new FlowerBreeze()),
	EXCELLENCY(new PassiveTalent(
			"Excellency",
			"Pytaria gains &b30% &7damage boost per each &c10% &7of her missing &chealth &c❤&7.",
			Material.ROSE_BUSH
	)),

	// Troll
	TROLL_SPIN(new TrollSpin()),
	REPULSOR(new Repulsor()),
	TROLL_PASSIVE(new PassiveTalent("idk yet", "idk yet", Material.STONE)),

	// Nightmare
	PARANOIA(new Paranoia()),
	SHADOW_SHIFT(new ShadowShift()),
	IN_THE_SHADOWS(new PassiveTalent("In the Shadows", "While in moody light, your &b&lSpeed &7and &c&lDamage &7is increased.", Material.DRIED_KELP)),

	// Dr. Ed
	CONFUSION_POTION(new ConfusionPotion()),
	MISSING_TALENT_0(null),
	MISSING_TALENT_1(new PassiveTalent("missing", "missing", Material.BEDROCK)),

	// Ender
	TELEPORT_PEARL(new TeleportPearl()),
	TRANSMISSION_BEACON(new TransmissionBeacon()),
	ENDERMAN_FLESH(new PassiveTalent("Fears of Enderman", "While in water, you will constantly take damage.", Material.WATER_BUCKET)),

	// Spark
	SPARK_MOLOTOV(new Molotov()),
	SPARK_FLASH(new SparkFlash()),
	FIRE_GUY(new PassiveTalent("Fire Guy", "You're completely immune to &clava &7and &cfire &7damage.", Material.LAVA_BUCKET)),

	// SA
	SHADOW_PRISM(new ShadowPrism()),
	SHROUDED_STEP(new ShroudedStep()),
	SECRET_SHADOW_WARRIOR_TECHNIQUE(new PassiveTalent(
			"Dark Cover",
			"As a assassin, you have mastered ability to stay in shadows. While &e&lSNEAKING&7, you become completely invisible, but cannot deal damage and your footsteps are visible.",
			Material.NETHERITE_CHESTPLATE
	)),

	// Witcher
	AARD(new Aard()),
	IGNY(new Igny()),
	KVEN(new Kven()),
	AKCIY(new Akciy()),
	IRDEN(new Irden()),
	COMBO_SYSTEM(new PassiveTalent(
			"Combo",
			"Dealing continuous damage to the same target will increase your combo, greater combo hits deals increased damage.",
			Material.SKELETON_SKULL
	)),

	// Vortex
	VORTEX_STAR(new VortexStar()),
	STAR_ALIGNER(new StarAligner()),
	MISSING_TALENT(null),

	// Freazly
	ICE_CONE(new IceCone()),

	// test
	TestChargeTalent(new TestChargeTalent()),

	;

	private final Talent talent;

	Talents(Talent talent) {
		if (talent instanceof UltimateTalent) {
			throw new IllegalArgumentException("ultimate talent enum initiation");
		}
		this.talent = talent;
		if (talent instanceof Listener listener) {
			Main.getPlugin().addEvent(listener);
		}
	}

	public void startCd(Player player) {
		getTalent().startCd(player);
	}

	public String getName() {
		return getTalent().getName();
	}

	public Talent getTalent() {
		return talent;
	}
}
