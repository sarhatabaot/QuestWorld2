package com.questworld.adapter;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import com.questworld.api.QuestWorld;
import com.questworld.util.VersionAdapter;
import com.questworld.util.json.JsonBlob;

public class CurrentAdapter extends VersionAdapter {
	@Override
	protected String forVersion() {
		return "v1_12_r1";
	}

	@Override
	public void makeSpawnEgg(ItemStack result, EntityType mob) {
		if (result.getItemMeta() instanceof SpawnEggMeta) {
			SpawnEggMeta meta = (SpawnEggMeta) result.getItemMeta();
			meta.setSpawnedType(mob);
			result.setItemMeta(meta);
		}
	}

	@Override
	public void makePlayerHead(ItemStack result, OfflinePlayer player) {
		if (result.getItemMeta() instanceof SkullMeta) {
			SkullMeta meta = (SkullMeta) result.getItemMeta();
			meta.setOwningPlayer(player);
			result.setItemMeta(meta);
		}
	}

	@Override
	public ShapelessRecipe shapelessRecipe(String recipeName, ItemStack output) {
		return new ShapelessRecipe(new NamespacedKey(QuestWorld.getPlugin(), recipeName), output);
	}

	@Override
	public void sendActionbar(Player player, String message) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				"minecraft:title " + player.getName() + " actionbar " + JsonBlob.fromLegacy(message).toString());
	}
}