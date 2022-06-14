package me.walcriz.compasstalkie.item.items;

import me.walcriz.compasstalkie.CompassTalkiePlugin;
import me.walcriz.compasstalkie.item.BaseItem;
import me.walcriz.compasstalkie.item.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CompassTalkieItem extends BaseItem {

    public CompassTalkieItem(ItemStack item) {
        super(item);
    }

    @Override
    public void onUseItem(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.LODESTONE)
            return;

        Player player = event.getPlayer();
        if (!player.hasPermission(CompassTalkiePlugin.CHANGE_PERMISSION) || !player.hasPermission(CompassTalkiePlugin.TURNON_PERMISSION))
            return;

        ActivatedCompassTalkieItem c = new ActivatedCompassTalkieItem(item, event.getClickedBlock().getLocation());
        c.setIsActive(true);
        event.getPlayer().getInventory().forEach(i -> {
            BaseItem customItem = ItemManager.getInstance().getItem(i);
            if (customItem instanceof ActivatedCompassTalkieItem cCustomItem) {
                cCustomItem.setIsActive(false);
            }
        });

        ItemManager.getInstance().linkItemToNewCustomItem(item, c, false);
    }

    @Override
    public String getBaseName() {
        return "Compass Talkie";
    }

    @Override
    public String getItemUniqueId() {
        return "compass_talkie";
    }

    @Override
    public Material getMaterial() {
        return Material.COMPASS;
    }

    @Override
    public boolean isFakeEnchanted() {
        return false;
    }
}
