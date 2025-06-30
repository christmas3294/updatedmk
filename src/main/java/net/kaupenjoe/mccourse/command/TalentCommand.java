package net.kaupenjoe.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import net.kaupenjoe.mccourse.talent.PlayerTalentData;
//import net.kaupenjoe.mccourse.talent.Talent;
//import net.kaupenjoe.mccourse.event.TalentEvents;
//import net.kaupenjoe.mccourse.screen.TalentTreeScreen;
import net.kaupenjoe.mccourse.event.TalentEvents;
import net.kaupenjoe.mccourse.talent.PlayerTalentData;
import net.kaupenjoe.mccourse.talent.Talent;
import net.kaupenjoe.mccourse.talent.TalentTreeScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
public class TalentCommand {
    public TalentCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("talent")
                .then(Commands.literal("addpoints")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> addPoints(ctx, IntegerArgumentType.getInteger(ctx, "amount")))))
                .then(Commands.literal("unlock")
                        .then(Commands.argument("talent", StringArgumentType.word())
                                .executes(this::unlock)))
                .then(Commands.literal("gui").executes(this::openGui)));
    }

    private int addPoints(CommandContext<CommandSourceStack> context, int amount) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayer();
        PlayerTalentData.addPoints(player, amount);
        context.getSource().sendSuccess(() -> Component.literal("Added " + amount + " talent points."), false);
        return 1;
    }

    private int unlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayer();
        String name = StringArgumentType.getString(context, "talent").toUpperCase();
        Talent talent;
        try {
            talent = Talent.valueOf(name);
        } catch (IllegalArgumentException e) {
            context.getSource().sendFailure(Component.literal("Unknown talent: " + name));
            return -1;
        }

        if (PlayerTalentData.unlockTalent(player, talent)) {
           // TalentEvents.applyTalents(player);
            context.getSource().sendSuccess(() -> Component.literal("Unlocked " + name + "!"), false);
            return 1;
        } else {
            context.getSource().sendFailure(Component.literal("Could not unlock " + name));
            return -1;
        }
    }

    private int openGui(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayer();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft.getInstance().setScreen(new TalentTreeScreen(player));
        });
        return 1;
    }
}