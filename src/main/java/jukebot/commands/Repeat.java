package jukebot.commands;

import jukebot.audio.AudioHandler;
import jukebot.utils.Command;
import jukebot.utils.CommandProperties;
import jukebot.utils.Context;
import jukebot.utils.Permissions;

@CommandProperties(description = "Loop the queue, track or nothing", category = CommandProperties.category.CONTROLS)
public class Repeat implements Command {

    private final Permissions permissions = new Permissions();

    public void execute(final Context context) {

        final AudioHandler player = context.getAudioPlayer();

        if (!player.isPlaying()) {
            context.embed("Not Playing", "Nothing is currently playing.");
            return;
        }

        if (!permissions.ensureMutualVoiceChannel(context.getMember())) {
            context.embed("No Mutual VoiceChannel", "Join my VoiceChannel to use this command.");
            return;
        }

        if (!context.isDJ(true)) {
            context.embed("Not a DJ", "You need to be a DJ to use this command.\n[See here on how to become a DJ](https://jukebot.serux.pro/faq)");
            return;
        }

        final String[] args = context.getArgs();

        switch (args[0].toLowerCase()) {
            case "a":
            case "all":
                player.setRepeat(AudioHandler.repeatMode.ALL);
                break;
            case "s":
            case "single":
                player.setRepeat(AudioHandler.repeatMode.SINGLE);
                break;
            case "n":
            case "none":
                player.setRepeat(AudioHandler.repeatMode.NONE);
                break;
            default:
                context.embed("Player Repeat", "Current mode: " + player.getRepeatMode() + "\nAvailable modes: `s`ingle, `a`ll, `n`one");
                return;
        }

        context.embed("Player Repeat", "Set repeat to **" + player.getRepeatMode() + "**");

    }
}
