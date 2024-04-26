# ⚠️WARNING⚠️
**Please *do not* blindly add this to your server unless you have a backup of everything. In its current stage, it can and will crash the server and may cause corruption if not used carefully.**
## State of the project
The project is currently stale. I haven't decided to completely abandon it and may decide to keep working on it, but do not expect regular updates while this notice is in the readme.
# ShellCommands
ShellCommands is a mod that aims to add features from the Unix shell to commands to make them more powerful and easier to work with.

## Current features
- Executing multiple commands at once with `&&`
- Command substitution with `/{command}/`
- Executing asynchronously with `&` (⚠️)

## Building

### Linux
You can run the following command with the JAVA_HOME enviorment variable pointing to a java 17 installation:
```
./gradlew build
```
