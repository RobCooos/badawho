package me.hapyl.fight.event;

import me.hapyl.fight.event.io.DamageOutput;

public class DamageData extends DamageOutput {
    public DamageData(double damage) {
        super(damage);
    }

    public DamageData(double damage, boolean cancelEvent) {
        super(damage, cancelEvent);
    }
}
