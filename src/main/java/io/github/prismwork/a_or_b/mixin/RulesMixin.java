package io.github.prismwork.a_or_b.mixin;

import io.github.prismwork.a_or_b.MachineVoteGunChan;
import net.minecraft.voting.rules.Rule;
import net.minecraft.voting.rules.Rules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Rules.class)
public abstract class RulesMixin {
    @Inject(
            method = "register",
            at = @At("HEAD")
    )
    private static <R extends Rule> void a_or_b$registerRulesButWhy(String string, int i, R rule, CallbackInfoReturnable<R> cir) {
        if (!MachineVoteGunChan.RULES_REGISTERED) {
            MachineVoteGunChan.registerRules();
            MachineVoteGunChan.RULES_REGISTERED = true;
        }
    }
}
