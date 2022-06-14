package me.walcriz.compasstalkie.item.items;

import me.walcriz.compasstalkie.CompassTalkiePlugin;
import me.walcriz.compasstalkie.frequency.FrequencyHandler;
import me.walcriz.compasstalkie.item.BaseItem;
import me.walcriz.compasstalkie.item.ItemManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ActivatedCompassTalkieItem extends BaseItem {

    private boolean isActive = false;
    private Location location;

    public ActivatedCompassTalkieItem(ItemStack item) {
        super(item);

        int x = ItemManager.getInstance().fromData("frequency_x", PersistentDataType.INTEGER, item);
        int y = ItemManager.getInstance().fromData("frequency_y", PersistentDataType.INTEGER, item);
        int z = ItemManager.getInstance().fromData("frequency_z", PersistentDataType.INTEGER, item);
        this.location = new Location(Bukkit.getServer().getWorlds().get(0), x, y, z);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Frequency: " + x + "-" + y + "-" + z));
        item.lore(lore);
        ItemManager.getInstance().setData("compasstalkie_active", PersistentDataType.BYTE, (byte) (isActive ? 1 : 0), item);
    }

    public ActivatedCompassTalkieItem(ItemStack item, Location location) {
        super(item);
        this.location = location;

        ItemManager.getInstance().setData("frequency_x", PersistentDataType.INTEGER, this.location.getBlockX(), item);
        ItemManager.getInstance().setData("frequency_y", PersistentDataType.INTEGER, this.location.getBlockY(), item);
        ItemManager.getInstance().setData("frequency_z", PersistentDataType.INTEGER, this.location.getBlockZ(), item);
    }

    public Location getBoundLocation() {
        return location;
    }

    public void setNewLocation(Location location) {
        this.location = location;
        ItemManager.getInstance().setData("frequency_x", PersistentDataType.INTEGER, this.location.getBlockX(), item);
        ItemManager.getInstance().setData("frequency_y", PersistentDataType.INTEGER, this.location.getBlockY(), item);
        ItemManager.getInstance().setData("frequency_z", PersistentDataType.INTEGER, this.location.getBlockZ(), item);
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
        ItemManager.getInstance().setData("compasstalkie_active", PersistentDataType.BYTE, (byte) (isActive ? 1 : 0), item);
    }

    @Override
    public void onUseItem(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()) {
            if (isActive && event.getPlayer().hasPermission(CompassTalkiePlugin.TURNOFF_PERMISSION)) {
                isActive = false;
                FrequencyHandler.getInstance().removePlayerFromFrequency(event.getPlayer());
            } else if (!isActive && event.getPlayer().hasPermission(CompassTalkiePlugin.TURNON_PERMISSION)) {
                isActive = true;
                FrequencyHandler.getInstance().addPlayerToFrequency(location, event.getPlayer());
            }
            event.setCancelled(true);
            return;
        }

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.LODESTONE)
            return;

        Player player = event.getPlayer();
        if (!player.hasPermission(CompassTalkiePlugin.CHANGE_PERMISSION) || !player.hasPermission(CompassTalkiePlugin.TURNON_PERMISSION))
            return;

        ItemManager.getInstance().linkItemToNewCustomItem(item, new ActivatedCompassTalkieItem(item, event.getClickedBlock().getLocation()), false);
        FrequencyHandler.getInstance().removePlayerFromFrequency(player);
        FrequencyHandler.getInstance().addPlayerToFrequency(event.getClickedBlock().getLocation(), event.getPlayer());
        event.setCancelled(true);
    }

    @Override
    public String getBaseName() {
        return "Activated Compass Talkie";
    }

    @Override
    public String getItemUniqueId() {
        return "activated_compass_talkie";
    }

    @Override
    public Material getMaterial() {
        return Material.COMPASS;
    }

    @Override
    public boolean isFakeEnchanted() {
        return isActive;
    }
}
