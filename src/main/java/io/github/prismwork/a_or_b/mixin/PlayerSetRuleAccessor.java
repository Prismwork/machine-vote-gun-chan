package io.github.prismwork.a_or_b.mixin;

import net.minecraft.voting.rules.actual.PlayerEntry;
import net.minecraft.voting.rules.actual.PlayerSetRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerSetRule.class)
public interface PlayerSetRuleAccessor {
    @Invoker("add")
    boolean makeDictator(PlayerEntry entry);
}
