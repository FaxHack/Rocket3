package org.rocket3;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.client.api.events.network.EventPacket;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.render.font.IFontRenderer;
import org.rusherhack.client.api.setting.BindSetting;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.client.api.utils.WorldUtils;
import org.rusherhack.core.bind.key.NullKey;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;
import org.rusherhack.core.setting.StringSetting;
import org.rusherhack.core.utils.ColorUtils;
import net.minecraft.network.protocol.game.*;
import net.minecraft.client.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.io.Serializable;
import java.util.ArrayList;

import org.rusherhack.core.event.stage.Stage;

import net.minecraft.world.item.Items;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;


import org.rusherhack.client.api.utils.InventoryUtils;
/**
 * Crafts paper and duration 3 rockets
 *
 * @author PK268
 */
public class Rocket3Module extends ToggleableModule {

	/**
	 * Settings
	 */
	public final BooleanSetting countCrafted = new BooleanSetting("Print", "Prints how many fireworks you've crafted in the session", false);
	public final NumberSetting<Integer> delay = new NumberSetting<>("Delay (ms)", 0, 0, 1000)
			.incremental(1);

	/**
	 * Constructor
	 */
	public Rocket3Module() {
		super("Rocket3", "Auto-crafts flight duration 3 rockets", ModuleCategory.CLIENT);

		// register settings
		this.registerSettings(
				this.delay);
	}

