package dev.blackilykat.shellcommands.command;

/**
 * Component that doesn't need parsing to obtain its value.<br>
 * For example, in this command: <code>say /{data get entity @r Health}/</code>,
 * <code>say </code> would be a CommandTextComponent.
 */
public class CommandTextComponent extends CommandComponent {
    public String value;
    public CommandTextComponent(String value) {
        this.value = value.trim();
    }

    @Override
    public String value() {
        return value;
    }
}
