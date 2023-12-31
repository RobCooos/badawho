package me.hapyl.fight.game.term;

import org.bukkit.Material;

import javax.annotation.Nonnull;

public enum Terms {

    TRUE_DAMAGE(new Term(
            Material.BONE_MEAL,
            Category.SYSTEM,
            "True Damage",
            "True Damage ignores target defense and always deals the same amount of damage."
    )),

    SNAPSHOT(new Term(
            Material.YELLOW_DYE,
            Category.SYSTEM,
            "Snapshot",
            "The damage calculations for snapshot damage are calculated once, instead of every time the damage is dealt."
    )),

    ;

    private final Term term;

    Terms(Term term) {
        this.term = term;
    }

    @Nonnull
    public Term getTerm() {
        return term;
    }
}
