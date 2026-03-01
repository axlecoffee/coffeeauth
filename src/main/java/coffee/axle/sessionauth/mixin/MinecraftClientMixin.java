package coffee.axle.sessionauth.mixin;

import coffee.axle.sessionauth.CoffeeAuth;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "getSession", at = @At("HEAD"), cancellable = true)
    private void onGetSession(CallbackInfoReturnable<Session> cir) {
        if (!CoffeeAuth.overrideSession)
            return;
        cir.setReturnValue(CoffeeAuth.currentSession);
    }

    @Inject(method = "getProfileKeys", at = @At("HEAD"), cancellable = true)
    private void onGetProfileKeys(CallbackInfoReturnable<ProfileKeys> cir) {
        if (!CoffeeAuth.overrideSession)
            return;
        if (CoffeeAuth.isSessionModified()) {
            cir.setReturnValue(ProfileKeys.MISSING);
        }
    }
}
