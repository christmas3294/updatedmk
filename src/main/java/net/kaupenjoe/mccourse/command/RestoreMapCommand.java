package net.kaupenjoe.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.*;

/**
 * Command that restores the overworld from a backup folder named "A".
 * This is a minimal example and assumes the backup exists under
 * <server>/backups/A. Running this while the server is online may cause
 * corruption; it is only for demonstration purposes.
 */
public class RestoreMapCommand {
    public RestoreMapCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("restoremap")
                .requires(cs -> cs.hasPermission(2))
                .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        Path backup = server.getServerDirectory().toPath().resolve("backups").resolve("A");
        Path target = server.getWorldPath(LevelResource.ROOT);

        try {
            copyRecursive(backup, target);
            context.getSource().sendSuccess(() -> Component.literal("Restored map A"), true);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Failed to restore: " + e.getMessage()));
            return 0;
        }

        // Optionally kick players so they reload the world
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.disconnect(Component.literal("World restored. Please reconnect."));
        }
        return 1;
    }

    private static void copyRecursive(Path src, Path dest) throws IOException {
        if (!Files.exists(src)) {
            throw new IOException("Backup not found: " + src);
        }
        Files.walk(src).forEach(from -> {
            Path to = dest.resolve(src.relativize(from).toString());
            try {
                if (Files.isDirectory(from)) {
                    Files.createDirectories(to);
                } else if (!from.getFileName().toString().equals("session.lock")) {
                    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
