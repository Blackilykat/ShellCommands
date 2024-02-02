package dev.blackilykat.shellcommands.command;

/**
 * An operator, which doesn't have a value itself but affects the
 * surrounding components.
 */
public class CommandOperatorComponent extends CommandComponent {
    public final Operator operator;
    public CommandOperatorComponent(Operator operator) {
        this.operator = operator;
    }
    @Override
    public String value() {
        return operator.name();
    }

    public enum Operator {
        /**
         * Equivalent of && <br>
         * Ends a command and starts another command to be run after the first finished.
         */
        THEN,
        /**
         * Equivalent of & <br>
         * Ends a command to be run asynchronously and (optionally) starts another
         * command to run now
         */
        WHILE,
        /**
         * Equivalent of | <br>
         * Ends a command to be run asynchronously and starts another command which
         * will recieve the output of the first as input.
         */
        PIPE,
    }
}
