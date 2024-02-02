package dev.blackilykat.shellcommands;

import com.mojang.brigadier.ParseResults;
import dev.blackilykat.shellcommands.command.Command;
import dev.blackilykat.shellcommands.command.CommandOperatorComponent;
import dev.blackilykat.shellcommands.command.CommandTextComponent;
import dev.blackilykat.shellcommands.exception.InvalidCommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class CommandParser {
    /**
     * Parse the command into its components recursively.
     * @param command the original, plain text command
     * @param parseResults vanilla minecraft's parseResults needed to run the command
     * @return the parsed command
     * @throws InvalidCommandSyntaxException if the syntax of the command is not valid
     */
    public static Command parse(String command, ParseResults<ServerCommandSource> parseResults) throws InvalidCommandSyntaxException {
        Command parsedCommand = new Command(parseResults);
        StringBuilder latest = new StringBuilder();
        StringBuilder special = new StringBuilder();
        Type type = Type.TEXT;
        byte escaping = 0;
        for(int i = 0; i < command.length(); i++) {
            char character = command.charAt(i);
            if(special.toString().equals("&") && type == Type.TEXT) {
                parsedCommand.components.add(new CommandTextComponent(latest.toString()));
                latest = new StringBuilder();
                special = new StringBuilder();
                if(character == '&') {
                     parsedCommand.components.add(new CommandOperatorComponent(CommandOperatorComponent.Operator.THEN));
                     continue;
                }
                parsedCommand.components.add(new CommandOperatorComponent(CommandOperatorComponent.Operator.WHILE));
            }
            switch (character) {
                case '\\' -> {
                    if(escaping > 0) latest.append('\\');
                    else escaping = 2;
                }
                case '/' -> {
                    if(escaping > 0) {
                        latest.append('/');
                    }
                    else {
                        special.append('/');
                    }
                }
                case '{' -> {
                    if(special.toString().equals("/")) {
                        special.append('{');
                    } else {
                        latest.append('{');
                    }
                }
                case '}' -> {
                    if(command.length() > i+1 && command.charAt(i+1) == '/') special.append('}');
                    else latest.append('}');
                }
                case '&' -> {
                    if(escaping > 0 || type == Type.COMMAND) {
                        latest.append('&');
                    } else {
                        special.append('&');
                    }
                }
                default -> {
                    latest.append(character);
                }
            }
            if(special.toString().equals("/{")) {
                if (type == Type.COMMAND) {
                    throw new InvalidCommandSyntaxException();
                }
                parsedCommand.components.add(new CommandTextComponent(latest.toString()));
                latest = new StringBuilder();
                special = new StringBuilder();
                type = Type.COMMAND;
            } else if(special.toString().equals("}/")) {
                if(type == Type.TEXT) {
                    throw new InvalidCommandSyntaxException();
                }
                parsedCommand.components.add(parse(latest.toString(), parseResults));
                latest = new StringBuilder();
                special = new StringBuilder();
                type = Type.TEXT;
            }
            if(escaping > 0) {
                escaping--;
            }
        }
        if(type == Type.COMMAND) throw new InvalidCommandSyntaxException();

        parsedCommand.components.add(new CommandTextComponent(latest.toString()));

        if(special.toString().equals("&")) {
            parsedCommand.components.add(new CommandOperatorComponent(CommandOperatorComponent.Operator.WHILE));
        }
        return parsedCommand;
    }

    /**
     * What the parser is currently parsing, indicates how it should behave with symbols.
     */
    public enum Type {
        /**
         * Reading plain text which will become a CommandTextComponent.
         */
        TEXT,
        /**
         * Reading part of a command substitution which will become a Command object
         */
        COMMAND
    }
}
