package me.hapyl.fight.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import me.hapyl.fight.CF;
import me.hapyl.fight.GVar;
import me.hapyl.fight.Main;
import me.hapyl.fight.build.NamedSignReader;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.fx.GiantItem;
import me.hapyl.fight.fx.beam.Quadrant;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.cosmetic.CosmeticCollection;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.game.cosmetic.crate.convert.CrateConvert;
import me.hapyl.fight.game.cosmetic.crate.convert.CrateConverts;
import me.hapyl.fight.game.entity.*;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.cooldown.CooldownData;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.BatCloud;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bloodfield.BloodfiendData;
import me.hapyl.fight.game.heroes.archive.dark_mage.AnimatedWither;
import me.hapyl.fight.game.heroes.archive.doctor.ElementType;
import me.hapyl.fight.game.heroes.archive.engineer.Engineer;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.lobby.StartCountdown;
import me.hapyl.fight.game.maps.gamepack.GamePack;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.engineer.Construct;
import me.hapyl.fight.game.talents.archive.juju.Orbiting;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.display.DamageDisplay;
import me.hapyl.fight.game.ui.splash.SplashText;
import me.hapyl.fight.gui.HeroPreviewGUI;
import me.hapyl.fight.gui.LegacyAchievementGUI;
import me.hapyl.fight.gui.styled.profile.DeliveryGUI;
import me.hapyl.fight.gui.styled.profile.achievement.AchievementGUI;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.ChatUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Compute;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.LazyEvent;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.command.*;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.hologram.Hologram;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.GUI;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Cuboid;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.player.EffectType;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.DataWatcherType;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.reflect.npc.Human;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import me.hapyl.spigotutils.module.util.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class CommandRegistry extends DependencyInjector<Main> implements Listener {

    private final CommandProcessor processor;

    public CommandRegistry(Main main) {
        super(main);

        Bukkit.getPluginManager().registerEvents(this, main);
        this.processor = new CommandProcessor(main);

        // Unregister annoying paper commands
        unregisterAnnoyingPaperCommandsThatICannotOverride();

        register(new HeroCommand("hero"));
        register(new GameCommand("cf"));
        register(new ReportCommandCommand("report"));
        register(new ParticleCommand("part"));
        register(new GameEffectCommand("gameEffect"));
        register(new MapCommand("map"));
        register(new ModeCommand("mode"));
        register(new AdminCommand("admin"));
        register(new DebugBooster("debugBooster"));
        register(new SettingCommand("setting"));
        register(new TutorialCommand("tutorial"));
        register(new TeamCommand("team"));
        register(new TestWinConditionCommand("testWinCondition"));
        register(new TriggerWinCommand("triggerWin"));
        register(new DummyCommand("dummy"));
        register(new CooldownCommand("cooldown"));
        register(new ExperienceCommand("experience"));
        register(new AchievementCommand("achievement"));
        register(new CosmeticCommand("cosmetic"));
        register(new SyncDatabaseCommand("syncDatabase"));
        register(new CastSpellCommand("cast"));
        register(new UpdateParkourLeaderboardCommand("updateParkourLeaderboard"));
        register(new ModifyParkourCommand("modifyParkour"));
        register(new InterruptCommand("interrupt"));
        register(new TestDatabaseCommand("testdatabase"));
        register(new EquipCommand("equip"));
        register(new HeadCommand("head"));
        register(new RankCommand("rank"));
        register(new DebugPlayerCommand("debugPlayer"));
        register(new DebugAchievementCommand("debugAchievement"));
        register(new ProfileCommand("profile"));
        register(new GVarCommand("gvar"));
        register(new PlayerAttributeCommand("playerAttribute"));
        register(new SnakeBuilderCommand("snakeBuilder"));
        register(new CrateCommandCommand("crate"));
        register(new GlobalConfigCommand("globalConfig"));
        register(new SimplePlayerCommand("delivery") {
            @Override
            protected void execute(Player player, String[] args) {
                new DeliveryGUI(player);
            }
        });

        register(new SimplePlayerAdminCommand("ph") {
            @Override
            protected void execute(Player player, String[] args) {
                final PlayerProfile profile = PlayerProfile.getProfile(player);
                final Heroes hero = profile.getHero();
                final Heroes enumHero = getArgument(args, 0).toEnum(Heroes.class, hero);

                if (enumHero == null) {
                    Chat.sendMessage(player, "&cInvalid hero!");
                    return;
                }

                new HeroPreviewGUI(player, enumHero, 0);
            }
        });

        register(new SimplePlayerAdminCommand("hex") {
            @Override
            protected void execute(Player player, String[] args) {
                final String color = getArgument(args, 0).toString();
                final String string = Chat.arrayToString(args, 1);

                final me.hapyl.fight.game.color.Color theColor = new me.hapyl.fight.game.color.Color(color);

                Chat.sendMessage(player, "&aOutput: ");
                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.suggestCommand(color),
                        LazyEvent.showText("&eClick to copy!"),
                        theColor + string
                );
            }
        });

        register(new SimplePlayerAdminCommand("countTalentTypes") {
            @Override
            protected void execute(Player player, String[] args) {
                final Map<Talent.Type, Integer> typeCount = Maps.newHashMap();

                for (Talent.Type type : Talent.Type.values()) {
                    typeCount.compute(type, Compute.intAdd());
                }

                Chat.sendMessage(player, "&aHere's a total of talent counts:");
                typeCount.forEach((type, count) -> Chat.sendMessage(player, " &2%s: %s", type.getName(), count));
            }
        });

        register(new SimplePlayerAdminCommand("testShield") {
            @Override
            protected void execute(Player player, String[] args) {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cMust be in game.");
                    return;
                }

                final double capacity = getArgument(args, 0).toDouble(20);

                gamePlayer.setShield(new Shield(gamePlayer, capacity));
                Chat.sendMessage(player, "&aApplied shield with %s capacity.", capacity);
            }
        });

        register(new SimplePlayerCommand("canConvertCrate") {
            @Override
            protected void execute(Player player, String[] args) {
                final CrateConverts enumConvert = getArgument(args, 0).toEnum(CrateConverts.class);

                if (enumConvert == null) {
                    Chat.sendMessage(player, "&cInvalid convert!");
                    return;
                }

                final CrateConvert convert = enumConvert.get();
                final int canConvertTimes = convert.canConvertTimes(player);

                if (canConvertTimes > 0) {
                    Chat.sendMessage(player, "&aYou can convert %s times!", canConvertTimes);
                }
                else {
                    Chat.sendMessage(player, "&cYou don't have enough items to convert!");
                }

                if (player.isOp()) {
                    Chat.sendMessage(player, "&7Convert Data:");
                    Chat.sendMessage(player, convert.toString());
                }
            }
        });

        register(new SimpleAdminCommand("lastHapylGUI") {
            @Override
            protected void execute(CommandSender sender, String[] args) {
                final Player hapyl = Bukkit.getPlayer("hapyl");

                if (hapyl == null) {
                    Chat.sendMessage(sender, "&chapyl is not online!");
                    return;
                }

                final GUI lastGUI = GUI.getPlayerLastGUI(hapyl);
                Debug.info("lastGUI=" + (lastGUI == null ? "NONE!" : lastGUI.getName()));
            }
        });

        register(new SimplePlayerAdminCommand("testBlocksCirclingAround") {
            GameTask task;
            List<ArmorStand> blocks = Lists.newArrayList();

            @Override
            protected void execute(Player player, String[] args) {
                if (task != null) {
                    task.cancel();
                    task = null;

                    blocks.forEach(ArmorStand::remove);
                    blocks.clear();

                    Chat.sendMessage(player, "&cCancelled!");
                    return;
                }

                final Location location = player.getLocation();

                final int blockCount = getArgument(args, 0).toInt(10);
                final double distance = getArgument(args, 1).toDouble(5);

                for (int i = 0; i < blockCount; i++) {
                    boolean glow = i % 2 == 0;
                    blocks.add(Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
                        self.setSilent(true);
                        self.setInvisible(true);
                        self.setGravity(false);
                        self.setHelmet(new ItemStack(Material.STONE));
                        if (glow) {
                            self.setGlowing(true);
                        }
                    }));
                }

                final double spread = Math.PI * 2 / Math.max(blockCount, 1);

                task = new TickingGameTask() {
                    private double theta = 0.0d;

                    @Override
                    public void run(int tick) {
                        int index = 1;

                        for (ArmorStand block : blocks) {
                            final double x = Math.sin(theta + spread * index) * distance;
                            final double y = Math.sin(theta + spread * index) * 0.5d;
                            final double z = Math.cos(theta + spread * index) * distance;

                            location.add(x, y, z);
                            block.teleport(location);
                            location.subtract(x, y, z);
                            index++;
                        }

                        if (theta >= Math.PI * 2) {
                            theta = 0.0d;
                        }

                        theta += Math.PI / 16;
                    }
                }.runTaskTimer(0, 2);

                Chat.sendMessage(player, "&aStarted!");
            }
        });

        register("clearCachedTalentItems", (player, args) -> {
            for (Talents enumTalent : Talents.values()) {
                final Talent talent = enumTalent.getTalent();

                if (talent != null) {
                    talent.nullifyItem();
                }
            }

            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("testTalentLock") {
            @Override
            protected void execute(Player player, String[] args) {
                final int i = getArgument(args, 0).toInt();
                final int lock = getArgument(args, 1).toInt();
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cCannot lock talent outside a game!");
                    return;
                }

                final TalentLock talentLock = gamePlayer.getTalentLock();
                final HotbarSlots slot = gamePlayer.getProfile().getHotbarLoadout().bySlot(i);

                if (slot == null) {
                    Chat.sendMessage(player, "&cInvalid talent!");
                    return;
                }

                final boolean isLock = talentLock.setLock(slot, lock);
                if (!isLock) {
                    Chat.sendMessage(player, "&cCannot lock '%s'!", slot.getName());
                    return;
                }

                Chat.sendMessage(player, "&aLocked '%s' for %s!", slot.getName(), CFUtils.decimalFormatTick(lock));
            }
        });

        register(new SimplePlayerAdminCommand("testGiantItem") {

            private GiantItem item;

            @Override
            protected void execute(Player player, String[] args) {
                if (item == null) {
                    item = new GiantItem(
                            player.getLocation(),
                            ItemBuilder.playerHeadUrl("d81fcffb53acbc7c00c53bc7121ca259371b5b76c001dc52139e1804c287e54").asIcon()
                    );
                    Chat.sendMessage(player, "&aSpawned!");
                    return;
                }

                final TypeConverter argument = getArgument(args, 0);
                final float degree = argument.toFloat(-1);

                if (degree != -1) {
                    item.rotate(degree);
                    Chat.sendMessage(player, "&aRotated %s degrees.", degree);
                    return;
                }

                final String string = argument.toString();

                if (string.equalsIgnoreCase("remove")) {
                    item.remove();
                    item = null;
                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                if (string.equalsIgnoreCase("dance")) {
                    new TickingGameTask() {

                        float d;

                        @Override
                        public void run(int tick) {
                            if (d >= 360) {
                                cancel();
                                Chat.sendMessage(player, "&aDone!");
                                return;
                            }

                            item.rotate(d);

                            d += (float) 360 / GVar.get("deg", 30);
                            Debug.info("degree = " + d);
                        }
                    }.runTaskTimer(2, 1);
                }

            }
        });

        register("viewLegacyAchievementGUI", (player, args) -> {
            new LegacyAchievementGUI(player);
        });

        register(new CustomItemCommand("customItem"));

        register("getLobbyItems", (player, args) -> {
            LobbyItems.giveAll(player);
            Chat.sendMessage(player, "&aThere you go!");
        });

        register("startAndCancelCountdown", (player, args) -> {
            final Manager manager = Manager.current();
            manager.createStartCountdown(DebugData.FORCE);
            manager.stopStartCountdown(player);

            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("entity") {
            @Override
            protected void execute(Player player, String[] args) {
                final GameEntities entity = getArgument(args, 0).toEnum(GameEntities.class);

                if (entity == null) {
                    Chat.sendMessage(player, "&cInvalid entity!");
                    return;
                }

                entity.spawn(player.getLocation());
                Chat.sendMessage(player, "&aSpawned %s!", entity.type);
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return completerSort(GameEntities.values(), args);
            }
        });

        register("testKnockback360", (player, args) -> {
            final TickingGameTask task = new TickingGameTask() {
                @Override
                public void run(int tick) {
                    if (tick >= 360) {
                        Chat.sendMessage(player, "&aFinished!");
                        cancel();
                        return;
                    }

                    player.sendHurtAnimation(tick);
                }
            };
            task.setIncrement(15);
            task.runTaskTimer(0, 1);
        });

        register(new SimplePlayerAdminCommand("testGuardianBeams") {

            private Quadrant quadrant;

            @Override
            protected void execute(Player player, String[] args) {
                if (quadrant != null) {
                    quadrant.remove();
                    quadrant = null;

                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                final Location location = player.getLocation();
                quadrant = new Quadrant(location) {
                    @Override
                    public void onTouch(@Nonnull LivingGameEntity entity) {
                        entity.damage(10);
                    }
                };

                quadrant.setHeight(3);
                quadrant.runTaskTimer(0, 1);
                Chat.sendMessage(player, "&aStarted!");
            }
        });

        register(new SimplePlayerAdminCommand("ef") {
            @Override
            protected void execute(Player player, String[] args) {
                final EffectType effectType = getArgument(args, 0).toEnum(EffectType.class);
                final int effectDuration = getArgument(args, 1).toInt(20);

                if (effectType == null) {
                    Chat.sendMessage(player, "&cUnknown effect!");
                    return;
                }

                if (effectDuration < 0) {
                    Chat.sendMessage(player, "&cDuration cannot be negative!");
                    return;
                }

                player.addPotionEffect(effectType.getType().createEffect(effectDuration, 1));
                Chat.sendMessage(player, "&aApplied %s for %s!", effectType, effectDuration);
            }
        });

        register("damageSelf", (player, args) -> {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNo handle!");
                return;
            }

            final double damage = args.length > 0 ? Numbers.getDouble(args[0], 1.0d) : 1.0d;

            gamePlayer.setLastDamager(gamePlayer);
            gamePlayer.damage(damage, EnumDamageCause.ENTITY_ATTACK);
            Chat.sendMessage(player, "&aDamaged!");
        });

        register(new SimplePlayerAdminCommand("testBatCloud") {

            private BatCloud batCloud;

            @Override
            protected void execute(Player player, String[] args) {
                if (batCloud != null) {
                    batCloud.remove();
                    batCloud = null;

                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                batCloud = new BatCloud(player);
                Chat.sendMessage(player, "&aSpawned!");
            }
        });

        register("deleteGamePlayer", (player, args) -> {
            final PlayerProfile profile = PlayerProfile.getProfile(player);
            if (profile == null) {
                return;
            }

            profile.resetGamePlayer();
            Chat.sendMessage(player, "&aDone!");
        });

        register("dumpColor", (player, args) -> {
            final ItemStack item = player.getInventory().getItemInMainHand();
            final ItemMeta meta = item.getItemMeta();

            if (meta instanceof LeatherArmorMeta colorMeta) {
                final org.bukkit.Color color = colorMeta.getColor();
                final int red = color.getRed();
                final int green = color.getGreen();
                final int blue = color.getBlue();

                final me.hapyl.fight.game.color.Color stringColor = new me.hapyl.fight.game.color.Color(color);

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.suggestCommand(red + ", " + green + ", " + blue),
                        LazyEvent.showText("&eClick to copy color!"),
                        "&aColor: " + stringColor + "∎∎∎ &6&lCLICK TO COPY RGB"
                );
            }

            if (meta instanceof ArmorMeta armorMeta) {
                final ArmorTrim trim = armorMeta.getTrim();

                if (trim != null) {
                    final TrimPattern pattern = trim.getPattern();
                    final TrimMaterial material = trim.getMaterial();

                    final String patternKey = pattern.getKey().getKey().toUpperCase();
                    final String materialKey = material.getKey().getKey().toUpperCase();

                    Chat.sendClickableHoverableMessage(
                            player,
                            LazyEvent.suggestCommand("TrimPattern." + patternKey + ", TrimMaterial." + materialKey),
                            LazyEvent.showText("&eClick to copy armor trim!"),
                            "&aTrim: " + patternKey + " x " + materialKey + " &6&lCLICK TO COPY TRIM"
                    );
                }
            }

        });

        register(new SwiftTeleportCommand());

        register(new SimplePlayerCommand("cancelCountdown") {
            @Override
            protected void execute(Player player, String[] args) {
                final Manager manager = Manager.current();
                final StartCountdown countdown = manager.getStartCountdown();

                if (countdown == null) {
                    Chat.sendMessage(player, "&cNothing to cancel!");
                    return;
                }

                countdown.cancelByPlayer(player);
            }
        });

        register("debugCollection", (player, args) -> {
            final CosmeticCollection collection = Enums.byName(CosmeticCollection.class, args[0]);

            if (collection == null) {
                Message.error(player, "Invalid collection.");
                return;
            }

            Message.info(player, collection.getItems().toString());
        });

        register(new SimplePlayerAdminCommand("skin") {

            private final String textureUrl = "https://textures.minecraft.net/texture/";

            @Override
            protected void execute(Player player, String[] args) {
                final String arg = getArgument(args, 0).toString();

                if (arg.equalsIgnoreCase("reset")) {
                    PlayerSkin.reset(player);
                    Message.success(player, "Reset your skin!");
                    return;
                }

                final String url = arg.contains(textureUrl) ? arg : textureUrl + arg;
                final byte[] encoded = Base64.getEncoder().encode(url.getBytes(StandardCharsets.UTF_8));
                final String texture = new String(encoded);

                new PlayerSkin(texture, texture).apply(player);
                Message.success(player, "Set your skin!");
            }
        });

        register("debugCrate", (player, args) -> {
            final Crates crate = Enums.byName(Crates.class, args.length == 0 ? "null" : args[0]);

            if (crate == null) {
                Message.error(player, "Cannot find crate named {}.", args[0]);
                return;
            }

            Message.info(player, crate.getCrate().toString());
        });

        register(new SimplePlayerAdminCommand("testOrbiting") {
            private Orbiting orbiting;

            @Override
            protected void execute(Player player, String[] args) {
                if (orbiting == null) {
                    orbiting = new Orbiting(3, Material.ARROW) {
                        @Override
                        public @Nonnull Location getAnchorLocation() {
                            return player.getLocation();
                        }
                    };

                    orbiting.setMatrix(
                            0.5000f,
                            0.5000f,
                            0.7071f,
                            0.0000f,
                            -0.7071f,
                            0.7071f,
                            -0.0000f,
                            0.0000f,
                            -0.5000f,
                            -0.5000f,
                            0.7071f,
                            0.0000f,
                            0.0000f,
                            0.0000f,
                            0.0000f,
                            1.0000f
                    );
                    orbiting.addMissing(player.getLocation());
                    orbiting.runTaskTimer(0, 1);

                    Chat.sendMessage(player, "&aSpawned!");
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "delete" -> {
                        orbiting.removeAll();
                        orbiting = null;

                        Chat.sendMessage(player, "&cDeleted!");
                    }
                    case "remove" -> {
                        orbiting.remove();

                        Chat.sendMessage(player, "&aRemoved!");
                    }
                    case "add" -> {
                        orbiting.add(player.getLocation());

                        Chat.sendMessage(player, "&aAdded!");
                    }
                    case "tp" -> {
                        orbiting.forEach(stand -> {
                            stand.teleport(stand.getLocation().add(0, 1, 0));
                        });

                        Chat.sendMessage(player, "&aTeleported!");
                    }
                }
            }
        });

        register("dumpEntities", (player, args) -> {
            final Set<GameEntity> entities = CF.getEntities();

            Chat.sendMessage(player, "&aEntity Dump (%s)", entities.size());

            boolean lighterColor = true;
            for (GameEntity entity : entities) {
                final String className = entity.getClass().getSimpleName();
                final String name = entity.getName();

                Chat.sendMessage(player, (lighterColor ? "&b " : "&3 ") + className + ":" + name);
                lighterColor = !lighterColor;

                // Glow duh
                if (entity instanceof LivingGameEntity livingGameEntity) {
                    livingGameEntity.addPotionEffect(PotionEffectType.GLOWING, 60, 1);
                }
            }
        });

        register("setHeroSkin", (player, args) -> {
            if (args.length == 0) {
                PlayerSkin.reset(player);
                Chat.sendMessage(player, "&eReset!");
                return;
            }

            final Heroes hero = Enums.byName(Heroes.class, args[0]);

            if (hero == null) {
                Chat.sendMessage(player, "&cInvalid skin!");
                return;
            }

            final PlayerSkin skin = hero.getHero().getSkin();

            if (skin == null) {
                Chat.sendMessage(player, "&cThis hero doesn't have a skin!");
                return;
            }

            skin.apply(player);
            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("testLine") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Location start = player.getLocation();
                final Location end = player.getTargetBlockExact(100).getLocation().add(0.5d, 0.5d, 0.5d);

                final double step = 0.25d;
                final double distance = start.distance(end);
                final Vector vector = end.clone().subtract(start).toVector().normalize().multiply(step);

                final Location location = start.clone();

                for (double d = 0.0d; d < distance; d += step) {
                    final double traveled = d / distance;
                    final double y = Math.sin(traveled * Math.PI * 3);

                    location.add(vector);
                    location.add(0, y, 0);

                    PlayerLib.spawnParticle(location, Particle.VILLAGER_HAPPY, 1);
                    location.subtract(0, y, 0);
                }

                Chat.sendMessage(player, "&aDrawn!");
            }
        });

        register("testAddSucculence", (player, args) -> {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNo handle.");
                return;
            }

            final Bloodfiend bloodfiend = Heroes.BLOODFIEND.getHero(Bloodfiend.class);
            final BloodfiendData data = bloodfiend.getData(gamePlayer);

            data.addSucculence(gamePlayer);
            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("debugCooldown") {
            @Override
            protected void execute(Player player, String[] args) {
                final Cooldown cooldown = getArgument(args, 0).toEnum(Cooldown.class);
                final long duration = getArgument(args, 1).toLong();

                if (cooldown == null) {
                    Message.Error.INVALID_ENUMERABLE_ARGUMENT.send(player, Arrays.toString(Cooldown.values()));
                    return;
                }

                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Message.error(player, "Cannot use outside a game.");
                    return;
                }

                if (duration > 0) {
                    gamePlayer.startCooldown(cooldown, duration);
                    Message.success(player, "Started cooldown!");
                    return;
                }

                final CooldownData data = gamePlayer.getCooldown().getData(cooldown);

                if (data == null) {
                    Message.error(player, "&cYou don't have this cooldown!");
                    return;
                }

                player.sendMessage(data.toString());
            }
        });

        register(new SimplePlayerAdminCommand("velocity") {
            @Override
            protected void execute(Player player, String[] args) {
                final double x = getArgument(args, 0).toDouble();
                final double y = getArgument(args, 1).toDouble();
                final double z = getArgument(args, 2).toDouble();

                if (x == 0 && y == 0 && z == 0) {
                    Message.error(player, "At least one vector must be positive!");
                    return;
                }

                player.setVelocity(new Vector(x, y, z));
                player.sendMessage(ChatColor.GREEN + "Whoosh!");
            }
        });

        register("testTransformationRotation", (player, args) -> {
            if (args.length != 8) {
                Chat.sendMessage(
                        player,
                        "&cInvalid usage! /testTransformationRotation (double0) (double1) (double2) (double3) (double4) (double5) (double6) (double7)"
                );
                return;
            }

            final double a = Validate.getDouble(args[0]);
            final double b = Validate.getDouble(args[1]);
            final double c = Validate.getDouble(args[2]);
            final double d = Validate.getDouble(args[3]);

            final double e = Validate.getDouble(args[4]);
            final double f = Validate.getDouble(args[5]);
            final double g = Validate.getDouble(args[6]);
            final double h = Validate.getDouble(args[7]);

            Entities.ITEM_DISPLAY.spawn(player.getLocation(), self -> {
                self.setItemStack(ItemBuilder.playerHeadUrl("2c8c8f382667bf59f164106849c00e6dfd9ad00a72670b9de99589e4dcd00900").asIcon());
                self.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
                self.setTransformation(new Transformation
                        (
                                new Vector3f(0, 0, 0),
                                new Quaternionf(a, b, c, d),
                                new Vector3f(1, 1, 1),
                                new Quaternionf(e, f, g, h)
                        ));

                self.setCustomName("(%s, %s, %s, %s) (%s, %s, %s, %s)".formatted(a, b, c, d, e, f, g, h));
                self.setCustomNameVisible(true);
            });

            Chat.sendMessage(player, "&aSpawned!");
        });

        register("testHoverText", (player, args) -> {
            final TextComponent text = new TextComponent("Hover me");

            text.setHoverEvent(ChatUtils.showText("first line", "second line", "&ccolor"));
            player.spigot().sendMessage(text);
        });

        register("spawnEntityWithMaxDodgeToTestTheDodgeAttributeBecauseIHaveNoFriendsToTestItWith", (player, args) -> {
            final Pig pig = Entities.PIG.spawn(player.getLocation());
            final LivingGameEntity entity = CF.getEntity(pig);

            if (entity == null) {
                Chat.sendMessage(player, "&1Entity handle is null!");
                return;
            }

            entity.getAttributes().set(AttributeType.DODGE, AttributeType.DODGE.maxValue());

            Chat.sendMessage(player, "&1There you go!");
        });

        register(new SimplePlayerCommand("viewAchievementGUI") {
            @Override
            protected void execute(Player player, String[] args) {
                new AchievementGUI(player);
            }
        });

        register(new SimplePlayerAdminCommand("simulateDeathMessage") {
            @Override
            protected void execute(Player player, String[] strings) {
                final EnumDamageCause cause = getArgument(strings, 0).toEnum(EnumDamageCause.class);
                final double distance = getArgument(strings, 1).toDouble();
                final GamePlayer gamePlayer = CF.getPlayer(player);

                final String format = cause.getRandomIfMultiple().format(gamePlayer, gamePlayer, distance);
                Chat.sendMessage(player, ChatColor.RED + format);
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return completerSort(EnumDamageCause.values(), args);
            }
        });

        register("recipes", (player, args) -> {
            if (args.length == 0) {
                Chat.sendMessage(player, "&cMissing argument, either 'reset' or 'clear'.");
                return;
            }

            final String arg = args[0];

            if (arg.equalsIgnoreCase("reset")) {
                Bukkit.resetRecipes();
                Chat.broadcast("&aReset recipes!");
            }
            else if (arg.equalsIgnoreCase("clear")) {
                Bukkit.clearRecipes();
                Chat.broadcast("&aCleared recipes!");
            }
            else {
                Chat.sendMessage(player, "&cInvalid usage!");
            }
        });

        register("dumpBlockNamesCSV", (player, args) -> {
            Runnables.runAsync(() -> {
                final File path = new File(Main.getPlugin().getDataFolder(), "element_types.csv");

                try (FileWriter writer = new FileWriter(path)) {
                    for (Material material : Material.values()) {
                        if (material.isBlock()) {
                            writer.append(material.name().toUpperCase()).append(",").append("NULL");
                            writer.append("\n");
                        }
                    }

                    Runnables.runSync(() -> {
                        Chat.sendMessage(player, "&aDumped into &e%s&a!", path.getAbsolutePath());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        register("testEternaItemBuilderAddTrim", (player, args) -> {
            player.getInventory()
                    .addItem(ItemBuilder.of(Material.DIAMOND_CHESTPLATE).setArmorTrim(TrimPattern.EYE, TrimMaterial.DIAMOND).asIcon());
        });

        register("getUuidName", (player, args) -> {
            try {
                final UUID uuid = UUID.fromString(args[0]);

                Chat.sendMessage(player, "&a%s belongs to %s", uuid.toString(), Bukkit.getOfflinePlayer(uuid).getName());
            } catch (Exception e) {
                Chat.sendMessage(player, "&cProvide a valid uuid!");
            }
        });

        register(new SimplePlayerAdminCommand("nextTrim") {

            // bro just make this a fucking enum
            private final TrimPattern[] PATTERNS =
                    {
                            TrimPattern.SENTRY,
                            TrimPattern.DUNE,
                            TrimPattern.COAST,
                            TrimPattern.WILD,
                            TrimPattern.WARD,
                            TrimPattern.EYE,
                            TrimPattern.VEX,
                            TrimPattern.TIDE,
                            TrimPattern.SNOUT,
                            TrimPattern.RIB,
                            TrimPattern.SPIRE,
                            TrimPattern.WAYFINDER,
                            TrimPattern.SHAPER,
                            TrimPattern.SILENCE,
                            TrimPattern.RAISER,
                            TrimPattern.HOST
                    };

            @Override
            protected void execute(Player player, String[] strings) {
                final String string = getArgument(strings, 0).toString().toLowerCase();

                switch (string) {
                    case "helmet" -> nextTrim(player, EquipmentSlot.HEAD);
                    case "chestplate", "chest" -> nextTrim(player, EquipmentSlot.CHEST);
                    case "leggings", "legs" -> nextTrim(player, EquipmentSlot.LEGS);
                    case "boots" -> nextTrim(player, EquipmentSlot.FEET);
                    default -> {
                        Chat.sendMessage(player, "&cInvalid argument, accepting: [helmet, chestplate, chest, leggings, legs, boots]");
                    }
                }
            }

            private void nextTrim(Player player, EquipmentSlot slot) {
                final PlayerInventory inventory = player.getInventory();
                final ItemStack item = inventory.getItem(slot);

                if (item == null) {
                    Chat.sendMessage(player, "&cNot a valid item.");
                    return;
                }

                if (!(item.getItemMeta() instanceof ArmorMeta meta)) {
                    Chat.sendMessage(player, "&cCannot apply trim to this piece!");
                    return;
                }

                final ArmorTrim trim = meta.getTrim();
                TrimPattern pattern = trim == null ? PATTERNS[0] : trim.getPattern();

                for (int i = 0; i < PATTERNS.length; i++) {
                    if (PATTERNS[i] == pattern) {
                        pattern = i + 1 >= PATTERNS.length ? PATTERNS[0] : PATTERNS[i + 1];
                        break;
                    }
                }

                meta.setTrim(new ArmorTrim(trim == null ? TrimMaterial.QUARTZ : trim.getMaterial(), pattern));
                item.setItemMeta(meta);

                Chat.sendMessage(player, "&aSet %s pattern.", pattern.getKey().getKey());
            }
        });

        register("readElementTypesCSV", (player, args) -> {
            Runnables.runAsync(() -> {
                final File file = new File(Main.getPlugin().getDataFolder(), "element_types.csv");

                if (!file.exists()) {
                    Chat.sendMessage(player, "&cFile 'element_types.csv' doesn't exists!");
                    return;
                }

                final Map<ElementType, Set<Material>> mapped = Maps.newHashMap();

                try (var reader = new Scanner(file)) {
                    int index = 1;

                    while (reader.hasNextLine()) {
                        final String line = reader.nextLine();
                        final String[] split = line.split(",");

                        final Material material = Enums.byName(Material.class, split[0]);
                        final ElementType elementType = Enums.byName(ElementType.class, split[1]);

                        if (material == null) {
                            Chat.sendMessage(player, "&4ERROR @ %s! &cMaterial %s is invalid!", index, split[0]);
                            reader.close();
                            return;
                        }

                        if (elementType == null) {
                            Chat.sendMessage(player, "&4ERROR @ %s! &cType %s is invalid!", index, split[1]);
                            reader.close();
                            return;
                        }

                        // Don't care about null
                        if (elementType == ElementType.NULL) {
                            continue;
                        }

                        mapped.compute(elementType, (t, set) -> {
                            if (set == null) {
                                set = Sets.newHashSet();
                            }

                            set.add(material);

                            return set;
                        });

                        index++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Write to file
                final File fileOut = new File(Main.getPlugin().getDataFolder(), "element_types.mapped");

                try (var writer = new FileWriter(fileOut)) {
                    for (ElementType type : ElementType.values()) {
                        final Set<Material> materials = mapped.getOrDefault(type, Sets.newHashSet());

                        writer.append("//").append(String.valueOf(type)).append("\n");

                        int index = 0;
                        for (Material material : materials) {
                            if (index++ != 0) {
                                writer.append(",\n");
                            }
                            writer.append("     Material.").append(material.name());
                        }

                        writer.append("\n");
                    }

                    Runnables.runSync(() -> Chat.sendMessage(player, "&aDumped into &e%s&a!", fileOut.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        });

        register("debugLevelUpConstruct", (player, args) -> {
            final Construct construct = Heroes.ENGINEER.getHero(Engineer.class).getConstruct(CF.getPlayer(player));

            if (construct == null) {
                Chat.sendMessage(player, "&cNo construct!");
                return;
            }

            construct.levelUp();
            Chat.sendMessage(player, "&aLevelled up!");
        });

        register(new SimplePlayerCommand("resourcePack") {
            @Override
            protected void execute(Player player, String[] args) {
                final PlayerProfile profile = PlayerProfile.getProfile(player);

                if (profile == null) {
                    Chat.sendMessage(player, "&cCouldn't find your profile! That's a bug you should report.");
                    return;
                }

                profile.promptResourcePack();
            }
        });

        register("testAchievementToast", (player, args) -> {
        });

        register(new SimplePlayerAdminCommand("testSplashText") {

            private SplashText splash;

            @Override
            protected void execute(Player player, String[] args) {
                if (splash == null) {
                    splash = new SplashText(player);
                }
                else if (args.length == 0) {
                    splash.remove();
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                final int startLine = getArgument(args, 0).toInt();

                splash.create(startLine, Arrays.copyOfRange(args, 1, args.length));

                Chat.sendMessage(player, "&aDone!");
            }
        });

        register(new SimpleAdminCommand("colorMeThis") {
            @Override
            protected void execute(CommandSender sender, String[] args) {
                if (args.length == 0) {
                    Chat.sendMessage(sender, "&cColor you what?");
                    return;
                }

                Chat.sendMessage(sender, "&aThere you go!");
                Chat.sendMessage(sender, Chat.arrayToString(args, 0));
            }
        });

        register(new SimplePlayerAdminCommand("debugSnakeBlock") {

            private BlockDisplay display;

            @Override
            protected void execute(Player player, String[] args) {
                if (display != null) {
                    display.remove();
                }

                final float transformation = getArgument(args, 0).toFloat();

                display = Entities.BLOCK_DISPLAY.spawn(player.getLocation(), self -> {
                    self.setBlock(Material.STONE.createBlockData());
                    self.setTransformation(new Transformation(
                            new Vector3f(transformation, transformation, transformation),
                            new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f),
                            new Vector3f(0.25f, 0.25f, 0.25f),
                            new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f)
                    ));
                });

                Chat.sendMessage(player, "&aSpawned with %s.", transformation);
            }
        });

        register(new SimplePlayerAdminCommand("displayDamage") {
            @Override
            protected void execute(Player player, String[] args) {
                // displayDamage (damage) (crit)
                final double damage = getArgument(args, 0).toDouble(10.0d);
                final boolean isCrit = getArgument(args, 1).toString().equalsIgnoreCase("true");

                new DamageDisplay(damage, isCrit).display(LocationHelper.getInFront(player.getLocation(), 1.0d));
            }
        });

        register(new SimplePlayerAdminCommand("debugTextDisplayOpaque") {

            private final Set<TextDisplay> set = Sets.newHashSet();

            @Override
            protected void execute(Player player, String[] args) {
                set.forEach(TextDisplay::remove);
                set.clear();

                final Location location = player.getLocation();

                for (var ref = new Object() {
                    byte i = Byte.MIN_VALUE;
                }; ref.i < Byte.MAX_VALUE; ref.i++) {
                    Entities.TEXT_DISPLAY.spawn(location, self -> {
                        self.setBillboard(Display.Billboard.CENTER);
                        self.setSeeThrough(true);
                        self.setTextOpacity(ref.i);
                        self.setText("&a" + ref.i);
                    });

                    location.add(0.0d, 0.25d, 0.0d);
                }

                Chat.sendMessage(player, "&aDone!");
            }
        });

        register(new SimplePlayerAdminCommand("temperStat") {
            @Override
            protected void execute(Player player, String[] args) {
                // $ <stat> <amount> <duration>
                // $ temper [value] [duration]
                final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cCannot use outside a game.");
                    return;
                }

                final Temper temper = getArgument(args, 0).toEnum(Temper.class);
                final AttributeType attributeType = getArgument(args, 1).toEnum(AttributeType.class);
                final double value = getArgument(args, 2).toDouble(0.2d);
                final int duration = getArgument(args, 3).toInt(100);

                if (attributeType == null) {
                    Message.error(player, "Invalid type!");
                    return;
                }

                if (temper == null) {
                    Message.error(player, "Invalid temper!");
                    return;
                }

                temper.temper(gamePlayer, attributeType, value, duration);
                Chat.sendMessage(player, "&aDone!");
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                if (args.length == 1) {
                    return completerSort(Temper.values(), args);
                }

                return null;
            }
        });

        register(new SimplePlayerAdminCommand("simulateSelfDeath") {
            @Override
            protected void execute(Player player, String[] args) {
                final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cMust be in game to use this.");
                    return;
                }

                gamePlayer.setLastDamager(CF.getPlayer(player));
                gamePlayer.die(true);
            }
        });

        register(new SimplePlayerAdminCommand("testEternaClone") {
            @Override
            protected void execute(Player player, String[] strings) {
                new Cuboid(
                        BukkitUtils.defLocation(-9, 64, -271),
                        BukkitUtils.defLocation(-5, 66, -269)
                ).cloneBlocksTo(BukkitUtils.defLocation(
                        -2,
                        64,
                        -270
                ), true);

                Chat.sendMessage(player, "<color=#359751>Done!</>");
            }
        });

        register(new SimplePlayerAdminCommand("drawCircleAlong") {
            @Override
            protected void execute(Player player, String[] args) {
                final String string = getArgument(args, 0).toString();

                if (!string.equalsIgnoreCase("x") && !string.equalsIgnoreCase("z")) {
                    Chat.sendMessage(player, "&cShould be either along X or Z, not " + string);
                    return;
                }

                final double radius = 3.0d;
                final boolean isX = string.equalsIgnoreCase("x");
                final Location location = player.getLocation();

                for (double d = 0.0d; d < Math.PI * 2; d += Math.PI / 16) {
                    final double x = isX ? Math.sin(d) * radius : 0.0d;
                    final double y = Math.cos(d) * radius;
                    final double z = !isX ? Math.sin(d) * radius : 0.0d;

                    location.add(x, y, z);

                    // Do something
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 1);

                    location.subtract(x, y, z);
                }
            }
        });

        register(new SimpleAdminCommand("ram") {

            private final long GIGABYTE = 1_048_576L;

            @Override
            protected void execute(CommandSender sender, String[] args) {
                Chat.sendMessage(
                        sender,
                        "&6Current Memory Usage: &a%s/%s mb (Max: %s mb)",
                        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / GIGABYTE,
                        Runtime.getRuntime().totalMemory() / GIGABYTE,
                        Runtime.getRuntime().maxMemory() / GIGABYTE
                );

                final OperatingSystemMXBean mx = ManagementFactory.getOperatingSystemMXBean();

                if (mx instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
                    double cpuLoad = sunOsBean.getCpuLoad();
                    Chat.sendMessage(sender, "&6CPU Load: &a%.1f%%" + (cpuLoad <= 0.0d ? " &cNot supported?" : ""), cpuLoad * 100);
                }
            }
        });

        register(new SimplePlayerAdminCommand("gradient") {
            @Override
            protected void execute(Player player, String[] args) {
                // gradient #FROM #TO BOLD<bool> ...Text

                final Color from = Color.decode(getArgument(args, 0).toString());
                final Color to = Color.decode(getArgument(args, 1).toString());
                final boolean isBold = getArgument(args, 2).toString().equalsIgnoreCase("true");

                final String string = Chat.arrayToString(args, 3);
                final Gradient gradient = new Gradient(string);

                if (isBold) {
                    gradient.makeBold();
                }

                Chat.sendMessage(player, "&a&lOutput:");

                Chat.sendMessage(player, "&aLinear:");
                Chat.sendMessage(player, "");
                Chat.sendMessage(player, gradient.rgb(from, to, Interpolators.LINEAR));
                Chat.sendMessage(player, "");
                copyCommand(
                        player,
                        "new Gradient(%s).rgb(%s, %s, Interpolators.LINEAR)",
                        isBold,
                        string,
                        colorToString(from),
                        colorToString(to)
                );

                Chat.sendMessage(player, "&aQuadratic Fast -> Slow:");
                Chat.sendMessage(player, "");
                Chat.sendMessage(player, gradient.rgb(from, to, Interpolators.QUADRATIC_FAST_TO_SLOW));
                Chat.sendMessage(player, "");
                copyCommand(
                        player,
                        "new Gradient(%s).rgb(%s, %s, Interpolators.QUADRATIC_FAST_TO_SLOW)",
                        isBold,
                        string,
                        colorToString(from),
                        colorToString(to)
                );

                Chat.sendMessage(player, "&aQuadratic Slow -> Fast:");
                Chat.sendMessage(player, "");
                Chat.sendMessage(player, gradient.rgb(from, to, Interpolators.QUADRATIC_SLOW_TO_FAST));
                Chat.sendMessage(player, "");
                copyCommand(
                        player,
                        "new Gradient(%s).rgb(%s, %s, Interpolators.QUADRATIC_SLOW_TO_FAST)",
                        isBold,
                        string,
                        colorToString(from),
                        colorToString(to)
                );

            }

            private String colorToString(Color color) {
                return "%s, %s, %s".formatted(color.getRed(), color.getGreen(), color.getBlue());
            }

            private void copyCommand(Player player, String command, boolean bold, Object... format) {
                if (bold) {
                    command = command.replaceFirst("\\.", ".makeBold().");
                }

                command = command.formatted(format);

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.copyToClipboard(command),
                        LazyEvent.showText("&eClick to copy code"),
                        "&e&lCLICK TO COPY"
                );
            }
        });


        register(new SimplePlayerAdminCommand("debugDamageData") {
            @Override
            protected void execute(Player player, String[] strings) {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cNo handle.");
                    return;
                }

                final LivingGameEntity targetEntity = Collect.targetEntity(gamePlayer, 20.0d, 0.9d, e -> e.isNot(player));

                if (targetEntity == null) {
                    Chat.sendMessage(player, gamePlayer.getData());
                    return;
                }

                Chat.sendMessage(player, targetEntity.getData());
            }
        });

        register(new SimplePlayerAdminCommand("readSigns") {

            private Queue<Sign> queue;

            @Override
            protected void execute(Player player, String[] strings) {
                final NamedSignReader reader = new NamedSignReader(player.getWorld());

                if (queue == null) {
                    queue = reader.readAsQueue();
                    Chat.sendMessage(player, "&aFound %s signs. Use the command again to teleport to the next one.", queue.size());
                    return;
                }

                if (queue.peek() == null) {
                    Chat.sendMessage(player, "&cNo more signs!");
                    queue = null;
                    return;
                }

                final Sign sign = queue.poll();
                final String line = sign.getLine(0).replace("[", "").replace("]", "").toUpperCase();
                final Location location = sign.getLocation().add(0.5d, 0.0d, 0.5d); // center it
                final String locationString = BukkitUtils.locationToString(location);

                Chat.sendMessage(player, "");
                Chat.sendMessage(player, "&aNext: &l" + line.toUpperCase());

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.runCommand("/tp %s %s %s", location.getX(), location.getY(), location.getZ()),
                        LazyEvent.showText("&eClick to teleport!"),
                        "&6&lCLICK TO TELEPORT"
                );

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.copyToClipboard("%s %s %s".formatted(location.getX(), location.getY(), location.getZ())),
                        LazyEvent.showText("&eClick to copy coordinates!"),
                        "&6&lCLICK TO COPY COORDINATES"
                );

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.copyToClipboard(line.equalsIgnoreCase("spawn")
                                ? "addLocation(%s, 0, 0)".formatted(locationString)
                                : "addPackLocation(PackType.%s, %s)".formatted(line, locationString)),
                        LazyEvent.showText("&eClick to copy code!"),
                        "&6&lCLICK TO COPY CODE"
                );

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.runCommand(getUsage().toLowerCase()),
                        LazyEvent.showText("&aClick to show the next sign!"),
                        "&a&lNEXT"
                );
            }

        });

        register(new SimpleAdminCommand("listProfiles") {
            @Override
            protected void execute(CommandSender commandSender, String[] strings) {
                Manager.current().listProfiles();
            }
        });

        register(new CFCommand("testHologramHeights", PlayerRank.ADMIN) {

            private List<Hologram> holograms = Lists.newArrayList();
            private GameTask task;

            @Override
            protected void execute(Player player, String[] args, PlayerRank rank) {
                final double offsetY = getArgument(args, 0).toDouble(0.0d);
                final Location absoluteLocation = player.getLocation();
                final Location location = player.getLocation().subtract(0, offsetY, 0);

                if (task != null) {
                    task.cancel();
                    task = null;
                }

                holograms.forEach(Hologram::destroy);
                holograms.clear();

                task = new GameTask() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            absoluteLocation.add(i, 0, 0);
                            PlayerLib.spawnParticle(absoluteLocation, Particle.VILLAGER_HAPPY, 1);
                            absoluteLocation.subtract(i, 0, 0);
                        }
                    }
                }.runTaskTimer(0, 1);

                for (int i = 0; i < 10; i++) {
                    location.add(1, 0, 0);

                    holograms.add(new Hologram()
                            .setLines(createArray(i + 1))
                            .create(location)
                            .showAll());
                }

                Chat.sendMessage(player, "&aDone!");
            }

            private String[] createArray(int size) {
                final String[] array = new String[size];

                for (int i = 0; i < size; i++) {
                    array[i] = "test" + i;
                }

                return array;
            }

        });

        register(new CFCommand("testChestAnimation", PlayerRank.ADMIN) {
            @Override
            protected void execute(Player player, String[] args, PlayerRank rank) {
                // <command> (open, close)
                final String argument = getArgument(args, 0).toString().toLowerCase();

                final Block targetBlock = player.getTargetBlockExact(10);

                if (targetBlock == null) {
                    Message.error(player, "Not looking at a block.");
                    return;
                }

                final Material type = targetBlock.getType();

                if (type != Material.CHEST && type != Material.TRAPPED_CHEST && type != Material.ENDER_CHEST) {
                    Message.error(player, "Not looking at a chest!");
                    return;
                }

                switch (argument) {
                    case "open" -> {
                        CFUtils.playChestAnimation(targetBlock, true);
                        Message.success(player, "Playing open animation.");
                    }

                    case "close" -> {
                        CFUtils.playChestAnimation(targetBlock, false);
                        Message.success(player, "Playing close animation.");
                    }

                    default -> Message.error(player, "Invalid argument! Must be either 'open' or 'close'!");
                }
            }
        });

        register(new SimplePlayerAdminCommand("respawnGamePacks") {
            @Override
            protected void execute(Player player, String[] args) {
                final IGameInstance instance = Manager.current().getGameInstance();

                if (instance == null) {
                    Chat.sendMessage(player, "&cNo game instance.");
                    return;
                }

                final IntInt i = new IntInt();

                instance.getEnumMap().getMap().getGamePacks().forEach(pack -> {
                    pack.getActivePacks().forEach(activePack -> {
                        i.increment();
                        activePack.createEntity();
                    });
                });

                Chat.sendMessage(player, "&aRespawned %s packs.", i);
            }
        });

        register("accelerateGamePackSpawn", (player, args) -> {
            final GameInstance gameInstance = Manager.current().getGameInstance();

            if (gameInstance == null) {
                Chat.sendMessage(player, "&cCannot accelerate outside a game!");
                return;
            }

            gameInstance.getEnumMap().getMap().getGamePacks().forEach(GamePack::accelerate);

            Chat.sendMessage(player, "&aDone!");
        });

        register(new PluginsCommandOverride());

        register(new SimplePlayerAdminCommand("riptide") {

            private HumanNPC npc;
            private org.bukkit.entity.Entity entity;

            @Override
            protected void execute(Player player, String[] args) {
                if (npc != null) {
                    entity.remove();
                    entity = null;
                    npc.remove();
                    npc = null;
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                final Location location = player.getLocation();
                location.setYaw(90f);
                location.setPitch(90f);

                final Entities<? extends org.bukkit.entity.Entity> toSpawn = Entities.byName(args[0]);

                if (toSpawn == null) {
                    Chat.sendMessage(player, "&cInvalid entity = " + args[0]);
                    return;
                }

                final HumanNPC npc = new HumanNPC(location.clone().subtract(0.0, GVar.get("riptide", 1d), 0.0), "", player.getName());
                entity = toSpawn.spawn(location.clone().subtract(0.0, GVar.get("riptide2", 0.0d), 0.0d), self -> {
                    self.setGravity(false);
                });

                npc.bukkitEntity().setInvisible(true);
                npc.setCollision(false);
                npc.showAll();

                npc.setDataWatcherByteValue(8, (byte) 0x04);
                npc.updateDataWatcher();

                this.npc = npc;
                Chat.sendMessage(player, "&aSpawned!");

                new GameTask() {
                    @Override
                    public void run() {
                        if (entity == null) {
                            this.cancel();
                            return;
                        }

                        final Location entityLocation = entity.getLocation();
                        entityLocation.add(0.0, GVar.get("riptide3", 0.0d), 0.0);
                        entityLocation.setYaw(90f);
                        entityLocation.setPitch(90f);

                        npc.teleport(entityLocation);
                    }
                }.runTaskTimer(0, 1);
            }
        });

        register(new SimplePlayerAdminCommand("stopglow") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Pig spawn = Entities.PIG.spawn(player.getLocation());

                Glowing.glowInfinitly(spawn, ChatColor.GOLD, player);

                Runnables.runLater(() -> {
                    Glowing.stopGlowing(player, spawn);
                    Chat.sendMessage(player, "Stop glowing");
                }, 60);
            }
        });

        register(new SimplePlayerAdminCommand("testTitleAnimation") {
            @Override
            protected void execute(Player player, String[] args) {
                new TitleAnimation();
            }
        });

        register(new SimpleAdminCommand("debugTeams") {
            @Override
            protected void execute(CommandSender sender, String[] args) {
                for (GameTeam team : GameTeam.values()) {
                    Debug.info("%s = %s", team.getName(), team.listMembers());
                }
            }
        });

        register(new SimplePlayerAdminCommand("dumpBlockHardness") {
            @Override
            protected void execute(Player player, String[] strings) {
                Runnables.runAsync(() -> {
                    final Map<String, Float> hardness = Maps.newHashMap();

                    for (Material material : Material.values()) {
                        if (material.isBlock()) {
                            hardness.put(material.name(), material.getHardness());
                        }
                    }

                    final LinkedHashMap<String, Float> sorted = hardness.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    final String json = gson.toJson(sorted);
                    final File path = new File(Main.getPlugin().getDataFolder(), "hardness.json");

                    hardness.clear();
                    sorted.clear();

                    try (FileWriter writer = new FileWriter(path)) {
                        writer.write(json);

                        Runnables.runSync(() -> {
                            Chat.sendMessage(player, "&aDumped into &e%s&a!", path.getAbsolutePath());
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        register(new SimplePlayerAdminCommand("resetDaily") {
            @Override
            protected void execute(Player player, String[] strings) {
                final PlayerDatabase database = PlayerDatabase.getDatabase(player);
                database.dailyRewardEntry.setLastDaily(System.currentTimeMillis() - DailyReward.MILLIS_WHOLE_DAY);

                Chat.sendMessage(player, "&aReset daily!");
            }
        });

        register(new SimplePlayerAdminCommand("setAttackSpeed") {
            @Override
            protected void execute(Player player, String[] args) {
                final double value = getArgument(args, 0).toDouble();

                if (value == 0.0d) {
                    Chat.sendMessage(player, "&cInvalid value.");
                    return;
                }

                final ItemStack item = player.getInventory().getItemInMainHand();
                final ItemMeta meta = item.getItemMeta();

                if (meta == null) {
                    Chat.sendMessage(player, "&cItem has no meta.");
                    return;
                }

                meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
                meta.addAttributeModifier(
                        Attribute.GENERIC_ATTACK_SPEED,
                        new AttributeModifier("SPEED", value, AttributeModifier.Operation.ADD_NUMBER)
                );

                item.setItemMeta(meta);

                Chat.sendMessage(player, "&aSuccess! Set attack speed to " + value);
            }
        });

        register(new SimplePlayerAdminCommand("skullBlockFace") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Block block = player.getTargetBlockExact(100);

                if (block == null) {
                    Chat.sendMessage(player, "&cNo block in sight.");
                    return;
                }

                final BlockState state = block.getState();
                if (!(state instanceof Skull skull)) {
                    Chat.sendMessage(player, "&cTarget block is not a skull.");
                    return;
                }

                Chat.sendMessage(player, "&aBlock Face: &l" + skull.getRotation().name());
            }
        });

        register(new SimplePlayerAdminCommand("getTextBlockTestItem") {
            @Override
            protected void execute(Player player, String[] strings) {
                player.getInventory().addItem(ItemBuilder.of(Material.FEATHER).addTextBlockLore("""
                        This is a text block lore test, and this should be the first paragraph.
                                                        
                        &a;;Where this is the second one, and it's also all green!
                                                        
                                 
                        Two paragraphs, wow!
                                                        
                        &c;;And I know your name, %s!
                        """, player.getName()).asIcon());
            }
        });

        register(new SimplePlayerAdminCommand("spawnWither") {

            @Override
            protected void execute(Player player, String[] args) {
                if (args.length != 3) {
                    Chat.sendMessage(player, "&cForgot (from:Int), (to:Int) and (speed:Long).");
                    return;
                }

                final int from = Validate.getInt(args[0]);
                final int to = Validate.getInt(args[1]);
                final long speed = Validate.getLong(args[2]);

                new AnimatedWither(LocationHelper.getInFront(player.getLocation(), 6)) {

                    @Override
                    public void onInit(@Nonnull Wither wither) {
                        wither.setSilent(true);
                    }

                    @Override
                    public void onStart() {
                        Chat.sendMessage(player, "&aStarted %s->%s with speed %s", from, to, speed);
                    }

                    @Override
                    public void onStop() {
                        Chat.sendMessage(player, "&aStopped");
                        doLater(wither::remove, 60);
                    }

                    @Override
                    public void onTick(int tick) {
                        Chat.sendMessage(player, "&a>>" + getInvul());
                    }
                }.startAnimation(from, to, speed);
            }

        });

        register(new SimplePlayerAdminCommand("asyncDbTest") {

            private final Document FILTER = new Document("_dev", "hapyl");
            Document document;

            @Override
            protected void execute(Player player, String[] args) {
                final MongoCollection<Document> collection = getPlugin().database.getPlayers();
                document = collection.find(FILTER).first();

                if (document == null) {
                    document = new Document(FILTER);
                    collection.insertOne(document);
                }

                if (args.length >= 1) {
                    final String arg0 = args[0];

                    if (arg0.equalsIgnoreCase("dump")) {
                        document.forEach((k, v) -> {
                            Debug.info("%s = %s", k, v);
                        });
                        return;
                    }

                    if (args.length >= 2) {
                        final String arg1 = args[1];

                        if (arg0.equalsIgnoreCase("get")) {
                            final String get = document.get(arg1, "null");

                            Chat.sendMessage(player, "&e%s = &6%s", arg1, get);
                        }
                        else if (arg0.equalsIgnoreCase("set")) {
                            if (args.length < 3) {
                                Chat.sendMessage(player, "Forgot the value, stupid.");
                                return;
                            }

                            final String toSet = args[2];

                            Runnables.runAsync(() -> {
                                document = collection.findOneAndUpdate(document, Updates.set(arg1, toSet));
                                Chat.sendMessage(player, "&aSet and update %s.", toSet);
                            });
                        }
                    }

                    return;
                }

                Chat.sendMessage(player, "&cInvalid usage, idiot.");
            }
        });

        register(new SimplePlayerAdminCommand("spawnme") {

            HumanNPC npc;

            @Override
            protected void execute(Player player, String[] strings) {
                if (npc != null) {

                    if (strings.length > 0) {
                        Chat.sendMessage(player, "&aApplying skin...");
                        npc.setSkinAsync(strings[0]);
                        return;
                    }

                    npc.remove();
                    npc = null;
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                npc = new HumanNPC(player.getLocation(), player.getName(), player.getName());
                npc.setLookAtCloseDist(5);
                npc.addDialogLine("Hello {player}, my name is {name} and I'm here as a test!", 40);
                npc.addDialogLine("I'm located at {location}", 20);
                npc.addDialogLine("That's it then, bye &c❤");
                npc.setInteractionDelay(60);

                npc.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                npc.setPose(NPCPose.CROUCHING);

                npc.show(player);

                Chat.sendMessage(player, "&aSpawned!");
            }
        });

        register(new SimplePlayerAdminCommand("spawnDancingPiglin") {
            @Override
            protected void execute(Player player, String[] args) {
                final Piglin piglin = Entities.PIGLIN.spawn(player.getLocation());
                final Entity minecraftEntity = Reflect.getMinecraftEntity(piglin);

                piglin.setCustomName(new Gradient("Dancing Piglin").rgb(Color.PINK, Color.RED, Interpolators.LINEAR));
                piglin.setCustomNameVisible(true);
                piglin.setImmuneToZombification(true);
                Chat.sendMessage(player, "&aSpawned!");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (piglin.isDead()) {
                            this.cancel();
                            return;
                        }

                        Reflect.setDataWatcherValue(minecraftEntity, DataWatcherType.BOOL, 19, true);
                    }
                }.runTaskTimer(main, 0, 1);
            }
        });

        // these are small shortcuts not feeling creating a class D:

        register("start", (player, args) -> {
            player.performCommand("cf start " + Chat.arrayToString(args, 0));
        });

        register(new TestNpcDeathAnimationCommand("testNpcDeathAnimation"));

        register("testNpcTeleport", (player, args) -> {
            final Human human = HumanNPC.create(player.getLocation());
            human.showAll();

            new TickingGameTask() {
                @Override
                public void run(int tick) {
                    if (tick >= 100) {
                        human.remove();
                        cancel();
                        Chat.sendMessage(player, "&cRemoved!");
                        return;
                    }

                    human.teleport(player.getLocation());
                }
            }.runTaskTimer(0, 1);
        });

        register(new SimpleAdminCommand("stop") {
            // true -> stop server, false -> stop game instance

            @Override
            protected void execute(CommandSender sender, String[] args) {
                if (sender instanceof ConsoleCommandSender) {
                    Bukkit.dispatchCommand(sender, "minecraft:stop");
                    return;
                }

                final String arg = getArgument(args, 0).toString();
                Bukkit.dispatchCommand(sender, (arg.equalsIgnoreCase("server") || arg.equalsIgnoreCase("s")) ? "minecraft:stop" : "cf stop");

                Chat.sendMessage(sender, "&6&lWARNING &eDeprecated usage of &n/stop&e command, prefer &n/cf stop&e!");
            }
        });

    }

    // Have to use reflection here, since Paper is really annoying.
    @SuppressWarnings("all")
    private void unregisterAnnoyingPaperCommandsThatICannotOverride() {
        Set<String> commandsToRemove = Sets.newHashSet();

        commandsToRemove.add("plugins");
        commandsToRemove.add("tps");

        try {
            final SimpleCommandMap commandMap = CommandProcessor.getCommandMap();
            final Iterator<Command> iterator = commandMap.getCommands().iterator();
            final Field field = FieldUtils.getDeclaredField(SimpleCommandMap.class, "knownCommands", true);
            final Map<String, Command> map = (Map<String, Command>) field.get(commandMap);

            for (String commandName : commandsToRemove) {
                final Command command = commandMap.getCommand(commandName);

                if (command == null) {
                    continue;
                }

                // unregister aliases as well
                for (String alias : command.getAliases()) {
                    map.remove(alias);
                }

                //command.unregister(commandMap);
                map.remove(commandName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterAlias(String commandName, SimpleCommandMap commandMap, Map<String, Command> map) {
        final Command command = commandMap.getCommand(commandName);

        if (command == null) {
            return;
        }

        command.unregister(commandMap);
        map.remove(commandName);
    }

    private void register(String name, BiConsumer<Player, String[]> consumer) {
        processor.registerCommand(new SimplePlayerAdminCommand(name) {
            @Override
            protected void execute(Player player, String[] args) {
                consumer.accept(player, args);
            }
        });
    }

    private void register(SimpleCommand command) {
        processor.registerCommand(command);
    }

}
