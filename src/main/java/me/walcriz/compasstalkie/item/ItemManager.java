package me.walcriz.compasstalkie.item;

import me.walcriz.compasstalkie.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class ItemManager {
    private static ItemManager instance = new ItemManager();
    public static ItemManager getInstance() { return instance; }

    private Map<ItemStack, BaseItem> customItems = new HashMap<>();

    public <T> void setData(String key, PersistentDataType<?, T> type, T data, ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        container.set(new NamespacedKey(Main.instance, key), type, data);
    }

    public <T> T fromData(String key, PersistentDataType<?, T> type, ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(new NamespacedKey(Main.instance, key), type);
    }

    public void registerItem(ItemStack item, BaseItem customItem) {
        customItems.put(item, customItem);
        customItem.setUpItem(true);
    }

    public BaseItem getItem(ItemStack item) {
        if (customItems.get(item) == null) {
            String itemType = fromData("item_type", PersistentDataType.STRING, item);
            for (CustomItemTypes type : CustomItemTypes.values()) {
                if (type.itemUniqueId.equals(itemType)) {
                    try {
                        registerItem(item, type.clazz.getConstructor(ItemStack.class).newInstance(item));
                    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {}
                }
            }
        }

        return customItems.get(item);
    }

    public void linkItemToNewCustomItem(ItemStack item, BaseItem newCustomItem, boolean setName) {
        if (customItems.get(item) != null) { customItems.remove(item); }
        customItems.put(item, newCustomItem);
        newCustomItem.setUpItem(setName);
    }

    public ItemManager() {

    }
}
