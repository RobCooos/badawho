package kz.hapyl.fight.game.maps.maps;

import kz.hapyl.fight.game.maps.GameMap;
import kz.hapyl.fight.game.maps.Size;
import kz.hapyl.fight.game.maps.features.JapanFeature;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class JapanMap extends GameMap implements Listener {
	public JapanMap() {
		super("Japan", "This map based on real-life temple &e平等院 (Byōdō-in)&7!", Material.PINK_GLAZED_TERRACOTTA);
		this.setSize(Size.LARGE);
		this.addFeature(new JapanFeature());
		this.addLocation(300, 64, 0, 180, 0);
	}

}
