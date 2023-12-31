package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jna.platform.win32.OaIdl;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.Discharge;
import me.hapyl.fight.game.talents.archive.TestChargeTalent;
import me.hapyl.fight.game.talents.archive.alchemist.CauldronAbility;
import me.hapyl.fight.game.talents.archive.alchemist.RandomPotion;
import me.hapyl.fight.game.talents.archive.archer.ShockDark;
import me.hapyl.fight.game.talents.archive.archer.TripleShot;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodCup;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodfiendPassive;
import me.hapyl.fight.game.talents.archive.bloodfiend.TwinClaws;
import me.hapyl.fight.game.talents.archive.bloodfiend.candlebane.CandlebaneTalent;
import me.hapyl.fight.game.talents.archive.bloodfiend.chalice.BloodChaliceTalent;
import me.hapyl.fight.game.talents.archive.bounty_hunter.GrappleHookTalent;
import me.hapyl.fight.game.talents.archive.bounty_hunter.ShortyShotgun;
import me.hapyl.fight.game.talents.archive.dark_mage.BlindingCurse;
import me.hapyl.fight.game.talents.archive.dark_mage.HealingAura;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.archive.dark_mage.SlowingAura;
import me.hapyl.fight.game.talents.archive.doctor.ConfusionPotion;
import me.hapyl.fight.game.talents.archive.doctor.HarvestBlocks;
import me.hapyl.fight.game.talents.archive.ender.EnderPassive;
import me.hapyl.fight.game.talents.archive.ender.TeleportPearl;
import me.hapyl.fight.game.talents.archive.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.archive.engineer.EngineerRecall;
import me.hapyl.fight.game.talents.archive.engineer.EngineerSentry;
import me.hapyl.fight.game.talents.archive.engineer.EngineerTurret;
import me.hapyl.fight.game.talents.archive.frostbite.IceBarrier;
import me.hapyl.fight.game.talents.archive.frostbite.Icicles;
import me.hapyl.fight.game.talents.archive.frostbite.IceCageTalent;
import me.hapyl.fight.game.talents.archive.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.archive.harbinger.TidalWaveTalent;
import me.hapyl.fight.game.talents.archive.healer.HealingOrb;
import me.hapyl.fight.game.talents.archive.healer.ReviveTotem;
import me.hapyl.fight.game.talents.archive.heavy_knight.Slash;
import me.hapyl.fight.game.talents.archive.heavy_knight.Updraft;
import me.hapyl.fight.game.talents.archive.heavy_knight.Uppercut;
import me.hapyl.fight.game.talents.archive.hercules.HerculesJump;
import me.hapyl.fight.game.talents.archive.hercules.HerculesShift;
import me.hapyl.fight.game.talents.archive.juju.ArrowShield;
import me.hapyl.fight.game.talents.archive.juju.Climb;
import me.hapyl.fight.game.talents.archive.juju.PoisonZone;
import me.hapyl.fight.game.talents.archive.juju.TricksOfTheJungle;
import me.hapyl.fight.game.talents.archive.km.LaserEye;
import me.hapyl.fight.game.talents.archive.knight.SlownessPotion;
import me.hapyl.fight.game.talents.archive.knight.Spear;
import me.hapyl.fight.game.talents.archive.knight.StoneCastle;
import me.hapyl.fight.game.talents.archive.librarian.BlackHole;
import me.hapyl.fight.game.talents.archive.librarian.EntityDarkness;
import me.hapyl.fight.game.talents.archive.librarian.LibrarianShield;
import me.hapyl.fight.game.talents.archive.librarian.WeaponDarkness;
import me.hapyl.fight.game.talents.archive.mage.ArcaneMute;
import me.hapyl.fight.game.talents.archive.mage.MageTransmission;
import me.hapyl.fight.game.talents.archive.moonwalker.GravityZone;
import me.hapyl.fight.game.talents.archive.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.talents.archive.moonwalker.MoonSliteBomb;
import me.hapyl.fight.game.talents.archive.nightmare.Paranoia;
import me.hapyl.fight.game.talents.archive.nightmare.ShadowShift;
import me.hapyl.fight.game.talents.archive.ninja.NinjaDash;
import me.hapyl.fight.game.talents.archive.ninja.NinjaSmoke;
import me.hapyl.fight.game.talents.archive.orc.OrcAxe;
import me.hapyl.fight.game.talents.archive.orc.OrcGrowl;
import me.hapyl.fight.game.talents.archive.pytaria.FlowerBreeze;
import me.hapyl.fight.game.talents.archive.pytaria.FlowerEscape;
import me.hapyl.fight.game.talents.archive.shadow_assassin.*;
import me.hapyl.fight.game.talents.archive.shaman.ResonanceType;
import me.hapyl.fight.game.talents.archive.shaman.Totem;
import me.hapyl.fight.game.talents.archive.shaman.TotemTalent;
import me.hapyl.fight.game.talents.archive.shark.Submerge;
import me.hapyl.fight.game.talents.archive.shark.Whirlpool;
import me.hapyl.fight.game.talents.archive.spark.Molotov;
import me.hapyl.fight.game.talents.archive.spark.SparkFlash;
import me.hapyl.fight.game.talents.archive.sun.SyntheticSun;
import me.hapyl.fight.game.talents.archive.swooper.BlastPack;
import me.hapyl.fight.game.talents.archive.swooper.Blink;
import me.hapyl.fight.game.talents.archive.taker.DeathSwap;
import me.hapyl.fight.game.talents.archive.taker.FatalReap;
import me.hapyl.fight.game.talents.archive.taker.SpiritualBonesPassive;
import me.hapyl.fight.game.talents.archive.tamer.MineOBall;
import me.hapyl.fight.game.talents.archive.techie.TrapCage;
import me.hapyl.fight.game.talents.archive.techie.TrapWire;
import me.hapyl.fight.game.talents.archive.troll.Repulsor;
import me.hapyl.fight.game.talents.archive.troll.TrollSpin;
import me.hapyl.fight.game.talents.archive.vampire.BatSwarm;
import me.hapyl.fight.game.talents.archive.vampire.VampirePet;
import me.hapyl.fight.game.talents.archive.vortex.StarAligner;
import me.hapyl.fight.game.talents.archive.vortex.VortexSlash;
import me.hapyl.fight.game.talents.archive.vortex.VortexStar;
import me.hapyl.fight.game.talents.archive.witcher.*;
import me.hapyl.fight.game.talents.archive.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.archive.zealot.MaledictionVeil;
import me.hapyl.fight.game.talents.archive.zealot.MalevolentHitshield;
import me.hapyl.fight.util.Compute;
import me.hapyl.spigotutils.module.util.BFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * This is a registry for all talents that are
 * executable, no matter if is used or not.
 *
 * <p>
 * Stores ENUM(Talent) where Talent is a class instance of a talent.
 * </p>
 *
 * <p>
 * Talents <b>MUST</b> be stored in here, otherwise they will not be registered.
 * </p>
 * <p>
 * To get actual talent class, use {@link #getTalent(Class)}.
 *
 * @author hapyl
 */
public enum Talents {

    /**
     * {@link me.hapyl.fight.game.heroes.archive.archer.Archer}
     */
    TRIPLE_SHOT(new TripleShot()),
    SHOCK_DARK(new ShockDark()),
    HAWKEYE_ARROW(new PassiveTalent(
            "Hawkeye Arrow",
            "Fully charged shots while sneaking have &b25%&7 chance to fire a hawkeye arrow that homes to nearby enemies.",
            Material.ENDER_EYE,
            Talent.Type.DAMAGE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.alchemist.Alchemist}
     */
    POTION(new RandomPotion()),
    CAULDRON(new CauldronAbility()),
    INTOXICATION(new PassiveTalent(
            "Intoxication", """
            Drinking potions will increase &eIntoxication &7level that will decrease constantly.
                        
            Having high &eIntoxication&7 levels isn't good for your body!
            """,
            Material.DRAGON_BREATH,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.moonwalker.Moonwalker}
     */
    MOONSLITE_PILLAR(new MoonPillarTalent()),
    @Deprecated MOONSLITE_BOMB(new MoonSliteBomb()),
    MOON_GRAVITY(new GravityZone()),
    TARGET(new PassiveTalent("Space Suit", "Your suit grants you slow falling ability.", Material.FEATHER, Talent.Type.ENHANCE)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.hercules.Hercules}
     */
    HERCULES_DASH(new HerculesShift()),
    HERCULES_UPDRAFT(new HerculesJump()),
    PLUNGE(new PassiveTalent(
            "Plunge",
            "While airborne, &e&lSNEAK &7to perform plunging attack, dealing damage to nearby enemies.",
            Material.COARSE_DIRT,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.mage.Mage}
     */
    MAGE_TRANSMISSION(new MageTransmission()),
    ARCANE_MUTE(new ArcaneMute()),
    SOUL_HARVEST(new PassiveTalent(
            "Soul Harvest",
            "Deal &bmelee &7damage to gain soul fragment as fuel for your &e&lSoul &e&lEater&7's range attacks.",
            Material.SKELETON_SPAWN_EGG,
            Talent.Type.IMPAIR
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.pytaria.Pytaria}
     */
    FLOWER_ESCAPE(new FlowerEscape()),
    FLOWER_BREEZE(new FlowerBreeze()),
    EXCELLENCY(new PassiveTalent(
            "Excellency",
            "The less &chealth&7 Pytaria has, the more her %s and %s increases. But her %s significantly decreases.".formatted(
                    AttributeType.ATTACK,
                    AttributeType.CRIT_CHANCE,
                    AttributeType.DEFENSE
            ), Material.ROSE_BUSH,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.troll.Troll}
     */
    TROLL_SPIN(new TrollSpin()),
    REPULSOR(new Repulsor()),
    TROLL_PASSIVE(new PassiveTalent(
            "Last Laugh",
            "Your hits have &b0.1% &7chance to instantly kill enemy.",
            Material.BLAZE_POWDER,
            Talent.Type.ENHANCE
    )),

    // Tamer
    MINE_O_BALL(new MineOBall()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.nightmare.Nightmare}
     */
    PARANOIA(new Paranoia()),
    SHADOW_SHIFT(new ShadowShift()),
    IN_THE_SHADOWS(new PassiveTalent(
            "In the Shadows",
            "While in moody light, your %s&7 and %s&7 increases.".formatted(AttributeType.ATTACK, AttributeType.SPEED),
            Material.DRIED_KELP,
            Talent.Type.ENHANCE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.doctor.DrEd}
     */
    CONFUSION_POTION(new ConfusionPotion()),
    HARVEST(new HarvestBlocks()),
    BLOCK_SHIELD(new PassiveTalent(
            "Block Maelstrom",
            "Creates a block that orbits around you, dealing damage based on the element upon contact with opponents.____&7Refreshes every &b10s&7.",
            Material.BRICK,
            Talent.Type.DEFENSE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.ender.Ender}
     */
    TELEPORT_PEARL(new TeleportPearl()),
    TRANSMISSION_BEACON(new TransmissionBeacon()),
    ENDER_PASSIVE(new EnderPassive()),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.spark.Spark}
     */
    SPARK_MOLOTOV(new Molotov()),
    SPARK_FLASH(new SparkFlash()),
    FIRE_GUY(new PassiveTalent("Fire Guy", "You're completely immune to &clava &7and &cfire &7damage.", Material.LAVA_BUCKET)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.shadow_assassin.ShadowAssassin}
     */
    @Deprecated SHADOW_PRISM(new ShadowPrism()),
    @Deprecated SHROUDED_STEP(new ShroudedStep()),
    @Deprecated SECRET_SHADOW_WARRIOR_TECHNIQUE(new PassiveTalent(
            "Dark Cover",
            "As an assassin, you have mastered the ability to stay in the shadows.____While &e&lSNEAKING&7, you become completely invisible, but cannot deal damage and your footsteps are visible.",
            Material.NETHERITE_CHESTPLATE
    )),

    SHADOW_SWITCH(new ShadowSwitch()),
    DARK_COVER(new DarkCover()),
    SHADOW_ASSASSIN_CLONE(new ShadowAssassinClone()),
    SHADOW_ENERGY(new PassiveTalent(
            "Shadow Energy", """
            Accumulate %1$s while using abilities in &9Stealth&7 mode.
                        
            Spend %1$s to use empowered abilities in &cFury&7 mode.
            """.formatted(Named.SHADOW_ENERGY),
            Material.CHORUS_FRUIT,
            Talent.Type.ENHANCE
    )),

    // Witcher
    AARD(new Aard()),
    IGNY(new Igny()),
    KVEN(new Kven()),
    AKCIY(new Akciy()),
    IRDEN(new Irden()),
    COMBO_SYSTEM(new PassiveTalent(
            "Combo",
            "Dealing &bcontinuous damage&7 to the &bsame target&7 will increase your combo.____Greater combo hits deal &cincreased damage&7.",
            Material.SKELETON_SKULL
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.vortex.Vortex}
     */
    VORTEX_SLASH(new VortexSlash()),
    VORTEX_STAR(new VortexStar()),
    STAR_ALIGNER(new StarAligner()),
    LIKE_A_DREAM(new PassiveTalent(
            "Like a Dream", """
            Using %1$s grants you one stack of %2$s&7.
                        
            Each %2$s&7 stack increases &6Astral Slash&7 DMG by &b15%%&7.
                        
            &8;;Lose one stack after not gaining a stack for 5s.
            """.formatted(Talents.STAR_ALIGNER.getName(), Named.ASTRAL_SPARK),
            Material.RED_BED
    )),

    @Deprecated EYES_OF_THE_GALAXY(new PassiveTalent(
            "Eyes of the Galaxy",
            "Astral Stars you place will glow different colors:____&eYellow &7indicates a placed star.____&bAqua &7indicates closest star that will be consumed upon teleport.____&aGreen &7indicates star you will blink to upon teleport.",
            Material.ENDER_EYE
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.frostbite.Freazly}
     */
    ICICLES(new Icicles()),
    ICE_CAGE(new IceCageTalent()),
    ICE_BARRIER(new IceBarrier()),
    CHILL_AURA(new PassiveTalent("Chill Aura", """
            You emmit a &bchill aura&7, that &bslows&7 enemies in small AoE.
            """, Material.LIGHT_BLUE_DYE)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.dark_mage.DarkMage}
     */
    BLINDING_CURSE(new BlindingCurse()),
    SLOWING_AURA(new SlowingAura()),
    HEALING_AURA(new HealingAura()),
    SHADOW_CLONE(new ShadowClone()),
    DARK_MAGE_PASSIVE(new PassiveTalent("Wither Blood", """
            Upon taking &cdamage&7, there is a small chance to &8wither&7 the attacker.
            """, Material.WITHER_ROSE, Talent.Type.IMPAIR)),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.knight.BlastKnight}
     */
    STONE_CASTLE(new StoneCastle()),
    DISCHARGE(new Discharge()),
    @Deprecated SPEAR(new Spear()),
    @Deprecated SLOWNESS_POTION(new SlownessPotion()),
    SHIELDED(new PassiveTalent(
            "Shielded",
            "Blocking damage using your shield will add a charge to it, up to &b10&7 charges.____Once charged, it will explode and create &bNova Explosion&7, dealing moderate damage and knocking back nearby opponents.",
            Material.SHIELD
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.ninja.Ninja}
     */
    NINJA_DASH(new NinjaDash()),
    NINJA_SMOKE(new NinjaSmoke()),
    FLEET_FOOT(new PassiveTalent(
            "Ninja Style", """
            Ninjas are fast and agile.
                        
            You gain %s &7boost, can &bdouble jump&7 and don't take &3fall&7 damage!
            """.formatted(AttributeType.SPEED), Material.ELYTRA
    )),

    /**
     * {@link me.hapyl.fight.game.heroes.archive.taker.Taker}
     */
    FATAL_REAP(new FatalReap()),
    DEATH_SWAP(new DeathSwap()),
    SPIRITUAL_BONES(new SpiritualBonesPassive()),

    // Juju
    ARROW_SHIELD(new ArrowShield()),
    @Deprecated CLIMB(new Climb()),
    TRICKS_OF_THE_JUNGLE(new TricksOfTheJungle()),
    POISON_ZONE(new PoisonZone()),
    JUJU_PASSIVE(new PassiveTalent(
            "Climb", """
            Raised by the jungle, Juju mastered the ability to climb anything.
                        
            &e&lJUMP &7near the wall to grab onto it.
            &e&lSNEAK &7to climb higher.
            """, Material.LEATHER_BOOTS
    )),

    // Swooper
    BLAST_PACK(new BlastPack()),
    BLINK(new Blink()),
    SNIPER_SCOPE(new PassiveTalent(
            "Path Writer",
            "Your last &b5&7 seconds of life are stored in the path writer.",
            Material.STRING
    )),

    // Shark
    SUBMERGE(new Submerge()),
    WHIRLPOOL(new Whirlpool()),
    CLAW_CRITICAL(new PassiveTalent(
            "Oceanborn/Sturdy Claws",
            "&b&lOceanborn:__While in water, your speed and damage is drastically increased.____&b&lSturdy Claws:__Critical hits summons an ancient creature from beneath that deals extra damage and heals you!",
            Material.MILK_BUCKET
    )),

    // Librarian
    BLACK_HOLE(new BlackHole()),
    ENTITY_DARKNESS(new EntityDarkness()),
    LIBRARIAN_SHIELD(new LibrarianShield()),
    WEAPON_DARKNESS(new WeaponDarkness()),

    // Harbinger
    STANCE(new MeleeStance()),
    TIDAL_WAVE(new TidalWaveTalent()),
    RIPTIDE(new PassiveTalent(
            "Riptide",
            "Fully charged shots in &e&lRange Stance&7 applies &bRiptide &7effect to opponents.____Hitting opponents affected by &bRiptide&7 with &nfully charged shots&7 or in &e&lMelee &e&lStance &7executes &bRiptide Slash&7 that rapidly deals damage.____&bRiptide Slash&7 can be executed once every &b2.5s&7 per opponent.",
            Material.HEART_OF_THE_SEA
    )),

    // Techie
    TRAP_CAGE(new TrapCage()),
    TRAP_WIRE(new TrapWire()),
    NEURAL_THEFT(new PassiveTalent(
            "Neural Theft/CYber Hack",
            "&b&lNeural Theft__Every &b10s&7, hacks into opponents revealing their location and health.____&b&lCYber Hack:__&7A small virus that reveals opponent's location, slows them and applies &6&lVulnerability&7 for short duration.",
            Material.CHAINMAIL_HELMET
    )),

    // Killing Machine
    LASER_EYE(new LaserEye()),
    //GRENADE(new ShellGrande()),

    // Shaman
    TOTEM(new Totem()),
    TOTEM_SLOWING_AURA(new TotemTalent(ResonanceType.SLOWING_AURA, 10)),
    TOTEM_HEALING_AURA(new TotemTalent(ResonanceType.HEALING_AURA, 12)),
    TOTEM_CYCLONE_AURA(new TotemTalent(ResonanceType.CYCLONE_AURA, 16)),
    TOTEM_ACCELERATION_AURA(new TotemTalent(ResonanceType.ACCELERATING_AURA, 20)),

    // Healer
    HEALING_ORB(new HealingOrb()),
    REVIVE_TOTEM(new ReviveTotem()),
    REVIVE(new PassiveTalent(
            "Revive",
            "When taking lethal damage, instead of dying, become a ghost and seek placed &bRevive Catalyst&7 to revive yourself. Once you use &bRevive Catalyst&7, it will be destroyed. All your catalysts will be highlighted for enemy players.",
            Material.GHAST_TEAR
    )),

    // Vampire
    VAMPIRE_PET(new VampirePet()),
    BAT_SWARM(new BatSwarm()),
    BLOOD_THIRST(new PassiveTalent(
            "Blood Thirst", """
            &c;;Your health is constantly drained.
                        
            Whenever you or your bats hit an opponent, you will gain a stack of &bblood&7, up to &b10&7 stacks.
                        
            Drink the blood to &cincrease your damage&7 and &cheal yourself&7.
                        
            &6;;Healing, damage boost, duration and cooldown are based on the number of stacks consumed.
            """,
            Material.REDSTONE
    )),

    // Bounty Hunter
    SHORTY(new ShortyShotgun()),
    GRAPPLE(new GrappleHookTalent()),
    SMOKE_BOMB(new PassiveTalent("Smoke Bomb", """
            Whenever your health falls below &c50%&7, you gain a &eSmoke Bomb&7.
            Throw it to create a smoke field that &bblinds&7 everyone inside it and grant you a &bspeed boost&7.
            """, Material.ENDERMAN_SPAWN_EGG
    )),

    // Heavy Knight
    UPPERCUT(new Uppercut()),
    UPDRAFT(new Updraft()),
    SLASH(new Slash()),

    // Orc
    ORC_GROWN(new OrcGrowl()),
    ORC_AXE(new OrcAxe()),
    ORC_PASSIVE(new PassiveTalent("Don't Anger Me/Orc's Blood", format("""
            &b&lDon't Anger Me
            Taking continuous damage within the set time window will trigger {} for &b3s&7.
                        
            &b&lOrc's Blood
            Negative effects are 50% less effective.
            """, Named.BERSERK), Material.FERMENTED_SPIDER_EYE)),

    // Engineer
    ENGINEER_SENTRY(new EngineerSentry()),
    ENGINEER_TURRET(new EngineerTurret()),
    ENGINEER_RECALL(new EngineerRecall()),
    ENGINEER_PASSIVE(new PassiveTalent("Magnetic Attraction", """
            Every few seconds you'll receive an Iron Ingot. 
            Use it to build stuff!""", Material.IRON_INGOT)),

    // Bloodfiend
    TWIN_CLAWS(new TwinClaws()),
    CANDLEBANE(new CandlebaneTalent()),
    BLOOD_CHALICE(new BloodChaliceTalent()),
    BLOOD_CUP(new BloodCup()),
    SUCCULENCE(new BloodfiendPassive()),

    // Zealot
    BROKEN_HEART_RADIATION(new BrokenHeartRadiation()),
    MALEVOLENT_HITSHIELD(new MalevolentHitshield()),
    MALEDICTION_VEIL(new MaledictionVeil()),

    // ???,
    SYNTHETIC_SUN(new SyntheticSun()),

    // test (keep last),
    TestChargeTalent(new TestChargeTalent());

    private final static Map<Talent, Talents> HANDLE_TO_ENUM;
    private final static Map<Talent.Type, List<Talents>> BY_TYPE;

    static {
        HANDLE_TO_ENUM = Maps.newHashMap();
        BY_TYPE = Maps.newHashMap();

        for (Talents enumTalent : values()) {
            final Talent talent = enumTalent.talent;

            if (talent == null) {
                continue;
            }

            HANDLE_TO_ENUM.put(talent, enumTalent);
            BY_TYPE.compute(talent.getType(), Compute.listAdd(enumTalent));
        }
    }

    private final Talent talent;

    Talents(Talent talent) {
        if (talent instanceof UltimateTalent) {
            throw new IllegalArgumentException("ultimate talent enum initiation");
        }
        this.talent = talent;
        if (talent instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getPlugin());
        }
    }

    public void startCd(GamePlayer player) {
        getTalent().startCd(player);
    }

    public String getName() {
        return getTalent().getName();
    }

    /**
     * Returns a handle of a talent.
     * <p>
     * Note that this method only returns a base handle,
     * for specific hero handles, use {@link #getTalent(Class)}.
     *
     * @return handle of a talent.
     */
    @Nonnull
    public Talent getTalent() {
        return talent;
    }

    /**
     * Returns a handle of a talent.
     * <p>
     * This method tries to cast the handle to the specified class.
     *
     * @param cast - Cast to.
     * @return handle of a talent.
     * @throws IllegalArgumentException if the cast is invalid.
     */
    @Nonnull
    public <E extends Talent> E getTalent(Class<E> cast) throws IllegalArgumentException {
        try {
            return cast.cast(talent);
        } catch (Exception e) {
            throw new IllegalArgumentException("talent is not of type " + cast.getSimpleName());
        }
    }

    /**
     * Gets the enum from a talent handle.
     *
     * @param talent - Talent handle.
     * @return the enum if present, or null.
     */
    @Nullable
    public static Talents fromTalent(@Nullable Talent talent) {
        if (talent == null) {
            return null;
        }

        return HANDLE_TO_ENUM.get(talent);
    }

    @Nonnull
    public static List<Talents> byType(@Nonnull Talent.Type type) {
        return BY_TYPE.getOrDefault(type, Lists.newArrayList());
    }

    private static String format(String textBlock, @Nullable Object... format) {
        if (format == null || format.length == 0) {
            return textBlock;
        }

        return BFormat.format(textBlock, format);
    }


}
