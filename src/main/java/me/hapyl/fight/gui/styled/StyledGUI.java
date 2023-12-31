package me.hapyl.fight.gui.styled;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.gui.StrictlyLobbyGUI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class StyledGUI extends PlayerGUI implements Styled {

    private final Size size;

    public StyledGUI(Player player, String name, Size size) {
        super(player, name, size.size);

        this.size = size;
        StaticStyledGUI.openFx(this);
    }

    @Nonnull
    @Override
    public Size getStyleSize() {
        return size;
    }

    @Override
    public final void openInventory() {
        if (this instanceof StrictlyLobbyGUI && Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cYou can only open this GUI in the lobby!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        clearItems();
        clearClickEvents();

        StaticStyledGUI.updateInventory(this);
        super.openInventory();
    }

    @Override
    public final void update() {
        openInventory();
    }

    @Override
    public void setHeader(@Nonnull ItemStack item) {
        StaticStyledGUI.setHeader(this, item);
    }

    @Override
    public void setPanelItem(int index, @Nonnull ItemStack item, @Nullable me.hapyl.spigotutils.module.inventory.gui.Action action) {
        StaticStyledGUI.setPanelItem(this, index, item, action);
    }

    @Override
    public void fillRow(int row, @Nonnull ItemStack item) {
        StaticStyledGUI.fillRow(this, row, item);
    }

}
