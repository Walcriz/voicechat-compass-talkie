package me.walcriz.compasstalkie.item;

import me.walcriz.compasstalkie.item.items.ActivatedCompassTalkieItem;
import me.walcriz.compasstalkie.item.items.CompassTalkieItem;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public enum CustomItemTypes {
    CompassTalkie("compass_talkie", CompassTalkieItem.class),
    ActivatedCompassTalkie("activated_compass_talkie", ActivatedCompassTalkieItem.class);

    final String itemUniqueId;
    final Class<? extends BaseItem> clazz;

    CustomItemTypes(String itemUniqueId, Class<? extends BaseItem> clazz) {
        this.itemUniqueId = itemUniqueId;
        this.clazz = clazz;
    }

    public String getItemUniqueId() { return itemUniqueId; }
    public Class<? extends BaseItem> getClazz() { return clazz; }
}
