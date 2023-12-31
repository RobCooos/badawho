package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Locale;

public class CachedHeroItem {

    private final Hero hero;

    private ItemStack selectItem;
    private ItemStack detailsItem;

    public CachedHeroItem(Hero hero) {
        this.hero = hero;
    }

    @Nonnull
    public ItemStack getDetailsItem() {
        if (detailsItem == null) {
            final ItemBuilder builder = new ItemBuilder(hero.getItem());
            final Archetype archetype = hero.getArchetype();

            builder.setName(hero.toString())
                    .addLore()
                    .addLore("&7Archetype: " + archetype)
                    .addSmartLore(archetype.getDescription(), "&8&o");

            // Affiliation
            if (hero.getOrigin() != Affiliation.NOT_SET) {
                builder.addLore();
                builder.addLore("&7Affiliation: " + hero.getOrigin());
                builder.addSmartLore(hero.getOrigin().getDescription(), "&8&o");
            }

            // Attributes
            builder.addLore().addLore("&e&lAttributes:");
            final HeroAttributes attributes = hero.getAttributes();

            attributes.forEachNonZero((type, value) -> {
                builder.addLore(" &7%s: &b%s", type.getName(), type.getFormatted(attributes));
            });

            builder.addLore();
            builder.addSmartLore(hero.getDescription(), "&8&o");

            detailsItem = builder.asIcon();
        }

        return detailsItem;
    }

    @Nonnull
    public ItemStack getSelectItem() {
        if (selectItem == null) {
            final ItemBuilder builder = new ItemBuilder(hero.getItem())
                    .setName(hero.toString())
                    .addLore("&8/hero " + Heroes.byHandle(hero).name().toLowerCase(Locale.ROOT))
                    .addLore()
                    .addLore("&7Archetype: " + hero.getArchetype())
                    .addLoreIf("&7Affiliation: " + hero.getOrigin(), hero.getOrigin() != Affiliation.NOT_SET)
                    .addLore();

            final HeroAttributes attributes = hero.getAttributes();
            builder.addLore("&e&lAttributes: ");
            builder.addLore(attributes.getLore(AttributeType.MAX_HEALTH));
            builder.addLore(attributes.getLore(AttributeType.ATTACK));
            builder.addLore(attributes.getLore(AttributeType.DEFENSE));
            builder.addLore(attributes.getLore(AttributeType.SPEED));

            builder.addLore();

            builder.addTextBlockLore(hero.getDescription(), "&8&o", 35, CFUtils.DISAMBIGUATE);

            if (hero instanceof ComplexHero) {
                builder.addLore();
                builder.addTextBlockLore("""
                        &6&lComplex Hero!
                        This hero is more difficult to play than others. Thus is &nnot&7 recommended for newer players.
                        """);
            }

            // Usage
            builder.addLore().addLore("&eLeft Click to select").addLore("&6Right Click for details");
            selectItem = builder.asIcon();
        }

        return selectItem;
    }

}
