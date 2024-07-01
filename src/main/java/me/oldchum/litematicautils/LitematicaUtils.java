package me.oldchum.litematicautils;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.config.Hotkeys;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/THCFree/litematica-printer/releases/latest
 *
 * @author Old Chum
 * @since June 29, 2024
 */
// TODO: Rotations, raycasting etc.
public class LitematicaUtils implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("litematica-utils");

	public static final ConfigBoolean NUKER_ENABLED = new ConfigBoolean("nukerEnabled", false, "Destroys blocks that are incorrectly placed in your schematic.");
	public static final ConfigHotkey TOGGLE_NUKER = new ConfigHotkey("nukerToggle", "", KeybindSettings.PRESS_ALLOWEXTRA_EMPTY, "Toggles nuker when pressed.");
	// Nuker range
	// Nuker flatten
	// Nuker flatten hotkey
	// TODO: Nuker modes
	// TODO: Nuker render
	// Nuker filter
	// Nuke meta
	// Nuker mine delta option
	// Nuker mode option from Mapmatica

	@Override
	public void onInitialize() {
		TOGGLE_NUKER.getKeybind().setCallback(new KeyCallbackToggleBooleanConfigWithMessage(NUKER_ENABLED));
	}

	public static ImmutableList<IConfigBase> getConfigList () {
		List<IConfigBase> ret = new ArrayList<>(Configs.Generic.OPTIONS);

		ret.add(NUKER_ENABLED);

		return ImmutableList.copyOf(ret);
	}

	public static List<ConfigHotkey> getHotkeyList () {
		List<ConfigHotkey> ret = new ArrayList<>(Hotkeys.HOTKEY_LIST);

		ret.add(TOGGLE_NUKER);

		return ret;
	}
}