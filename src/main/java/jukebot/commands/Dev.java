package jukebot.commands;

import jukebot.Database;
import jukebot.JukeBot;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import jukebot.utils.Context;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Random;

@CommandProperties(description = "Developer menu", category = CommandProperties.category.MISC, developerOnly = true)
public class Dev implements Command {

    private Random r = new Random();
    private String[] quotes = {
            "Master gave dobby a sock",
            "Needs more jellybeans",
            "what's this do?",
            "dev commands op plz nerf",
            "insert floppy disk into drive A:",
            "OOOOOOH SHINY"
    };

    public void execute(final Context context) {

        final String[] args = context.getArgs();

        if (args[0].equalsIgnoreCase("preload")) {
            if (JukeBot.isSelfHosted) {
                context.sendEmbed("Command Unavailable", "This command is unavailable on self-hosted JukeBot.");
                return;
            }
            if (args.length < 2) {
                context.sendEmbed("Missing Required Arg", "You need to specify `key`");
            } else {
                JukeBot.createPatreonApi(args[1]);
                context.getMessage().addReaction("\uD83D\uDC4C").queue();
            }
        } else if (args[0].equalsIgnoreCase("block")) {
            if (args.length < 2) {
                context.sendEmbed("Missing Required Arg", "You need to specify `userId`");
            } else {
                Database.blockUser(Long.parseLong(args[1]));
                context.sendEmbed("User Blocked", args[1] + " is now blocked from using JukeBot.");
            }
        } else if (args[0].equalsIgnoreCase("unblock")) {
            if (args.length < 2) {
                context.sendEmbed("Missing Required Arg", "You need to specify `userId`");
            } else {
                Database.unblockUser(Long.parseLong(args[1]));
                context.sendEmbed("User Unblocked", args[1] + " can now use JukeBot.");
            }
        } else if (args[0].equalsIgnoreCase("forcedc")) {
            if (args.length < 2) {
                context.sendEmbed("Missing Required Arg", "You need to specify `guildId`");
            }

            final Guild g = JukeBot.shardManager.getGuildById(args[1]);
            if (g == null) {
                context.sendEmbed("Unable to find Guild", "No guilds found matching that ID");
            } else {
                g.getAudioManager().closeAudioConnection();
            }
        } else if (args[0].equalsIgnoreCase("forcerc")) {
            if (args.length < 2) {
                context.sendEmbed("Missing Required Arg", "You need to specify `guildId`");
            }

            if (context.getMember().getVoiceState().getChannel() == null) {
                context.sendEmbed("Join VoiceChannel", "This command requires you to be in a voicechannel");
            }

            context.getGuild().getAudioManager().openAudioConnection(context.getMember().getVoiceState().getChannel());
        } else {
            context.sendEmbed("Dev Subcommands", "`->` preload <key>\n`->` block <userId>\n`->` unblock <userId>\n`->` forcedc <guildId>\n`->` forcerc", randomQuote());
        }
    }

    private String randomQuote() {
        return quotes[r.nextInt(quotes.length)];
    }

}
