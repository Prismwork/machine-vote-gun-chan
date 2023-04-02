package io.github.prismwork.a_or_b.client.entity;

import io.github.prismwork.a_or_b.entity.ThrownProposalEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ThrownProposalEntityRenderer extends ArrowRenderer<ThrownProposalEntity> {
    public ThrownProposalEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ThrownProposalEntity entity) {
        return new ResourceLocation("machine_vote_gun_chan", "textures/entity/projectiles/proposal.png");
    }
}
