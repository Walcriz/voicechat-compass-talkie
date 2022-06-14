package me.walcriz.compasstalkie.frequency;

import de.maxhenkel.voicechat.api.Group;
import org.bukkit.Location;

import java.util.UUID;

public class FrequencyGroup implements Group {

    private UUID id;
    private String name;

    public FrequencyGroup(Location pos) {
        String frequency =
                pos.getBlockX() +
                "-" +
                pos.getBlockY() +
                "-" +
                pos.getBlockZ();

        this.id = UUID.nameUUIDFromBytes(frequency.getBytes());
        this.name = frequency;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasPassword() {
        return false;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
