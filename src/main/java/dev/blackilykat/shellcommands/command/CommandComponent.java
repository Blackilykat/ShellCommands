package dev.blackilykat.shellcommands.command;

/**
 * A component of a parsed command.
 */
public abstract class CommandComponent {
    /**
     * @return what this component should be represented as when running
     * the command.
     */
    public abstract String value();
}
