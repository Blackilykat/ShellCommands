package dev.blackilykat.shellcommands;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShellCommands implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ShellCommands");

    /**
     * Instance of the latest MinecraftServer instance that started,
     * which will normally be the only running server.
     * <br>
     * It's here because you can't easily retrieve it at any time.
     */
    public static MinecraftServer server = null;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(mcserver -> server = mcserver);
        ServerLifecycleEvents.SERVER_STOPPED.register(mcserver -> server = null);
    }
}