package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.archive.*;
import me.hapyl.fight.game.cosmetic.archive.gadget.FireworkGadget;
import me.hapyl.fight.game.cosmetic.archive.gadget.dice.DiceGadget;
import me.hapyl.fight.game.cosmetic.archive.gadget.dice.HighClassDice;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum Cosmetics implements RareItem, BelongsToCollection {

    BLOOD(new Cosmetic("Blood", "A classic redstone particles mimicking blood.", Type.KILL, Rarity.COMMON, Material.REDSTONE) {
        @Override
        public void onDisplay(Display display) {
            display.particle(Particle.BLOCK_CRACK, 20, 0.4d, 0.4d, 0.4d, 0.0f, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
            display.sound(Sound.BLOCK_STONE_BREAK, 0.0f);
        }
    }),

    COOKIE_MADNESS(new Cosmetic("Cookie Madness", "More cookies! Moooore!", Type.KILL, Rarity.UNCOMMON, Material.COOKIE) {
        @Override
        public void onDisplay(Display display) {
            display.repeat(10, 2, (r, tick) -> {
                display.item(Material.COOKIE, 60);
                display.sound(Sound.ENTITY_ITEM_PICKUP, 0.0f + (tick * 0.1f));
            });
        }
    }),

    SQUID_LAUNCH(new SquidLaunchCosmetic()),
    GROUND_PUNCH(new GroundPunchCosmetic()),
    GIANT_SWORD(new GiantSwordCosmetic()),
    LIGHTNING(new Cosmetic("Light Strike", "Strikes a lightning effect.", Type.KILL, Rarity.COMMON, Material.LIGHTNING_ROD) {
        @Override
        public void onDisplay(Display display) {
            final World world = display.getLocation().getWorld();
            if (world == null) {
                return;
            }
            world.strikeLightningEffect(display.getLocation());
        }
    }),

    COUTURE_KILL(new CoutureCosmetic(Type.KILL)),
    MUSIC_KILL(new MusicCosmetic()),

    // Death Cosmetics
    SCARY_DOOKIE(new Cosmetic("Scary Dookie", "The ultimate scare.", Type.DEATH, Rarity.RARE, Material.COCOA_BEANS) {
        @Override
        public void onDisplay(Display display) {
            final Item item = display.item(Material.COCOA_BEANS, 6000);
            final Player player = display.getPlayer();
            final String name = player == null ? "Someones" : player.getName();

            item.setCustomName(Chat.format("&c&l%s's Dookie", name));
            item.setCustomNameVisible(true);
            item.setVelocity(new Vector(0.0d, 0.2d, 0.0d));

            display.sound(Sound.ENTITY_PLAYER_BURP, 1.25f);
            display.sound(Sound.ENTITY_HORSE_SADDLE, 1.75f);
        }
    }),

    FINAL_MESSAGE(new FinalMessageCosmetic()),
    BIG_BLAST(new BigBlastCosmetic()),
    COUTURE_DEATH(new CoutureCosmetic(Type.DEATH)),
    STYLISH_FALL(new StylishFallCosmetic()),
    ELECTROCUTE(new ElectrocuteCosmetic()),
    EMERALD_EXPLOSION(new EmeraldExplosion()),

    // Contrails
    MUSIC(new MusicContrail()),
    RAINBOW(new RainbowContrail()),
    BED_ROCKING(new BedRockingContrail()),
    FLOWER_PATH(new FlowerPathContrail()),
    SHADOW_TRAIL(new ShadowTrail()),

    // Prefixes
    FIGHTER(new PrefixCosmetic(
            "Fighter",
            "Show everyone who you really are.",
            "&a⚔",
            Rarity.COMMON
    ).setIcon(Material.WOODEN_SWORD)),

    OCTAVE(new PrefixCosmetic("Octave", "♪ ♪♫ ♪ ♫♫", "&d♫", Rarity.RARE).setIcon(Material.NOTE_BLOCK)),
    STAR(new PrefixCosmetic("Star", "I'm on a roll!", "&e★", Rarity.EPIC).setIcon(Material.GOLD_NUGGET)),
    BIOHAZARD(new PrefixCosmetic("Biohazard", "Put your mask on!", "&a☣", Rarity.EPIC).setIcon(Material.SLIME_BALL)),
    LOVE(new PrefixCosmetic("Love", "Love is...", "&c♥", Rarity.RARE).setIcon(Material.APPLE)),
    PEACE(new PrefixCosmetic("Peace", "Peace!", "&2&l✌", Rarity.LEGENDARY).setIcon(Material.WHITE_WOOL).setExclusive(true)),
    HAPPY(new PrefixCosmetic("Happy", "Just be happy!", "&a☻", Rarity.COMMON).setIcon(Material.EMERALD)),

    GENDER_MALE(new PrefixCosmetic(
            "Gender: Male",
            "Express your gender!",
            "&b♂",
            Rarity.RARE
    ).setIcon(Material.SOUL_LANTERN)),

    GENDER_FEMALE(new PrefixCosmetic(
            "Gender: Female",
            "Express your gender!",
            "&d♀",
            Rarity.RARE
    ).setIcon(Material.LANTERN)),

    ANNIHILATOR(new PrefixCosmetic(
            "Annihilator",
            "Show me what you got!",
            "&4&kx &4☠ &4&kx",
            Rarity.LEGENDARY
    ).setIcon(Material.WITHER_SKELETON_SKULL)),

    SUNNY(new PrefixCosmetic(
            "Sunny",
            "It's nice weather outside :)",
            "&e☀",
            Rarity.EPIC
    ).setIcon(Material.GOLD_BLOCK)),

    RAINY(new PrefixCosmetic(
            "Rainy",
            "I've got my umbrella!",
            "&b🌧",
            Rarity.EPIC
    ).setIcon(Material.WATER_BUCKET)),

    GLITCH(new PrefixCosmetic(
            "Glitch",
            "Is this thing on?",
            "&a&l&k💻",
            Rarity.RARE
    ).setIcon(Material.REDSTONE_TORCH)),

    CUPCAKE(new PrefixCosmetic(
            "Cupcake",
            "Tasty!~",
            "&d🧁",
            Rarity.EPIC
    ).setIcon(Material.PUMPKIN_PIE)),

    ACCESS_DENIED(new PrefixCosmetic(
            "Access Denied",
            "There is no way out!",
            "&4\uD83D\uDEAB",
            Rarity.EPIC
    ).setIcon(Material.BARRIER)),

    KISS_KISS(new PrefixCosmetic(
            "Kiss, Kiss",
            "x",
            "&c\uD83D\uDC8B",
            Rarity.RARE
    ).setIcon(Material.GOLDEN_APPLE)),

    MONKEY(new PrefixCosmetic(
            "Monkey!",
            "I'm monkey!",
            new Color("#674e38") + "🐵",
            Rarity.RARE
    ).setIcon(Material.BROWN_WOOL)),

    SMILEY(new PrefixCosmetic(
            "Smiley",
            "Smiling through the day.",
            "&f&lツ",
            Rarity.RARE
    ).setIcon(Material.PUFFERFISH)),

    TRIANGLE(new PrefixCosmetic(
            "Triangle",
            "Is it aligned?",
            "&3📐",
            Rarity.RARE
    ).setIcon(Material.WARPED_STAIRS)),

    DRAGON(new PrefixCosmetic(
            "Dragon",
            "How do I train it?",
            Color.MAROON + "🐉",
            Rarity.EPIC
    ).setIcon(Material.DRAGON_HEAD)),

    DICE_STATUS(
            new PrefixCosmetic(
                    "High Class",
                    "Straight from Kickback City!",
                    Color.BLACK + "🎲",
                    Rarity.LEGENDARY
            ).setIcon(Material.MUSIC_DISC_STAL).setExclusive(true)
    ),

    // Win Effects

    /**
     * Should not explicitly be used.
     */
    @Deprecated
    FIREWORKS(new FireworksWinEffect(), true),

    AVALANCHE(new AvalancheWinEffect()),

    TWERK(new TwerkWinEffect()),

    // Gadgets
    FIREWORK(new FireworkGadget()),
    DICE(new DiceGadget()),
    DICE_HIGH_CLASS(new HighClassDice()),

    ;

    private final static Map<Type, List<Cosmetics>> byType = Maps.newHashMap();
    private final static Map<Class<?>, List<Cosmetics>> BY_CLASS = Maps.newHashMap();

    static {
        for (Cosmetics value : values()) {
            if (value.ignore || value.cosmetic instanceof DisabledCosmetic) {
                continue;
            }

            byType.computeIfAbsent(value.getCosmetic().getType(), k -> Lists.newArrayList()).add(value);
        }
    }

    private final boolean ignore;
    private final Cosmetic cosmetic;
    @Nullable
    private CosmeticCollection collection;

    Cosmetics(Cosmetic cosmetic) {
        this(cosmetic, false);
    }

    Cosmetics(Cosmetic cosmetic, boolean force) {
        this.cosmetic = cosmetic;
        this.cosmetic.setHandle(this);
        this.ignore = force;
    }

    public Cosmetic getCosmetic() {
        return cosmetic;
    }

    @Nonnull
    public Type getType() {
        return cosmetic.getType();
    }

    public boolean isUnlocked(Player player) {
        return PlayerDatabase.getDatabase(player).getCosmetics().hasCosmetic(this);
    }

    public void setUnlocked(Player player, boolean flag) {
        final CosmeticEntry cosmeticEntry = PlayerDatabase.getDatabase(player).getCosmetics();

        if (flag) {
            cosmeticEntry.addOwned(this);
        }
        else {
            cosmeticEntry.unsetSelected(getType());
            cosmeticEntry.removeOwned(this);
        }
    }

    public boolean isSelected(Player player) {
        return PlayerDatabase.getDatabase(player).getCosmetics().getSelected(getType()) == this;
    }

    public void select(Player player) {
        PlayerDatabase.getDatabase(player).getCosmetics().setSelected(getType(), this);
    }

    public void deselect(Player player) {
        PlayerDatabase.getDatabase(player).getCosmetics().unsetSelected(getType());
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return cosmetic.getRarity();
    }

    @Nonnull
    @Override
    public String getId() {
        return name();
    }

    @Nonnull
    public <T> T getCosmetic(Class<T> clazz) {
        if (clazz.isInstance(cosmetic)) {
            return clazz.cast(cosmetic);
        }

        throw new IllegalArgumentException("%s cannot be cast to %s".formatted(this, clazz.getSimpleName()));
    }

    public boolean isIgnore() {
        return ignore;
    }

    @Nullable
    @Override
    public CosmeticCollection getCollection() {
        return collection;
    }

    @Override
    public void setCollection(@Nullable CosmeticCollection collection) {
        this.collection = collection;
    }

    // static members
    public static List<Cosmetics> getByType(Type type) {
        return new ArrayList<>(byType.getOrDefault(type, Lists.newArrayList()));
    }

    @Nullable
    public static Cosmetics getSelected(Player player, Type type) {
        return PlayerDatabase.getDatabase(player).getCosmetics().getSelected(type);
    }
}
