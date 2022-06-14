package me.walcriz.compasstalkie.events;

import me.walcriz.compasstalkie.item.BaseItem;
import me.walcriz.compasstalkie.item.ItemManager;
import me.walcriz.compasstalkie.item.items.CompassTalkieItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class PlayerListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR))
            return;

        ItemStack itemInHand = event.getItem();

        BaseItem customItem = ItemManager.getInstance().getItem(itemInHand);
        if (customItem == null)
            return;

        customItem.onUseItem(event);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getRecipe() instanceof ShapedRecipe recipe))
            return;

        switch (recipe.getKey().getNamespace()) {
            case "compasstalkie":
                ItemStack result = event.getRecipe().getResult();
                ItemManager.getInstance().registerItem(result, new CompassTalkieItem(result));
                break;
        }
    }
}
