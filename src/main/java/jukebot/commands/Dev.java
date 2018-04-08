package jukebot.commands;

import jukebot.JukeBot;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@CommandProperties(description = "Developer menu", category = CommandProperties.category.MISC, developerOnly = true)
public class Dev implements Command {

    public void execute(GuildMessageReceivedEvent e, String query) {

        String[] args = query.split("\\s+");

        if (args[0].equalsIgnoreCase("preload")) {
            if (JukeBot.isSelfHosted) {
                e.getChannel().sendMessage("Command unavailable").queue();
                return;
            }
            if (args.length < 3) {
                e.getChannel().sendMessage("Missing arg `key`").queue();
            } else {
                JukeBot.recreatePatreonApi(args[1]);
                e.getMessage().addReaction("\uD83D\uDC4C").queue();
            }
        } else {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(JukeBot.embedColour)
                    .setTitle("Debug Subcommands")
                    .setDescription("`->` preload <key>")
                    .build()
            ).queue();
        }
    }

}