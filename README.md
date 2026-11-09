# Minecraft Plugin: HideNameToggle

HideNameToggle is a Minecraft plugin for Paper servers that lets each player toggle whether player nametags are visible to them. Use the `/hide name` command to hide or show other players' name tags for your own view. The setting is saved across restarts.

## Install

1. Build the plugin with Maven using Java 21, or use the generated `plugin.jar` file.
2. Stop your Paper Minecraft server.
3. Copy `plugin.jar` into the server's `plugins` folder.
4. Start or restart the server.
5. Confirm that HideNameTag loaded successfully in the server console.

Requirements:

- Paper 1.21.11 or a compatible Paper 1.21 server
- Java 21

## Use

1. Join the server as a player.
2. Run `/hide name`.
3. Other players' nametags will be hidden from your view.
4. Run `/hide name` again to make the nametags visible again.

The setting is stored for each player's UUID in the plugin configuration, so it remains active after reconnecting or restarting the server. The command only changes the view of the player who uses it.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/8d3d7603-4a93-45ee-a14a-7ca883a09043?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=8d3d7603-4a93-45ee-a14a-7ca883a09043
