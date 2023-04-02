package io.github.prismwork.a_or_b;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.prismwork.a_or_b.entity.ThrownProposalEntity;
import io.github.prismwork.a_or_b.item.ProposalItem;
import io.github.prismwork.a_or_b.mixin.PlayerSetRuleAccessor;
import io.github.prismwork.a_or_b.rule.ProposalForMoreProposalsRule;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.voting.rules.Rules;
import net.minecraft.voting.rules.actual.PlayerEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MachineVoteGunChan implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("i fucked all these up mwah mwah mwah");
	public static final Item PROPOSAL = new ProposalItem();
	public static final EntityType<ThrownProposalEntity> THROWN_PROPOSAL = FabricEntityTypeBuilder
			.create(MobCategory.MISC)
			.<ThrownProposalEntity>entityFactory(ThrownProposalEntity::new)
			.dimensions(EntityDimensions.fixed(0.25F, 0.25F))
			.trackRangeBlocks(4)
			.trackedUpdateRate(10)
			.build();
	public static final Command<CommandSourceStack> BECOME_A_DICTATOR = context -> {
		Entity source = context.getSource().getEntityOrException();
		if (source instanceof ServerPlayer player) {
			context.getSource().sendSuccess(Component.literal("u r now a president mwah mwah mwah"), false);
			((PlayerSetRuleAccessor) Rules.PRESIDENT).makeDictator(PlayerEntry.from(player));
			return 1;
		}
		return 0;
	};
	public static ProposalForMoreProposalsRule PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS
			= new ProposalForMoreProposalsRule();
	public static boolean RULES_REGISTERED = false;


	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("machine_vote_gun_chan", "proposal"), PROPOSAL);
		Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation("machine_vote_gun_chan", "thrown_proposal"), THROWN_PROPOSAL);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register(entries -> entries.accept(PROPOSAL.getDefaultInstance()));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("giveMePower")
						.requires(source -> source.hasPermission(4)).executes(BECOME_A_DICTATOR)));
		LOGGER.info("Hello Fabric or Quilt world!");
	}

	public static void registerRules() {
		Registry.register(
				BuiltInRegistries.RULE,
				new ResourceLocation(
						"machine_vote_gun_chan",
						"proposals_proposals_proposals_proposals_proposals_proposals_proposals_proposals_proposals_proposals_proposals_proposals"
				),
				PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS_PROPOSALS
		);
	}
}
