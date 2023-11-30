package me.hapyl.fight.game.attribute;

import java.util.Random;

public class AttributeRandom extends Random {

    private final Attributes attributes;

    public AttributeRandom(Attributes attributes) {
        this.attributes = attributes;
    }

    public boolean checkBound(AttributeType type) {
        final double value = type.get(attributes);

        return value >= 1.0d || (value > 0 && checkBound(value));
    }

    public boolean checkBound(double bound) {
        if (bound < 0.0d) {
            return false;
        }
        else if (bound >= 1.0d) {
            return true;
        }

        final double value = nextDouble(0.0d, 1.0d);
        return value < bound;
    }

}
