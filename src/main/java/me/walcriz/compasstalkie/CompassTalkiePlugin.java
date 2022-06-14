package me.walcriz.compasstalkie;

import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import me.walcriz.compasstalkie.frequency.FrequencyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;
import java.util.UUID;

public class CompassTalkiePlugin implements VoicechatPlugin {

    public static Permission USE_PERMISSION = new Permission("voicechat_compass_talkie.use", PermissionDefault.TRUE);
    public static Permission CHANGE_PERMISSION = new Permission("voicechat_compass_talkie.change", PermissionDefault.TRUE);
    public static Permission TURNOFF_PERMISSION = new Permission("voicechat_compass_talkie.turnoff", PermissionDefault.TRUE);
    public static Permission TURNON_PERMISSION = new Permission("voicechat_compass_talkie.turnon", PermissionDefault.TRUE);
    public static Permission BROADCAST_PERMISSION = new Permission("voicechat_compass_talkie.broadcast", PermissionDefault.OP);

    public VoicechatServerApi serverApi;

    /**
     * @return the unique ID for this voice chat plugin
     */
    @Override
    public String getPluginId() {
        return Main.PLUGIN_ID;
    }

    /**
     * Called when the voice chat initializes the plugin.
     *
     * @param api the voice chat API
     */
    @Override
    public void initialize(VoicechatApi api) {

    }

    /**
     * Called once by the voice chat to register all events.
     *
     * @param registration the event registration
     */
    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void onServerStart(VoicechatServerStartedEvent event) {
        this.serverApi = event.getVoicechat();
    }

    private void onMicrophone(MicrophonePacketEvent event) {
        // The connection might be null if the event is caused by other means
        if (event.getSenderConnection() == null) {
            return;
        }
        // Cast the generic player object of the voice chat API to an actual bukkit player
        // This object should always be a bukkit player object on bukkit based servers
        if (!(event.getSenderConnection().getPlayer().getPlayer() instanceof Player player)) {
            return;
        }

        ServerPlayer serverPlayer = event.getSenderConnection().getPlayer();

        // Check if the player has the use permission
        if (!player.hasPermission(USE_PERMISSION)) {
            return;
        }

        Group group = event.getSenderConnection().getGroup();

        // Check if the player sending the audio is actually in a group
        if (group == null) {
            return;
        }

        // Cancel the actual microphone packet event that people in that group or close by don't hear the broadcaster twice
        event.cancel();

        VoicechatServerApi api = event.getVoicechat();

        List<UUID> playersOnFrequency = FrequencyHandler.getInstance().getPlayersOnFrequency(player);

        // Iterating over every player on the server
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            // Don't send the audio to the player that is broadcasting
            if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (!playersOnFrequency.contains(onlinePlayer.getUniqueId()))
                continue;

            VoicechatConnection connection = api.getConnectionOf(onlinePlayer.getUniqueId());
            // Check if the player is actually connected to the voice chat
            if (connection == null) {
                continue;
            }
            Group cGroup = connection.getGroup();
            if (cGroup == null || !cGroup.getName().equals(group.getName()))
                continue;

            Location location = player.getLocation();
            if (api.getPlayersInRange(serverPlayer.getServerLevel(), api.createPosition(location.getX(), location.getY(), location.getZ()),
                    api.getBroadcastRange()).stream().anyMatch(p -> p.getUuid().equals(onlinePlayer.getUniqueId())))
                continue;

            // Send a static audio packet of the microphone data to the connection of each player
            api.sendStaticSoundPacketTo(connection, event.getPacket().toStaticSoundPacket());
        }
    }
}
