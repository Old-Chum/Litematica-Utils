package me.oldchum.litematicautils.mixin;

import fi.dy.masa.litematica.event.InputHandler;
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
@Mixin(value = InputHandler.class, remap = false)
public class InputHandlerMixin {
    @Redirect(method = "addHotkeys", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Hotkeys;HOTKEY_LIST:Ljava/util/List;"))
    private List<ConfigHotkey> injectHotkeysAtAddHotKeys () {
        return LitematicaUtils.getHotkeyList();
    }

    @Redirect(method = "addKeysToMap", at = @At(value = "FIELD", target = "Lfi/dy/masa/litematica/config/Hotkeys;HOTKEY_LIST:Ljava/util/List;"))
    private List<ConfigHotkey> injectHotkeysAtAddKeysToMap() {
        return LitematicaUtils.getHotkeyList();
    }
}
