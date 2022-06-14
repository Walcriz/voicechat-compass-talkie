package me.walcriz.compasstalkie.frequency;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import me.walcriz.compasstalkie.CompassTalkiePlugin;
import me.walcriz.compasstalkie.Main;
import me.walcriz.compasstalkie.item.items.CompassTalkieItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public final class FrequencyHandler {
    private static final FrequencyHandler instance = new FrequencyHandler();
    public static FrequencyHandler getInstance() { return instance; }


    private Map<UUID, UUID> playerFrequencies = new HashMap<>();
    private Map<UUID, FrequencyGroup> frequencies = new HashMap<>();

    public List<UUID> getPlayersOnFrequency(Player player) {
        UUID frequency = playerFrequencies.get(player.getUniqueId());
        List<UUID> players = new ArrayList<>();

        playerFrequencies.forEach((pu, fu) -> { if (fu.equals(frequency)) players.add(pu); } );
        return players;
    }

    public void addPlayerToFrequency(Location location, Player player) {
        String frequency =
                location.getBlockX() +
                        "-" +
                        location.getBlockY() +
                        "-" +
                        location.getBlockZ();

        UUID frequencyId = UUID.nameUUIDFromBytes(frequency.getBytes());

        playerFrequencies.put(player.getUniqueId(), frequencyId);

        VoicechatServerApi vc = Main.instance.voicechatPlugin.serverApi;

        Group connectGroup;
        if (frequencies.get(frequencyId) == null) {
            FrequencyGroup group = new FrequencyGroup(location);
            frequencies.put(frequencyId, group);
        }
        // TODO: Add password support
        connectGroup = vc.createGroup(frequencyId.toString(), null);

        VoicechatConnection connection = vc.getConnectionOf(player.getUniqueId());
        if (connection == null)
            return;

        connection.setGroup(connectGroup);
    }

    public void removePlayerFromFrequency(Player player) {
        playerFrequencies.remove(player.getUniqueId());

        VoicechatServerApi vc = Main.instance.voicechatPlugin.serverApi;
        VoicechatConnection connection = vc.getConnectionOf(player.getUniqueId());
        if (connection == null)
            return;

        connection.setGroup(null);
    }
}
