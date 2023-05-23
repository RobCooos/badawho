package me.hapyl.fight;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import me.hapyl.fight.build.NamedSignReader;
import me.hapyl.fight.cmds.*;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.IGameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.TitleAnimation;
import me.hapyl.fight.game.damage.DamageHandler;
import me.hapyl.fight.game.heroes.storage.extra.AnimatedWither;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.LazyEvent;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.command.*;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.nn.IntInt;
import me.hapyl.spigotutils.module.reflect.DataWatcherType;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import me.hapyl.spigotutils.module.util.*;
import net.minecraft.world.entity.Entity;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.stream.Collectors;

public class CommandRegistry extends DependencyInjector<Main> {

    private final CommandProcessor processor;

    public CommandRegistry(Main main) {
        super(main);
        this.processor = new CommandProcessor(main);

        register(new HeroCommand("hero"));
        register(new GameCommand("cf"));
        register(new ReportCommandCommand("report"));
        register(new ParticleCommand("part"));
        register(new GameEffectCommand("gameEffect"));
        register(new MapCommand("map"));
        register(new ModeCommand("mode"));
        register(new AdminCommand("admin"));
        register(new DebugBooster("debugBooster"));
        register(new TrialCommand("trial"));
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

        register(new SimplePlayerAdminCommand("debugDamageData") {
            @Override
            protected void execute(Player player, String[] strings) {
                final LivingEntity targetEntity = Utils.getTargetEntity(player, 20.0d, 0.9d, e -> e != player);

                if (targetEntity == null) {
                    Chat.sendMessage(player, DamageHandler.getDamageData(player));
                    return;
                }

                Chat.sendMessage(player, DamageHandler.getDamageData(targetEntity));
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
                        LazyEvent.runCommand("tp %s %s %s", location.getX(), location.getY(), location.getZ()),
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
                        LazyEvent.runCommand(getName()),
                        LazyEvent.showText("&aClick to show next sign!"),
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

        register(new SimplePlayerAdminCommand("respawnGamePacks") {
            @Override
            protected void execute(Player player, String[] args) {
                final IGameInstance instance = Manager.current().getGameInstance();

                if (instance == null) {
                    Chat.sendMessage(player, "&cNo game instance.");
                    return;
                }

                final IntInt i = new IntInt();

                instance.getMap().getMap().getGamePacks().forEach(pack -> {
                    pack.getActivePacks().forEach(activePack -> {
                        i.increment();
                        activePack.createEntity();
                    });
                });

                Chat.sendMessage(player, "&aRespawned %s packs.", i);
            }
        });

        register(new SimplePlayerAdminCommand("riptide") {

            private final Set<Player> riptideActive = new HashSet<>();
            private HumanNPC npc;

            @Override
            protected void execute(Player player, String[] args) {

                // launch
                if (args.length >= 1 && npc != null) {
                    Chat.sendMessage(player, "&aLaunch started!");
                    new GameTask() {
                        private int maxTick = 20;

                        @Override
                        public void run() {
                            if (maxTick-- < 0) {
                                Chat.sendMessage(player, "&aLaunch finished!");
                                this.cancel();
                                return;
                            }

                            final Location location = npc.getLocation();
                            location.setYaw(90f);
                            location.setPitch(90f);
                            location.add(player.getEyeLocation().getDirection().multiply(0.25d));
                            npc.setLocation(location);

                        }
                    }.runTaskTimer(0, 1);

                    return;
                }

                if (npc != null) {
                    npc.remove();
                    npc = null;
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                final Location location = player.getLocation();
                location.add(0.0d, 1.8d, 0.0d);
                location.setYaw(90f);
                location.setPitch(90f);

                final HumanNPC npc = new HumanNPC(location, "", player.getName());
                npc.bukkitEntity().setInvisible(true);
                npc.showAll();

                npc.setDataWatcherByteValue(8, (byte) 0x04);

                this.npc = npc;
                Chat.sendMessage(player, "&aSpawned!");

                if (true) {
                    return;
                }

                if (riptideActive.contains(player)) {
                    riptideActive.remove(player);
                    return;
                }

                player.setVelocity(player.getLocation().getDirection().multiply(1.25d));
                new GameTask() {
                    private int maxTick = 40;

                    @Override
                    public void run() {
                        if (maxTick-- < 0) {
                            this.cancel();
                        }
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

        register(new SimplePlayerAdminCommand("dropDatabase") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Database database = Main.getPlugin().getDatabase();

                if (!database.isDevelopment()) {
                    Chat.sendMessage(player, "&cCannot drop PROD database!");
                    return;
                }

                final MongoDatabase mongoDatabase = database.getDatabase();

                for (String string : mongoDatabase.listCollectionNames()) {
                    mongoDatabase.getCollection(string).deleteMany(new Document());
                }

                Chat.sendMessage(player, "&aDropped database!");

                // Reload player database
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    PlayerDatabase.getDatabase(onlinePlayer).load();
                }
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

        register(tinyCommand("start", (player, args) -> {
            player.performCommand("cf start " + (args.length > 0 ? args[0] : ""));
        }));

        register(new SimpleAdminCommand("stop") {
            // true -> stop server, false -> stop game instance

            @Override
            protected void execute(CommandSender sender, String[] args) {
                final boolean type = args.length == 1 && (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("s"));

                Bukkit.dispatchCommand(sender, type ? "minecraft:stop" : "cf stop");
            }

            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return super.completerSort(Arrays.asList("game", "server"), args);
            }
        });

    }

    private SimplePlayerCommand tinyCommand(String name, Action.AB<Player, String[]> action) {
        return new SimplePlayerCommand(name) {
            @Override
            protected void execute(Player player, String[] strings) {
                action.use(player, strings);
            }
        };
    }

    private void register(SimpleCommand command) {
        processor.registerCommand(command);
    }

}
