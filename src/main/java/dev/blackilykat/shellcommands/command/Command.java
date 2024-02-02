package dev.blackilykat.shellcommands.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.blackilykat.shellcommands.ShellCommands;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of parsing a command. Because of the command substitution feature,
 * this can be a component of a command itself.
 * <br>
 * For example, in this command: <code>say /{data get entity @r Health}/</code>,
 * both <code>say /{data get entity @r Health}/</code> and
 * <code>data get entity @r Health</code> would be Command objects.
 */
public class Command extends CommandComponent {

    /**
     * The components which make this command.
     */
    public List<CommandComponent> components = new ArrayList<>();

    /**
     * The original sender of this command.
     */
    public ServerCommandSource source;

    /**
     * Minecraft's vanilla parseResults object, used to run the command.
     */
    public ParseResults<ServerCommandSource> parseResults;

    /**
     * The feedback of this command.
     */
    protected final StringBuilder[] output = {new StringBuilder()};

    public Command(ParseResults<ServerCommandSource> parseResults) {
        source = parseResults.getContext().getSource();
        this.parseResults = parseResults;
    }

    /**
     * Runs the command and finds its output
     * @return the string output of the command
     */
    public String value() {
        output[0] = new StringBuilder();

        vanillaRun();

        // delete last newline
        if(!output[0].isEmpty()) output[0].deleteCharAt(output[0].length()-1);
        return output[0].toString();
    }

    /**
     * Turns the components into text and runs the resulting command.
     */
    public void vanillaRun() {
        StringBuilder builder = new StringBuilder();
        for (CommandComponent component : components) {
            if(component instanceof CommandOperatorComponent cop) {
                switch(cop.operator) {
                    case WHILE -> {
                        String cmd = builder.toString();
                        new Thread(() -> {
                            // dangerous... will be improved with vanilla integration mod
                            vanillaRun(cmd);
                        }).start();
                    }
                    case THEN -> {
                        vanillaRun(builder.toString());
                    }
                    // input (for pipe) hasn't been implemented yet...
                }
                builder = new StringBuilder();
                continue;
            }
            builder.append(component.value());
        }
        vanillaRun(builder.toString());
    }


    /**
     * Runs a command without routing it through ShellCommands' parser
     * and redirects all output to this object's output builder.
     * @param command the command to run
     */
    void vanillaRun(String command) {
        CommandManager manager = ShellCommands.server.getCommandManager();
        ServerCommandSource newSource = source.withOutput(new CommandOutput() {
            @Override
            public void sendMessage(Text message) {
                // custom output catching logic
                output[0].append(message.asTruncatedString(Integer.MAX_VALUE)).append('\n');
            }

            @Override
            public boolean shouldReceiveFeedback() {
                return true;
            }

            @Override
            public boolean shouldTrackOutput() {
                return false;
            }

            @Override
            public boolean shouldBroadcastConsoleToOps() {
                return false;
            }
        });
        CommandDispatcher<ServerCommandSource> dispatcher = manager.getDispatcher();
        try {
            dispatcher.execute(dispatcher.parse(command, newSource));
        } catch(CommandSyntaxException e) {
            ShellCommands.LOGGER.info(String.format("""
                    Context: %s
                    Cursor: %s
                    Input: %s
                    Message: %s
                    Type: %s
                    """,
                            e.getContext(),
                            e.getCursor(),
                            e.getInput(),
                            e.getMessage(),
                            e.getType()
                    )
            );
        }
    }
}
