package core.commands;

import core.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author Veteran Software by Ague Mort
 */
public class Permissions implements Command {

    /**
     * Used to determine if appropriate arguments exist
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     * @return boolean true if criteria is met, false if criteria not met
     */
    @Override
    public final boolean called(String args, GuildMessageReceivedEvent event) {
        return true;
    }

    /**
     * Action taken after the command is verified
     *
     * @param args  Arguments being passed
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void action(String args, GuildMessageReceivedEvent event) {

    }

    /**
     * Returns help info for the command
     *
     * @param event From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void help(GuildMessageReceivedEvent event) {

    }

    /**
     * Runs specified scripts which are determined by {success}
     *
     * @param success [boolean]
     * @param event   From JDA: GuildMessageReceivedEvent
     */
    @Override
    public final void executed(boolean success, GuildMessageReceivedEvent event) {

    }
}
