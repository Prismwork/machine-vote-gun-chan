package io.github.prismwork.a_or_b.mixin;

import net.minecraft.voting.votes.ServerVote;
import net.minecraft.voting.votes.ServerVoteStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(ServerVoteStorage.class)
public interface ServerVoteStorageAccessor {
    @Accessor("pendingVotes")
    Map<UUID, ServerVote> pendings();
}
