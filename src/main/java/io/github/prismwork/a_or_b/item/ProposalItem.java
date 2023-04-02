package io.github.prismwork.a_or_b.item;

import com.mojang.serialization.Dynamic;
import io.github.prismwork.a_or_b.entity.ThrownProposalEntity;
import io.github.prismwork.a_or_b.mixin.ServerVoteStorageAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundVoteFinishPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.voting.rules.Rules;
import net.minecraft.voting.votes.ServerVote;
import net.minecraft.voting.votes.ServerVoteStorage;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ProposalItem extends ArrowItem {
    public ProposalItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public @NotNull AbstractArrow createArrow(Level level, ItemStack itemStack, LivingEntity livingEntity) {
        CompoundTag nbt = itemStack.getTag();
        Optional<ServerVote> vote = Optional.empty();
        UUID voteId = null;
        if (nbt != null) {
            vote = ServerVote.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("ProposalInfo"))).result();
            voteId = nbt.getUUID("ProposalUUID");
        }
        return new ThrownProposalEntity(level, vote.orElse(null), voteId, livingEntity);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        ItemStack stack = player.getItemInHand(interactionHand);
        AtomicReference<InteractionResultHolder<ItemStack>> ret = new AtomicReference<>(InteractionResultHolder.pass(stack));
        if (level.getServer() != null) {
            MinecraftServer server = level.getServer();
            ServerVoteStorage votes = server.getVoteStorage();
            CompoundTag nbt = stack.getTag();
            if (Rules.PRESIDENT.contains(player.getUUID())) {
                if (nbt == null) {
                    CompoundTag newNbt = stack.getOrCreateTag();
                    Map<UUID, ServerVote> voteMap = ((ServerVoteStorageAccessor) votes).pendings();
                    if (voteMap.isEmpty()) return InteractionResultHolder.pass(stack);
                    int randomRuleId = new Random().nextInt(voteMap.size());
                    int i = 0;
                    for (Map.Entry<UUID, ServerVote> vote : voteMap.entrySet()) {
                        if (i == randomRuleId) {
                            writeRuleToNbt(newNbt, vote.getValue(), vote.getKey());
                            server.getPlayerList().broadcastAll(new ClientboundVoteFinishPacket(vote.getKey()));
                            voteMap.remove(vote.getKey());
                            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
                            return InteractionResultHolder.success(stack);
                        }
                        i++;
                    }
                }
            }
            if (nbt != null) {
                if (nbt.contains("ProposalInfo")) {
                    ServerVote.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("ProposalInfo"))).result().ifPresent(vote -> {
                        for (ServerVote.Option option : vote.options().values()) {
                            for (ServerVote.Effect effect : option.changes()) {
                                effect.apply(server);
                            }
                        }
                        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
                        stack.shrink(1);
                        if (new Random().nextInt(1000) >= 980) {
                            level.explode(player, player.getX(), player.getY(), player.getZ(), 8.0f, Level.ExplosionInteraction.BLOCK);
                        }
                        ret.set(InteractionResultHolder.success(stack));
                    });
                }
            }
        }
        return ret.get();
    }

    private static void writeRuleToNbt(final CompoundTag nbt, @NotNull ServerVote vote, UUID id) {
        AtomicReference<Tag> voteNbt = new AtomicReference<>();
        ServerVote.CODEC.encodeStart(NbtOps.INSTANCE, vote).result().ifPresent(voteNbt::set);
        if (voteNbt.get() != null) {
            nbt.putUUID("ProposalUUID", id);
            nbt.put("ProposalInfo", voteNbt.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        if (itemStack.getTag() != null) {
            CompoundTag nbt = itemStack.getTag();
            if (nbt.contains("ProposalInfo")) {
                Optional<ServerVote> vote = ServerVote.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("ProposalInfo"))).result();
                if (vote.isPresent()) {
                    list.add(Component.translatable("item.proposal.tooltip.info")
                                    .withStyle(ChatFormatting.BLUE)
                                    .append(vote.get().header().displayName().copy().withStyle(ChatFormatting.AQUA)));
                    for (ServerVote.Option option : vote.get().options().values()) {
                        for (ServerVote.Effect effect : option.changes()) {
                            list.add(Component.literal("- ").withStyle(ChatFormatting.GRAY)
                                    .append(effect.description().copy().withStyle(ChatFormatting.GRAY)));
                        }
                    }
                } else {
                    appendEmptyTooltip(list);
                }
                return;
            }
        }
        appendEmptyTooltip(list);
    }

    private static void appendEmptyTooltip(List<Component> tooltips) {
        tooltips.add(Component.translatable("item.proposal.tooltip.empty").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
