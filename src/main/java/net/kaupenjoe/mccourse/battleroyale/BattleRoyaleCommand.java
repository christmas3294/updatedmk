package net.kaupenjoe.mccourse.battleroyale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Basic command for managing a simple battle royale match.
 */
public class BattleRoyaleCommand {
    public BattleRoyaleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("battle")
                .then(Commands.literal("start").requires(cs -> cs.hasPermission(2))
                        .executes(this::start))
                .then(Commands.literal("stop").requires(cs -> cs.hasPermission(2))
                        .executes(this::stop))
                .then(Commands.literal("join")
                        .executes(this::join))
        );
    }

    private int start(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        if (BattleRoyaleManager.isActive()) {
            context.getSource().sendFailure(Component.literal("Battle already running."));
            return 0;
        }
        BattleRoyaleManager.start(server);
        context.getSource().sendSuccess(() -> Component.literal("Battle started."), true);

        return 1;
    }

    private int stop(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        if (!BattleRoyaleManager.isActive()) {
            context.getSource().sendFailure(Component.literal("No active battle."));
            return 0;
        }
        BattleRoyaleManager.stop(server);
        context.getSource().sendSuccess(() -> Component.literal("Battle stopped."), true);
        return 1;
    }

    private int join(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
//        if (!BattleRoyaleManager.isActive()) {
//            context.getSource().sendFailure(Component.literal("No active battle."));
//            return 0;
//        }
        BattleRoyaleManager.addPlayer(player);
        context.getSource().sendSuccess(() -> Component.literal("Joined battle."), false);
        return 1;
    }
}