package coffee.axle.sessionauth.mixin;

import coffee.axle.sessionauth.CoffeeAuth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "getUser", at = @At("HEAD"), cancellable = true)
    private void onGetUser(CallbackInfoReturnable<User> cir) {
        if (!CoffeeAuth.overrideSession)
            return;
        cir.setReturnValue(CoffeeAuth.currentUser);
    }

    @Inject(method = "getProfileKeyPairManager", at = @At("HEAD"), cancellable = true)
    private void onGetProfileKeyPairManager(CallbackInfoReturnable<ProfileKeyPairManager> cir) {
        if (!CoffeeAuth.overrideSession)
            return;
        if (CoffeeAuth.isSessionModified()) {
            cir.setReturnValue(ProfileKeyPairManager.EMPTY_KEY_MANAGER);
        }
    }

    @Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
    private void onCreateTitle(CallbackInfoReturnable<String> cir) {
        if (!CoffeeAuth.overrideSession)
            return;
        String username = CoffeeAuth.currentUser.getName();
        cir.setReturnValue("CoffeeAuth v" + CoffeeAuth.MOD_VERSION + " | " + username);
    }
}