	public boolean CheckRecipe(boolean paper,  AbstractContainerMenu containerMenu)
	{
		if(paper)
		{	
			
			for(int inventorySlot = 1; inventorySlot <= 3; inventorySlot++)
			{
				if(containerMenu.getSlot(inventorySlot).hasItem())
				{
					if(!containerMenu.getSlot(inventorySlot).getItem().getDisplayName().getString().equals("[Sugar Cane]"))
					{
						return false;
					}
				}
				else 
				{
					return false;
				}
			}
			for(int inventorySlot = 4; inventorySlot <= 9; inventorySlot++)
			{
				if(containerMenu.getSlot(inventorySlot).hasItem())
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			for(int inventorySlot = 1; inventorySlot <= 3; inventorySlot++)
			{
				if(containerMenu.getSlot(inventorySlot).hasItem())
				{
					if(!containerMenu.getSlot(inventorySlot).getItem().getDisplayName().getString().equals("[Gunpowder]"))
					{
						return false;
					}
				}
				else 
				{
					return false;
				}
			}
			if(!containerMenu.getSlot(4).getItem().getDisplayName().getString().equals("[Paper]"))
			{
				return false;
			}

			for(int inventorySlot = 5; inventorySlot <= 9; inventorySlot++)
			{
				if(containerMenu.getSlot(inventorySlot).hasItem())
				{
					return false;
				}
			}

			return true;
		}
	}
	public boolean CorrectRecipe(boolean paper, AbstractContainerMenu containerMenu)
	{
		if(paper)
		{
			for(int inventorySlot = 4; inventorySlot <= 9; inventorySlot++)
			{
				if(containerMenu.getSlot(inventorySlot).hasItem())
				{
					try
					{
						Thread.sleep(delay.getValue());
						InventoryUtils.clickSlot(inventorySlot, true);
					}
					catch (Exception e){}
				}
			}
			if(!CheckRecipe(paper,containerMenu))
			{
				for(int inventorySlot = 1; inventorySlot <= 9; inventorySlot++)
				{
					if(containerMenu.getSlot(inventorySlot).hasItem())
					{
						try
						{
							Thread.sleep(delay.getValue());
							InventoryUtils.clickSlot(inventorySlot, true);
						}
						catch (Exception e){}
					}
				}
				return false;
			}
			return true;
		}
		else
		{
			for(int inventorySlot = 5; inventorySlot <= 9; inventorySlot++)
			{
				if(containerMenu.getSlot(inventorySlot).hasItem())
				{
					try
					{
						Thread.sleep(delay.getValue());
						InventoryUtils.clickSlot(inventorySlot, true);
						
					}
					catch (Exception e){}
				}
			}
			if(!CheckRecipe(paper,containerMenu))
			{
				for(int inventorySlot = 1; inventorySlot <= 9; inventorySlot++)
				{
					if(containerMenu.getSlot(inventorySlot).hasItem())
					{
						try
						{
							Thread.sleep(delay.getValue());
							InventoryUtils.clickSlot(inventorySlot, true);
						}
						catch (Exception e){}
					}
				}
				return false;
			}
			return true;
		}
	}
	private static final ExecutorService executor = Executors.newFixedThreadPool(2); 

	boolean checking = false;
	boolean activated = false;

	int totalCrafted = 0;

	@Subscribe
	private void onUpdate(EventUpdate event) {
		if(activated)
			rocket3Update();
    }
	Minecraft minecraft = Minecraft.getInstance();
	public void rocket3Update() {
        if (!(minecraft.screen instanceof CraftingScreen)) {
            return; // Not in a crafting table GUI
        }
		
        CraftingScreen craftingScreen = (CraftingScreen) minecraft.screen;
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;

        if (player == null || level == null) {
            return;
        }

        AbstractContainerMenu menu = craftingScreen.getMenu();
        if (!(menu instanceof CraftingMenu)) {
            return;
        }

		AbstractContainerMenu containerMenu = minecraft.player.containerMenu;
		boolean craftingPaper = true;
		boolean craftingFireworks = true;
		for (int inventorySlot = 9; inventorySlot <= 45; inventorySlot++) {
			String slotName = containerMenu.getSlot(inventorySlot).getItem().getDisplayName().getString();
			if(!slotName.equals("[Paper]") && !slotName.equals("[Air]") && !slotName.equals("[Sugar Cane]"))
			{
				craftingPaper = false;
				break;
			}
		}
		if(!craftingPaper)
		{
			for (int inventorySlot = 9; inventorySlot <= 45; inventorySlot++) {
				
			String slotName = containerMenu.getSlot(inventorySlot).getItem().getDisplayName().getString();
				if(!slotName.equals("[Paper]") && !slotName.equals("[Air]") && !slotName.equals("[Firework Rocket]") && !slotName.equals("[Gunpowder]"))
				{
					craftingFireworks = false;
					break;
				}
			}
		}
		else
		{
			craftingFireworks = false;
		}
		
		final boolean craftingOption = craftingPaper;

		if(craftingFireworks)
		{
			executor.submit(() -> {
				for (int i = 0; i < 4; i++) {
					for (int inventorySlot = 9; inventorySlot <= 45; inventorySlot++) {

						if (!(minecraft.screen instanceof CraftingScreen)) {
							return; // Not in a crafting table GUI
						}
						
						for(int j = 1; j <= 9; j++)
						{
							if(minecraft.screen instanceof CraftingScreen && containerMenu.getSlot(j).getItem().getDisplayName().getString().equals("[Firework Rocket]"))
							{
								try
								{
									Thread.sleep(delay.getValue());
								InventoryUtils.clickSlot(j, true);
								}
								catch (Exception e) 
									{
			
									}
							}
						}
						if (!containerMenu.getSlot(inventorySlot).hasItem()) {
							continue;
						}
		
						ItemStack stack = containerMenu.getSlot(inventorySlot).getItem();
		
						if ( i <= 2) //gunpowder
						{
							if(minecraft.screen instanceof CraftingScreen && stack.getCount() == 64 && stack.getDisplayName().getString().equals("[Gunpowder]"))
							{
								try {
									if(!CheckRecipe(craftingOption, containerMenu))
									{
										Thread.sleep(delay.getValue());
										InventoryUtils.clickSlot(inventorySlot, true);
									}
									else
									{
										if(!CorrectRecipe(craftingOption,containerMenu))
											return;
										if(CheckRecipe(craftingOption,containerMenu))
										{
										try{
											
											Thread.sleep(delay.getValue());
											if(containerMenu.getSlot(0).getItem().getTag().toString().equals("{Fireworks:{Flight:3b}}") && CheckRecipe(false,containerMenu))
											{
												
											InventoryUtils.clickSlot(0, true); //Grab the rockets
											totalCrafted += 64*3;
										if(countCrafted.getValue())
											ChatUtils.print("Crafted! Total this session: " + totalCrafted);
											break;
										}
											}
											catch (Exception e) 
											{
				
											}
										}
									}
								}
								catch (Exception e) 
									{
			
									}
								break;
							}
						}
						else //paper
						{
							if(minecraft.screen instanceof CraftingScreen && stack.getCount() == 64 && stack.getDisplayName().getString().equals("[Paper]"))
							{
								if(!CheckRecipe(craftingOption, containerMenu))
								{	
									try {
									Thread.sleep(delay.getValue());
									InventoryUtils.clickSlot(inventorySlot, true);
									}
									catch (Exception e) 
									{}
								}
								else
								{
									if(!CorrectRecipe(craftingOption,containerMenu))
									return;
								if(CheckRecipe(craftingOption,containerMenu))
								{
									try{
										Thread.sleep(delay.getValue());
											if(containerMenu.getSlot(0).getItem().getTag().toString().equals("{Fireworks:{Flight:3b}}") && CheckRecipe(false,containerMenu))
											{
												
										InventoryUtils.clickSlot(0, true); //Grab the rockets
										totalCrafted += 64*3;
										if(countCrafted.getValue())
											ChatUtils.print("Crafted! Total this session: " + totalCrafted);
										break;
										}
									}
									catch (Exception e) 
									{

									}
								}
								}

							if(CheckRecipe(craftingOption,containerMenu))
							{
								try
								{
									
									Thread.sleep(delay.getValue());
											if(containerMenu.getSlot(0).getItem().getTag().toString().equals("{Fireworks:{Flight:3b}}") && CheckRecipe(false,containerMenu))
											{
												
											InventoryUtils.clickSlot(0, true); //Grab the rockets
											totalCrafted += 64*3;
											if(countCrafted.getValue())
												ChatUtils.print("Crafted! Total this session: " + totalCrafted);
												break;
										}
								}
								catch (Exception e){}
								
							}
							else
							{
								if(!CorrectRecipe(craftingOption,containerMenu))
											return;
								if(CheckRecipe(craftingOption,containerMenu))
								{
									try
									{
										
										Thread.sleep(delay.getValue());
											if(containerMenu.getSlot(0).getItem().getTag().toString().equals("{Fireworks:{Flight:3b}}") && CheckRecipe(false,containerMenu))
											{
												
									InventoryUtils.clickSlot(0, true); //Grab the rockets
									totalCrafted += 64*3;
									if(countCrafted.getValue())
										ChatUtils.print("Crafted! Total this session: " + totalCrafted);
										break;
									}
								}
									catch (Exception e){}
								}
							}
							
						return;
						}
						}
					}
				}
				if(containerMenu.getSlot(0).getItem().getTag().toString().equals("{Fireworks:{Flight:3b}}") && CheckRecipe(craftingOption,containerMenu))
			{
				InventoryUtils.clickSlot(0, true); //edge case where last rockets not crafted
				totalCrafted += 64*3;
				if(countCrafted.getValue())
										ChatUtils.print("Crafted! Total this session: " + totalCrafted);
				
			}
			});
			
		}
		else if(craftingPaper)
		{
			executor.submit(() -> {
				for (int i = 0; i < 4; i++) {
					for (int inventorySlot = 9; inventorySlot <= 45; inventorySlot++) {
						for(int j = 1; j <= 9; j++){
							if(minecraft.screen instanceof CraftingScreen && containerMenu.getSlot(j).getItem().getDisplayName().getString().equals("[Paper]"))
							{
								try
								{
									Thread.sleep(delay.getValue());
									InventoryUtils.clickSlot(j, true);
								}
								catch (Exception e) 
								{
		
								}
							}
						}
						if (!containerMenu.getSlot(inventorySlot).hasItem()) {
							continue;
						}
	
						ItemStack stack = containerMenu.getSlot(inventorySlot).getItem();
						if(minecraft.screen instanceof CraftingScreen )
						{
						if ( i <= 2)
						{
							if(stack.getCount() == 64 && stack.getDisplayName().getString().equals("[Sugar Cane]"))
							{
								try {
									if(!CheckRecipe(craftingOption, containerMenu))
									{
									Thread.sleep(delay.getValue());
									InventoryUtils.clickSlot(inventorySlot, true);
									}
									else
									{
										if(!CorrectRecipe(craftingOption,containerMenu))
											return;
										if(CheckRecipe(craftingOption,containerMenu))
										{
										try{
											Thread.sleep(delay.getValue());
											if(containerMenu.getSlot(0).getItem().getDisplayName().getString().equals("[Paper]") && CheckRecipe(craftingOption,containerMenu))
											{
											InventoryUtils.clickSlot(0, true); //Grab the paper
											break;
											}
											}
											catch (Exception e) 
											{
				
											}
										}
										}
									}
									catch (Exception e) 
									{
		
									}
									break;
								}
							}
							else
							{
								try{
									if(CheckRecipe(craftingOption,containerMenu))
									{
										Thread.sleep(delay.getValue());
										if(containerMenu.getSlot(0).getItem().getDisplayName().getString().equals("[Paper]") && CheckRecipe(craftingOption,containerMenu))
										{
											InventoryUtils.clickSlot(0, true); //Grab the paper
										}
									}
									else
									{
										if(!CorrectRecipe(craftingOption,containerMenu))
											return;
										if(CheckRecipe(craftingOption,containerMenu))
										{
											Thread.sleep(delay.getValue());
											if(containerMenu.getSlot(0).getItem().getDisplayName().getString().equals("[Paper]") && CheckRecipe(craftingOption,containerMenu))
											{
												InventoryUtils.clickSlot(0, true); //Grab the paper
											}
										}
									}
								}
								catch (Exception e) {}
								return;
							}
						}
					}
				
			}
			});
		}
    }
	@Override
	public void onEnable() {
		activated = true;
	}

	@Override
	public void onDisable() {
		activated = false;
	}
}
