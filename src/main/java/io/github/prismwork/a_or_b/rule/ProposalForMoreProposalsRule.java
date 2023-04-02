package io.github.prismwork.a_or_b.rule;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundVoteFinishPacket;
import net.minecraft.network.protocol.game.ClientboundVoteStartPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.voting.rules.OneShotRule;
import net.minecraft.voting.rules.Rule;
import net.minecraft.voting.rules.RuleChange;
import net.minecraft.voting.votes.ServerVote;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ProposalForMoreProposalsRule extends OneShotRule.Simple {
    private final Codec<Change> codec;

    public ProposalForMoreProposalsRule() {
        this.codec = Codec.INT.xmap(Change::new, change -> change.count);
    }

    @Override
    protected @NotNull Optional<RuleChange> randomApprovableChange(
            MinecraftServer minecraftServer,
            RandomSource randomSource
    ) {
        return Optional.of(new Change(randomSource.nextInt(5, 26)));
    }

    @Override
    public @NotNull Codec<RuleChange> codec() {
        return Rule.puntCodec(codec);
    }

    public class Change extends OneShotRuleChange {
        private final int count;

        public Change(int count) {
            this.count = count;
        }

        @Override
        public @NotNull Rule rule() {
            return ProposalForMoreProposalsRule.this;
        }

        @Override
        protected @NotNull Component description() {
            return Component.translatable("rule.machine_vote_gun_chan.proposal_for_more_proposals", count);
        }

        @Override
        public void run(MinecraftServer server) {
            for (int i = 0; i < count; i++) {
                UUID uuid = UUID.randomUUID();
                RandomSource random = RandomSource.create();
                Optional<ServerVote> vote;
                if (random.nextBoolean()) {
                    vote = ServerVote.createRandomRepealVote(
                            uuid,
                            getAllRules(),
                            server,
                            ServerVote.VoteGenerationOptions.createFromRules(random)
                    );
                } else {
                    vote = ServerVote.createRandomApproveVote(
                            uuid,
                            getAllRules(),
                            server,
                            ServerVote.VoteGenerationOptions.createFromRules(random)
                    );
                }
                vote.ifPresent(serverVote -> {
                    server.startVote(uuid, serverVote);
                });
            }
        }

        private static Set<Rule> getAllRules() {
            Set<Rule> ret = new HashSet<>();
            for (Rule rule : BuiltInRegistries.RULE) {
                ret.add(rule);
            }
            return ret;
        }
    }
}
