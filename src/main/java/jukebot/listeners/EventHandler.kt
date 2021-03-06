package jukebot.listeners

import jukebot.Database
import jukebot.JukeBot
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter

class EventHandler : EventListener {
    override fun onEvent(event: GenericEvent) {
        when (event) {
            is GuildLeaveEvent -> onGuildLeave(event)
            is GuildVoiceLeaveEvent -> onGuildVoiceLeave(event)
        }
    }

    private fun onGuildLeave(event: GuildLeaveEvent) {
        JukeBot.removePlayer(event.guild.idLong)
    }

    private fun onGuildVoiceLeave(e: GuildVoiceLeaveEvent) {
        if (!e.member.user.isBot) {
            handleLeave(e.channelLeft)
        }
    }

    private fun handleLeave(channel: VoiceChannel) {
        if (!JukeBot.hasPlayer(channel.guild.idLong)) {
            return
        }

        val audioManager = channel.guild.audioManager

        if (!audioManager.isConnected) {
            return
        }

        val connectedChannel = audioManager.connectedChannel ?: return
        val isAlone = connectedChannel.members.none { !it.user.isBot }

        if (isAlone) {
            if (!Database.getIsPremiumServer(channel.guild.idLong) || !Database.getIsAutoDcDisabled(channel.guild.idLong)) {
                JukeBot.removePlayer(channel.guild.idLong)
                audioManager.closeAudioConnection()
            }
        }
    }
}
