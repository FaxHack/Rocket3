package org.example;

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
/**
 * Example rusherhack module
 *
 * @author John200410
 */
public class ExampleModule extends ToggleableModule {

	/**
	 * Settings
	 */
	private final BooleanSetting exampleBoolean = new BooleanSetting("Boolean",
			"Settings can optionally have a description", true);

	private final NumberSetting<Double> exampleDouble = new NumberSetting<>("Double", 0.0, -10.0, 10.0)

			// specifies incremental step for precise numbers
			.incremental(0.1)

			// predicate that determines conditions for the setting to be visible in the
			// clickgui
			.setVisibility(this.exampleBoolean::getValue);

	// consumer that is called when the setting is changed
	// .onChange(d -> ChatUtils.print("Changed double to " + d));

	private final ColorSetting exampleColor = new ColorSetting("Color", java.awt.Color.CYAN)

			// set whether alpha is enabled in the color picker
			.setAlphaAllowed(false)

			// sync the color with the theme color
			.setThemeSync(true);

	private final StringSetting exampleString = new StringSetting("String", "Hello World!")

			// disables the rendering of the setting name in the clickgui
			.setNameVisible(false);

	private final BindSetting rotate = new BindSetting("RotateBind", NullKey.INSTANCE /* unbound */);
	private final NumberSetting<Float> rotateYaw = new NumberSetting<>("Yaw", 0f, 0f, 360f).incremental(0.1f);
	private final NumberSetting<Float> rotatePitch = new NumberSetting<>("Pitch", 0f, -90f, 90f).incremental(0.1f);


	/**
	 * Constructor
	 */
	public ExampleModule() {
		super("Rocket3", "Auto-crafts flight duration 3 rockets", ModuleCategory.CLIENT);

		// subsettings
		this.rotate.addSubSettings(this.rotateYaw, this.rotatePitch);

		// register settings
		this.registerSettings(
				this.exampleBoolean,
				this.exampleDouble,
				this.exampleColor,
				this.exampleString,
				this.rotate);
	}

	boolean activated = false;
	@Subscribe
	private void onUpdate(EventUpdate event) {
		if(!activated)
		fillCraftingGridWithDirt();
    }
	Minecraft minecraft = Minecraft.getInstance();
	public void fillCraftingGridWithDirt() {
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

		activated = true;
        NonNullList<Slot> slots = menu.slots;

		Int2ObjectMap<ItemStack> changedSlots = new Int2ObjectOpenHashMap<>();
        // Crafting grid slots are usually at indices 1-9 in the slot list
        for (int i = 1; i <= 9; i++) {
            Slot slot = slots.get(i);

			ItemStack dirtStack = new ItemStack(Items.DIRT);
			int slotIndex = slot.index;
            if (slot.getItem().isEmpty()) {


                changedSlots.put(slot.index, dirtStack);
			}
		
                // Send packet to simulate clicking the slot with a dirt stack
                minecraft.getConnection().send(
                    new ServerboundContainerClickPacket(
						9,
                        menu.containerId,
                        menu.incrementStateId(),
                        slotIndex,
                        ClickType.QUICK_MOVE, // Click type: left click
                        dirtStack,
				changedSlots
                    )
                );
			}
        
    }
	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}
}
