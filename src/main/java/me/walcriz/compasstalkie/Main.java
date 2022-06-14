package me.walcriz.compasstalkie;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import me.walcriz.compasstalkie.events.PlayerListener;
import me.walcriz.compasstalkie.item.effects.Glow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public final class Main extends JavaPlugin {

    public static Main instance;

    public static final String PLUGIN_ID = "example_plugin";
    public static final Logger LOGGER = LogManager.getLogger(PLUGIN_ID);


    public CompassTalkiePlugin voicechatPlugin;

    @Override
    public void onEnable() {
        instance = this;
        registerGlow();
        registerRecipes();
        registerListeners();

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            voicechatPlugin = new CompassTalkiePlugin();
            service.registerPlugin(voicechatPlugin);
            LOGGER.info("Successfully registered compasstalkie plugin");
        } else {
            LOGGER.info("Failed to register compasstalkie plugin");
        }
    }

    @Override
    public void onDisable() {
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
            LOGGER.info("Successfully unregistered compasstalkie plugin");
        }
    }

    public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NamespacedKey key = new NamespacedKey(this, getDescription().getName());

            Glow glow = new Glow(key);
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException e){
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void registerRecipes() {
        ItemStack item = new ItemStack(Material.COMPASS);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "compasstalkie"), item);
        recipe.shape("  A", "SRS", "III");
        recipe.setIngredient('A', Material.IRON_BARS);
        recipe.setIngredient('S', Material.OAK_SAPLING);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        recipe.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(recipe);
    }

    public void registerListeners() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListener(), this);
    }
}
