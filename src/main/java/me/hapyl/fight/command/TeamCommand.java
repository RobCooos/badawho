package me.hapyl.fight.command;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.gui.TeamSelectGUI;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.entity.Player;

public class TeamCommand extends SimplePlayerCommand {
    public TeamCommand(String name) {
        super(name);

        addCompleterValues(1, "join", "leave");
        addCompleterValues(2, GameTeam.valuesStrings());
    }

    @Override
    protected void execute(Player player, String[] args) {
        // team - opens GUI
        // team toggle - toggles team mode
        // team join (team) - join certain team
        // team leave (team) - leave certain team

        if (Manager.current().isGameInProgress()) {
            Chat.sendMessage(player, "&cCannot modify team during a game!");
            return;
        }

        if (args.length == 0) {
            new TeamSelectGUI(player);
            return;
        }

        final String string = getArgument(args, 0).toString();
        final GameTeam team = getArgument(args, 1).toEnum(GameTeam.class);

        if (team == null) {
            Chat.sendMessage(player, "&cInvalid team!");
            return;
        }

        if (string.equalsIgnoreCase("join")) {
            if (team.isFull()) {
                Message.error(player, "Cannot join {} team because it's full!", team.getName());
                return;
            }

            team.addMember(player);
        }
    }

}
