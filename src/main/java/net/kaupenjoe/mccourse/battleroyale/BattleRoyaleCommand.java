package net.kaupenjoe.mccourse.battleroyale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kaupenjoe.mccourse.battleroyale.BattleRoyaleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

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

        if (BattleRoyaleManager.getroomplayercount()) {

                //Component.literal(dimension.location().getPath()
                BattleRoyaleManager.start(server);
                context.getSource().sendSuccess(() -> Component.literal("房间开始游戏"), true);



        }

        return 1;
    }

    private int stop(CommandContext<CommandSourceStack> context) {
        if (BattleRoyaleManager.getroom()) {
            MinecraftServer server = context.getSource().getServer();
            if (!BattleRoyaleManager.isActive()) {
                context.getSource().sendFailure(Component.literal("No active battle."));
                return 0;
            }
            BattleRoyaleManager.stop(server);

            context.getSource().sendSuccess(() -> Component.literal("Battle stopped."), true);
            return 1;  }

        return 0;
    }

    private int join(CommandContext<CommandSourceStack> context) {

        ServerPlayer player = context.getSource().getPlayer();
        ResourceKey<Level> dimension = player.level().dimension();

            //player.sendSystemMessage(Component.literal(dimension.location().getPath()));
            if (!BattleRoyaleManager.isActive()) {
                // context.getSource().sendFailure(Component.literal("No active battle."));
                //return 0;
                BattleRoyaleManager.addPlayer(player);
                context.getSource().sendSuccess(() -> Component.literal("加入房间"), false);
            }

       else{
            player.sendSystemMessage(Component.literal("你需要去到枪械地图世界"));
        }

        return 1;
    }
}