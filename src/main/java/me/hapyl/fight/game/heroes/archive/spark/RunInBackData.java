package me.hapyl.fight.game.heroes.archive.spark;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;

public record RunInBackData(GamePlayer player, Location location, double health) {
}
