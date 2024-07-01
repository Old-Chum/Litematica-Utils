package me.oldchum.litematicautils.mixin;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.litematica.gui.GuiConfigs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import me.oldchum.litematicautils.LitematicaUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * @author Old Chum
 * @since June 30, 2024
 */
@Mixin(value = GuiConfigs.class, remap = false)
public class GuiConfigsMixin {
    @Redirect(method = "getConfigs", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Configs$Generic;OPTIONS:Lcom/google/common/collect/ImmutableList;"))
    private ImmutableList<IConfigBase> injectConfigsAtGetConfigs () {
        return LitematicaUtils.getConfigList();
    }

    @Redirect(method = "getConfigs", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Hotkeys;HOTKEY_LIST:Ljava/util/List;"))
    private List<ConfigHotkey> injectHotkeysAtGetConfigs () {
        return LitematicaUtils.getHotkeyList();
    }
}
