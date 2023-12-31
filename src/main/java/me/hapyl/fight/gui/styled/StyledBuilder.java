package me.hapyl.fight.gui.styled;

import me.hapyl.fight.game.color.Color;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public interface StyledBuilder {

    @Nonnull
    default ItemStack asIcon(@Nonnull String name, @Nonnull String... lore) {
        return toBuilder().setName(name).setLore(List.of(lore)).asIcon();
    }

    @Nonnull
    default ItemStack asIconWithLore(@Nonnull String... lore) {
        final ItemBuilder builder = toBuilder();

        for (String s : lore) {
            builder.addLore(s);
        }

        return builder.asIcon();
    }

    @Nonnull
    default ItemStack asIcon() {
        return toBuilder().asIcon();
    }

    @Nonnull
    default ItemStack asButton(@Nonnull String clickTo) {
        return toBuilder().addLore().addLore(Color.BUTTON.color("Click to {action}!", clickTo)).asIcon();
    }

    @Nonnull
    ItemBuilder toBuilder();

}
