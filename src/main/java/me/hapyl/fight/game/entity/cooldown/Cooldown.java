package me.hapyl.fight.game.entity.cooldown;

/**
 * Keep in mind cooldowns are in <b>millis <i>not ticks</i></b>!
 */
public enum Cooldown {

    DWARF_LAVA(1000),
    BEAM_TOUCH(500),
    FEROCITY(100);

    public final long duration;

    Cooldown(long durationMillis) {
        this.duration = durationMillis;
    }
}
