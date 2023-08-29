package me.hapyl.fight.game.talents.archive.bloodfiend;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class TwinClaws extends Talent implements Listener {

    @DisplayField protected final double twinClawDamage = 20.0d;
    @DisplayField protected final int pillarDuration = Tick.fromSecond(20);
    @DisplayField protected final short pillarHeight = 7;
    @DisplayField protected final short pillarClicks = 10;

    private final Map<Player, Candlebane> pillars;

    public TwinClaws() {
        super("Twinclaws");

        setDescription("""
                Launch two giant claws. One in front, one behind.
                                
                If a claw hits an enemy, it will summon a &cCandlebane &cPillar&7 for &b{pillarDuration}&7, that will taunt all &cbitten&7 players.
                                
                If the duration expires and the pillar is not broken, all &cbitten&7 players will &4die&7.
                                
                &e;;Only one pillar may exist at the same time.
                """);

        setItem(Material.ACACIA_FENCE);
        setDuration(30);
        setCooldownSec(15);

        pillars = Maps.newHashMap();
    }

    @Override
    public void onDeath(Player player) {
        final Candlebane pillar = pillars.remove(player);

        if (pillar != null) {
            pillar.remove();
        }
    }

    @Override
    public void onStop() {
        pillars.values().forEach(Candlebane::remove);
        pillars.clear();
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);
        final World world = location.getWorld();

        if (world == null) {
            return Response.error("cannot spawn in an unloaded world");
        }

        final Vector vectorFront = location.getDirection().normalize().multiply(0.5d);
        final Location locationFront = location.clone().add(vectorFront);

        final Vector vectorBack = vectorFront.clone().multiply(-1);
        final Location locationBack = location.clone().add(vectorBack);

        final int duration = getDuration();

        new TwinClaw(player, locationFront, vectorFront, duration);
        new TwinClaw(player, locationBack, vectorBack, duration);

        return Response.OK;
    }

    @Nullable
    public Candlebane getPillar(Player player) {
        return pillars.get(player);
    }

    public void spawnPillar(Player player, Location location) {
        final Candlebane oldPillar = pillars.remove(player);

        if (oldPillar != null) {
            oldPillar.remove();
            Chat.sendMessage(player, "&aYour previous pillar was removed!");
        }

        pillars.put(player, new Candlebane(player, location) {
            @Override
            public void onTaskStop() {
                pillars.remove(player);
            }
        });
    }

    @Nonnull
    protected Map<Player, Candlebane> getPillars() {
        return pillars;
    }
}