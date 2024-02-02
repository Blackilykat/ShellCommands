package dev.blackilykat.shellcommands.exception;

/**
 * Gets thrown when the parser finds an unexpected key,
 * for example when there are unclosed <code>/{</code>
 */
public class InvalidCommandSyntaxException extends Exception {
    public InvalidCommandSyntaxException() {
        //TODO: more detail here
    }
}
