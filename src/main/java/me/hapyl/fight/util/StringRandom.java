package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.util.CollectionUtils;

import javax.annotation.Nonnull;

public final class StringRandom {

    private StringRandom() {
    }

    @Nonnull
    public static String of(@Nonnull @Range(min = 1) String... values) {
        if (values.length < 1) {
            return "";
        }

        return CollectionUtils.randomElement(values, "");
    }

}
