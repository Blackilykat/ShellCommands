package dev.blackilykat.shellcommands.mixin;

import com.mojang.brigadier.ParseResults;
import dev.blackilykat.shellcommands.CommandParser;
import dev.blackilykat.shellcommands.ShellCommands;
import dev.blackilykat.shellcommands.command.Command;
import dev.blackilykat.shellcommands.exception.InvalidCommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    /**
     * Prevents commands from running normally and forces them to
     * get parsed through the ShellCommands parser
     */
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void shellcommands$overrideCommandExecution(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci) {
        // ShellCommands.LOGGER.info("Command " + command + " was ran!");
        try {
            String output = CommandParser.parse(command, parseResults).value();
            // ShellCommands.LOGGER.info("Output: " + output);
            parseResults.getContext().getSource().sendMessage(Text.of(output));
        } catch(InvalidCommandSyntaxException e) {
            parseResults.getContext().getSource().sendError(Text.literal("Invalid syntax!"));
            ci.cancel();
        }
        ci.cancel();

    }
}
