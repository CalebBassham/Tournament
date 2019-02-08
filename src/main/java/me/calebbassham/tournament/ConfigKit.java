package me.calebbassham.tournament;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

class ConfigKit implements Kit {

    private final Map<Integer, ItemStack> items;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;

    private ConfigKit(Map<Integer, ItemStack> items, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.items = items;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    @Override
    public void equipPlayer(Player player) {
        PlayerInventory inv = player.getInventory();

        for (int slot : items.keySet()) {
            inv.setItem(slot, items.get(slot));
        }

        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
    }

    public static ConfigKit from(ConfigurationSection config) {
        ConfigurationSection invConfig = config.getConfigurationSection("inventory");
        Map<Integer, ItemStack> items = new HashMap<>();
        if (invConfig != null) items = parseInventoryItems(invConfig);

        ItemStack helmet = null;
        ConfigurationSection helmetSection = config.getConfigurationSection("armor.helmet");
        if (helmetSection != null) helmet = parseItemStack(helmetSection);

        ItemStack chestplate = null;
        ConfigurationSection chestplateSection = config.getConfigurationSection("armor.chestplate");
        if (helmetSection != null) chestplate = parseItemStack(chestplateSection);

        ItemStack leggings = null;
        ConfigurationSection legginsSection = config.getConfigurationSection("armor.leggings");
        if (helmetSection != null) leggings = parseItemStack(legginsSection);

        ItemStack boots = null;
        ConfigurationSection bootsSection = config.getConfigurationSection("armor.boots");
        if (helmetSection != null) boots = parseItemStack(bootsSection);

        return new ConfigKit(items, helmet, chestplate, leggings, boots);
    }

    private static Map<Integer, ItemStack> parseInventoryItems(ConfigurationSection config) {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ConfigurationSection c = config.getConfigurationSection(String.valueOf(i));
            if (c == null) continue;
            ItemStack item = parseItemStack(c);
            if (item == null) continue;
            items.put(i, item);
        }
        return items;
    }

    private static ItemStack parseItemStack(ConfigurationSection config) {
        String materialName = config.getString("material").toUpperCase();

        Material material;
        try {
            material = Material.valueOf(config.getString("material").toUpperCase());
        } catch (IllegalArgumentException e) {
            TournamentPlugin.instance.getLogger().info(String.format("%s is not a valid material.", materialName));
            return null;
        } catch (NullPointerException e) {
            TournamentPlugin.instance.getLogger().info("No material provided for kit item.");
            return null;
        }

        ItemStack item = new ItemStack(material);

        ConfigurationSection enchSection = config.getConfigurationSection("enchantments");
        if (enchSection != null) item.addEnchantments(parseEnchantments(enchSection));

        return item;
    }

    private static Map<Enchantment, Integer> parseEnchantments(ConfigurationSection config) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();

        for (String key : config.getKeys(false)) {
            int level = config.getInt(key);
            Enchantment enchantment = Enchantment.getByName(key);
            if (enchantment == null) {
                TournamentPlugin.instance.getLogger().info(String.format("%s is not an enchantment.", key));
                continue;
            }
            enchantments.put(enchantment, level);
        }

        return enchantments;
    }

}
