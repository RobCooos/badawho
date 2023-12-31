package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.Prefixed;

import javax.annotation.Nonnull;

/**
 * Represents hero's origin.
 */
public enum Affiliation implements Prefixed {

    NOT_SET(Color.ERROR + "❌", "Not set.", "Not set."),

    KINGDOM(Color.DARK_GOLDENROD + "🏰", "Kingdom", "A royal kingdom."),
    THE_WITHERS(Color.WITHERS + "👾", "The Withers", "An ancient race of withers, who bear hatred towards humanity."),
    UNKNOWN(Color.DEFAULT + "❓", "Unknown", "The origin of this hero is a mystery..."),
    ;

    private final String prefix;
    private final String name;
    private final String description;

    Affiliation(String prefix, String name, String description) {
        this.prefix = prefix;
        this.name = name;
        this.description = description;
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return prefix + " " + name;
    }
}
