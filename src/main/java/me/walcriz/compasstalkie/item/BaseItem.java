package me.walcriz.compasstalkie.item;

import it.unimi.dsi.fastutil.Pair;
import me.walcriz.compasstalkie.Main;
import me.walcriz.compasstalkie.item.effects.Glow;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;;import javax.annotation.Nullable;

public abstract class BaseItem {
    protected ItemStack item;

    public BaseItem(ItemStack item) {
        this.item = item;

        ItemManager.getInstance().setData("item_type", PersistentDataType.STRING, getItemUniqueId(), item);
    }

    public void setUpItem(boolean setItemName) {
        ItemMeta meta = item.getItemMeta();

        if (setItemName) {
            meta.displayName(Component.text(ChatColor.RESET + getBaseName()));
        }

        if (isFakeEnchanted()) {
            NamespacedKey key = new NamespacedKey(Main.instance, getItemUniqueId());
            meta.addEnchant(new Glow(key), 1, true);
        }

        item.setType(getMaterial());
    }

    public abstract void onUseItem(PlayerInteractEvent event);

    public abstract String getBaseName();
    public abstract String getItemUniqueId();
    public abstract Material getMaterial();
    public abstract boolean isFakeEnchanted();
}
