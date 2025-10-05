<div align="center">
<img src="https://github.com/user-attachments/assets/cee005d7-6240-46ec-b167-ca4df76172eb" alt="NotzWarps" height="300" >

#
NotzWarps is a complete and fully customizable Warp and TPA plugin that features: TPA with clickable messages, integrated GUI with customizable item icons and names, enabled auto-sort for warps, etc.

<br/>

## Information

### `Warps`
It has a menu that allows you to manually set or automatically distribute warp slots. If set to automatic, the order will be assigned to the set slots, and only those without a "-1" slot will appear. Easy warp creation and editing system, including slot, display and item. Delay when teleporting to warps, but players with VIP permission will teleport instantly. Teleportation cancellation when moving.

### `TPAs`
Intuitive TPA system with hover and clickable messages to accept or decline. Several aliases to accept and reject TPAs. Delay when teleporting to players, but players with VIP permission will teleport instantly. Teleport cancellation when moving.

<br/>

## Images
![Warp menu](https://cdn.modrinth.com/data/cached_images/826bbdc9b5b6710e61a4e62c10c3e3886aa2f8df.png)
![Warp menu*](https://cdn.modrinth.com/data/cached_images/5dbf822ed270c3ac503131ff3ed3543bd117245b.png)

</div>

<br/>

## Placeholders
 - `{default}` - Warp display or nickname from the OTHER player.
 - `{player_name}` - Name of the player that is executing the command.
 - `{player_displayname}` - Displayname of the player that is executing the command.

<br/>

## Permissions
- `notzwarps.warp.{warp}` - Allows the player to teleport to the warp.
- `notzwarps.admin` - Enables the player to use the /nw admin command.
- `notzwarps.nodelay` - Enables the player to teleport instantly for warp or player (recommended for admin use only).
- `notzwarps.vip` - VIP permission that allows the player to instantly teleport to warps only (not to other players) and always spawn in the VIP Warp (recommended for VIP players).

<br/>

## Commands
### `/tpa`
 - Access to TPA commands.

### `/warps`
 - Access to the Warp GUI.

### `/spawn`
 - Teleport to the Spawn.

### `/spawnvip`
 - Teleport to the VIP Spawn.

### `/tpaccept` | `/tpadeny`
 - Accepts or denies a TPA request.

### `/nwarp`
 - `autoslot` \<on/off> - Enables/disables the auto-sort of warps in the Warp GUI;
 - `list` - List all enabled warps;
 - `resetmenu` - Reset the warp menu;
 - `set` \<warp> - Sets the current location in a new or existing warp, creating it;
 - `setlore` \<lore...> - Changes the default lore of warps (be careful as it rewrites existing ones);
 - `spawntowarp` \<warp/on/off> - Enables/disables or changes the default spawn warp;
 - `spawnvip` \<warp/on/off> - Enables, disables, or changes the default spawn warp for VIP players;
 - `<warp>`
   - `get` - Get the warp's item;
   - `remove` - Deletes the warp;
   - `set` - Sets the current location in the warp;
   - `setdisplay` \<display...> - Change warp's item display;
   - `setenchant` \<on/off> - Changes if the warp item is enchanted or not.
   - `setlore` \<lore...> - Change warp's item lore;
   - `setslot` \<slot> - Changes warp's slot;
   - `setmaterial` \<material> - Changes the warp's item material;
   - `unsetslot` - Resets the warp's slot (useful to hide it if auto-sort is enabled).
<br/>
<sub> | <> required argument. | ( ) optional argument. | </sub>
#

<sub> Tested versions: 1.8 - 1.13 </sub>
