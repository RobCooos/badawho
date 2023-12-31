package me.hapyl.fight.npc;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.reflect.npc.Human;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public final class HumanManager implements Listener {

    private final Map<Player, PlayerNPC> playerNpc;

    public HumanManager(Main main) {
        this.playerNpc = Maps.newHashMap();

        Bukkit.getPluginManager().registerEvents(this, main);

        for (PersistentNPCs value : PersistentNPCs.values()) {
            value.getNpc().showAll();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateAll(player);
        }
    }

    private void deletePlayerNpc(Player player) {
        final Human human = playerNpc.get(player);

        if (human != null) {
            human.remove();
        }

        playerNpc.remove(player);
    }

    @EventHandler()
    public void handlePlayerJoinEvent(PlayerJoinEvent ev) {
        updateAll(ev.getPlayer());
    }

    @EventHandler()
    public void handlePlayerQuitEvent(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();

        deletePlayerNpc(player);
    }

    public void updateAll(Player player) {
        Runnables.runLater(() -> {
            for (PersistentNPCs npc : PersistentNPCs.values()) {
                npc.getNpc().show(player);
            }
        }, 10L);
    }


}
