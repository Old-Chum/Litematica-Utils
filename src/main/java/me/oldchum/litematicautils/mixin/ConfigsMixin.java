package me.oldchum.litematicautils.mixin;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import me.oldchum.litematicautils.LitematicaUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * https://github.com/THCFree/litematica-printer/releases/latest
 *
 * @since June 30, 2024
 */
@Mixin(value = Configs.class, remap = false)
public class ConfigsMixin {
    @Redirect(method = "loadFromFile", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Configs$Generic;OPTIONS:Lcom/google/common/collect/ImmutableList;"))
    private static ImmutableList<IConfigBase> injectOptionsAtLoad() {
        return LitematicaUtils.getConfigList();
    }

    @Redirect(method = "saveToFile", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Configs$Generic;OPTIONS:Lcom/google/common/collect/ImmutableList;"))
    private static ImmutableList<IConfigBase> injectOptionsAtSave() {
        return LitematicaUtils.getConfigList();
    }

    @Redirect(method = "loadFromFile", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Hotkeys;HOTKEY_LIST:Ljava/util/List;"))
    private static List<ConfigHotkey> injectHotkeysAtSave() {
        return LitematicaUtils.getHotkeyList();
    }

    @Redirect(method = "saveToFile", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Hotkeys;HOTKEY_LIST:Ljava/util/List;"))
    private static List<ConfigHotkey> injectHotkeysAtLoad() {
        return LitematicaUtils.getHotkeyList();
    }
}