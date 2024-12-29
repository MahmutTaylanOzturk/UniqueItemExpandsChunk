package me.taylan;


import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class UniqueItemExpandsChunk extends JavaPlugin implements Listener {

    private Map<UUID, HashSet<Material>> playerDiscoveredItems;

    @Override
    public void onEnable() {
        this.playerDiscoveredItems = new HashMap<>();
        getServer().getPluginManager().registerEvents(this, this);
        send("<gray>-----------------------------");
        send("<green>UniqueItemExpandsChunk Enabled!");
        send("<gold>Author: <gray>taylan");
        send("<gray>-----------------------------");
    }

    @Override
    public void onDisable() {
        send("<gray>-----------------------------");
        send("<red>UniqueItemExpandsChunk Disabled!");
        send("<gold>Author: <gray>taylan");
        send("<gray>-----------------------------");
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        Material item = event.getItem().getItemStack().getType();
        handleItemDiscovery(player, item);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Material craftedItem = event.getRecipe().getResult().getType();
            handleItemDiscovery(player, craftedItem);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());
        if (itemStack != null) {
            Material item = itemStack.getType();
            handleItemDiscovery(player, item);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                Material item = itemStack.getType();
                handleItemDiscovery(player, item);
            }
        }
    }

    private void handleItemDiscovery(Player player, Material item) {
        UUID playerUUID = player.getUniqueId();
        playerDiscoveredItems.putIfAbsent(playerUUID, new HashSet<>());
        HashSet<Material> discoveredItems = playerDiscoveredItems.get(playerUUID);

        if (!discoveredItems.contains(item)) {
            discoveredItems.add(item);
            expandWorldBorder(player.getWorld());
            player.sendMessage("You discovered a new item: " + item.name());
        }
    }

    private void expandWorldBorder(World world) {
        WorldBorder border = world.getWorldBorder();
        double newSize = border.getSize() + 16; // 1 chunk = 16 blocks
        border.setSize(newSize, 5);
        Bukkit.getLogger().info("World Border expanded to: " + newSize + " blocks!");
    }

    public void send(String s) {
        getServer().getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(s));
    }
}
