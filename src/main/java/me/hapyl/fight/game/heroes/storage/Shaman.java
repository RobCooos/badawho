package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.extra.ActiveTotem;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Shaman extends Hero implements ComplexHero {
    public Shaman() {
        super("Shaman");

        setItem(Material.OBSIDIAN);

        setWeapon(new Weapon(Material.STICK).setName("Shaman's Weapon").setDamage(5.0d));

    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Manager.current()
                        .getCurrentGame()
                        .getAlivePlayers(Heroes.SHAMAN)
                        .stream()
                        .map(player -> player.getPlayer())
                        .forEach(player -> {
                            final ActiveTotem totem = TalentHandle.TOTEM.getTargetTotem(player);
                            if (totem == null) {
                                TalentHandle.TOTEM.defaultAllTotems(player);
                                return;
                            }

                            totem.setActive();
                        });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public void useUltimate(Player player) {

    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TOTEM.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.TOTEM_SLOWING_AURA.getTalent();
    }

    @Override
    public Talent getThirdTalent() {
        return Talents.TOTEM_HEALING_AURA.getTalent();
    }

    @Override
    public Talent getFourthTalent() {
        return Talents.TOTEM_CYCLONE_AURA.getTalent();
    }

    @Override
    public Talent getFifthTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}