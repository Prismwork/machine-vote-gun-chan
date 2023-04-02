package io.github.prismwork.a_or_b.entity;

import com.mojang.serialization.Dynamic;
import io.github.prismwork.a_or_b.MachineVoteGunChan;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundVoteFinishPacket;
import net.minecraft.voting.votes.ServerVote;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ThrownProposalEntity extends AbstractArrow {
    private @Nullable ServerVote vote;
    private @Nullable UUID voteId;
    private @Nullable LivingEntity source;

    public ThrownProposalEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        vote = null;
        voteId = null;
        source = null;
    }

    @SuppressWarnings("unused")
    public ThrownProposalEntity(Level level, @Nullable ServerVote vote, @Nullable UUID voteId, @Nullable LivingEntity source) {
        this(MachineVoteGunChan.THROWN_PROPOSAL, level);
        this.vote = vote;
        this.voteId = voteId;
        this.source = source;
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (!(entity == source)) {
            if (vote != null) {
                if (entity.getServer() != null) {
                    for (ServerVote.Option option : vote.options().values()) {
                        for (ServerVote.Effect effect : option.changes()) {
                            effect.apply(entity.getServer());
                        }
                    }
                    entity.getServer().getPlayerList().broadcastAll(new ClientboundVoteFinishPacket(voteId));
                    discard();
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.level.explode(this, getX(), getY(), getZ(), 4.0f, Level.ExplosionInteraction.BLOCK);
        this.kill();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (vote != null) {
            ServerVote.CODEC.encodeStart(NbtOps.INSTANCE, vote).result().ifPresent(tag -> nbt.put("Vote", nbt));
        }
        if (voteId != null) {
            nbt.putUUID("VoteId", voteId);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Vote")) {
            Tag tag = nbt.get("Vote");
            AtomicReference<ServerVote> ret = new AtomicReference<>();
            ServerVote.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag)).result().ifPresent(ret::set);
            if (ret.get() != null) vote = ret.get();
        }
        if (nbt.contains("VoteId")) voteId = nbt.getUUID("VoteId");
    }
}
