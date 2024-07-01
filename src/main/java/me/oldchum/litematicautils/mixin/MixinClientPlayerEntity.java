package me.oldchum.litematicautils.mixin;

import me.oldchum.litematicautils.Nuker;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Old Chum
 * @since June 30, 2024
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo callback) {
        Nuker.tickNuker();
    }
}
