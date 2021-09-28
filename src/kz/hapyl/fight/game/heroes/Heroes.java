package kz.hapyl.fight.game.heroes;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.heroes.storage.*;
import kz.hapyl.fight.game.heroes.storage.DrEd;
import kz.hapyl.fight.game.heroes.storage.Ender;
import kz.hapyl.fight.game.heroes.storage.Spark;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public enum Heroes {

	// https://tools-unite.com/tools/random-picker-wheel?names=Blast Knight,Juju,KM,Librarian,Ninja,Shark,Swooper,Taker,Tamer

	ARCHER(new Archer()),
	ALCHEMIST(new Alchemist()),
	MOONWALKER(new Moonwalker()),
	HERCULES(new Hercules()),
	MAGE(new Mage()),
	PYTARIA(new Pytaria()),
	TROLL(new Troll()),
	NIGHTMARE(new Nightmare()),
	DR_ED(new DrEd()),
	ENDER(new Ender()),
	SPARK(new Spark()),
	SHADOW_ASSASSIN(new ShadowAssassin()),
	WITCHER(new WitcherClass()),
	VORTEX(new Vortex()),
	FREAZLY(new Freazly()),
	DARK_MAGE(new DarkMage()),

	;

	private final Hero hero;

	Heroes(Hero hero) {
		this.hero = hero;
		if (hero instanceof Listener listener) {
			Main.getPlugin().addEvent(listener);
		}
	}

	public Hero getHero() {
		return hero;
	}

	public List<GamePlayer> getPlayers() {
		final List<GamePlayer> players = new ArrayList<>();
		final GameInstance gameInstance = Manager.current().getGameInstance();
		if (gameInstance == null) {
			return players;
		}
		gameInstance.getPlayers().forEach((uuid, gp) -> {
			if (isSelected(gp.getPlayer())) {
				players.add(gp);
			}
		});
		return players;
	}

	public List<GamePlayer> getAlivePlayers() {
		final List<GamePlayer> players = getPlayers();
		players.removeIf(filter -> !filter.isAlive());
		return players;
	}

	public boolean isSelected(Player player) {
		return Manager.current().getSelectedHero(player) == this;
	}

}
