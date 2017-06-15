package me.mrCookieSlime.QuestWorld.hooks.builtin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import me.mrCookieSlime.QuestWorld.quests.QuestChecker;
import me.mrCookieSlime.QuestWorld.quests.QuestListener;
import me.mrCookieSlime.QuestWorld.quests.QuestManager;
import me.mrCookieSlime.QuestWorld.utils.PlayerTools;
import me.mrCookieSlime.QuestWorld.quests.Mission;

public class CraftMission extends MissionType implements Listener {
	public CraftMission() {
		super("CRAFT", true, true, false, SubmissionType.ITEM, new MaterialData(Material.WORKBENCH));
	}
	
	@Override
	public ItemStack displayItem(IMission instance) {
		
		return instance.getMissionItem().clone();
	}
	
	@Override
	protected String displayString(IMission instance) {
		return "&7Craft " + instance.getAmount() + "x " + StringUtils.formatItemName(instance.getDisplayItem(), false);
	}

	@EventHandler
	public void onCraft(CraftItemEvent e) {
		ItemStack test = e.getRecipe().getResult().clone();
		ClickType click = e.getClick();
		
		int recipeAmount = test.getAmount();
		
		switch(click) {
		case NUMBER_KEY:
			// If hotbar slot selected is full, crafting fails (vanilla behavior, even when items match)
			if(e.getWhoClicked().getInventory().getItem(e.getHotbarButton()) != null)
				recipeAmount = 0;
			break;
			
		case DROP:
		case CONTROL_DROP:
			// If we are holding items, craft-via-drop fails (vanilla behavior)
			ItemStack cursor = e.getCursor();
			// Apparently, rather than null, an empty cursor is AIR. I don't think that's intended.
			if(cursor != null && cursor.getType() != Material.AIR)
				recipeAmount = 0;
			break;
			
		case SHIFT_RIGHT:
		case SHIFT_LEFT:
			int maxCraftable = PlayerTools.getMaxCraftAmount(e.getInventory());
			int capacity = PlayerTools.fits(test, e.getView().getBottomInventory());
			
			// If we can't fit everything, increase "space" to include the items dropped by crafting
			// (Think: Uncrafting 8 iron blocks into 1 slot)
			if(capacity < maxCraftable)
				maxCraftable = ((capacity + recipeAmount - 1) / recipeAmount) * recipeAmount;
			
			recipeAmount = maxCraftable;
			break;
		default:
		}
		
		// No use continuing if we haven't actually crafted a thing
		if(recipeAmount == 0)
			return;
		
		test.setAmount(recipeAmount);
		
		QuestChecker.check((Player) e.getWhoClicked(), e, "CRAFT", new QuestListener() {
			
			@Override
			public void onProgressCheck(Player p, QuestManager manager, Mission task, Object event) {
				if (QuestWorld.getInstance().isItemSimiliar(test, task.getMissionItem())) manager.addProgress(task, e.getCurrentItem().getAmount());
			}
		});
	}
}