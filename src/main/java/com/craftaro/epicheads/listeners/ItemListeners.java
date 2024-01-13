package com.craftaro.epicheads.listeners;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.epicheads.EpicHeads;
import com.craftaro.epicheads.head.Head;
import com.craftaro.epicheads.utils.ItemEconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Optional;

public class ItemListeners implements Listener {
    private final EpicHeads plugin;

    public ItemListeners(EpicHeads plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void itemSpawnEvent(ItemSpawnEvent event) {
        ItemStack item = event.getEntity().getItemStack();
        if (!XMaterial.PLAYER_HEAD.isSimilar(item)
                || item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
            return;
        }

        String encodededStr = ItemUtils.getSkullTexture(item);

        if (encodededStr == null) {
            return;
        }

        String url;
        try {
            url = ItemUtils.getDecodedTexture(encodededStr);
        } catch (Exception ignored) {
            return; // If reached, the head was generated by another incompatible plugin.
        }

        if (url == null) {
            return;
        }
        Optional<Head> optional = this.plugin.getHeadManager()
                .getHeads()
                .stream()
                .filter(head -> url.equals(head.getUrl()))
                .findFirst();

        if (optional.isPresent()) {
            ItemStack itemNew = optional.get().asItemStack();

            ItemMeta meta = itemNew.getItemMeta();
            meta.setLore(new ArrayList<>());
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (ItemEconomy.isItem(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }
}
