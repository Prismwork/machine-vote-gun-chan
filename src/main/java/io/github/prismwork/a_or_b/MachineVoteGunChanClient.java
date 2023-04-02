package io.github.prismwork.a_or_b;

import io.github.prismwork.a_or_b.client.entity.ThrownProposalEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MachineVoteGunChanClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(MachineVoteGunChan.THROWN_PROPOSAL, ThrownProposalEntityRenderer::new);
    }
}
