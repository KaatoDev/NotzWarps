package dev.kaato.notzwarps.commands

import dev.kaato.notzapi.utils.MessageU.sendHeader
import dev.kaato.notzwarps.managers.CommandsManager.autoSlotCMD
import dev.kaato.notzwarps.managers.CommandsManager.getWarpIconCMD
import dev.kaato.notzwarps.managers.CommandsManager.listWarpCMD
import dev.kaato.notzwarps.managers.CommandsManager.removeWarpCMD
import dev.kaato.notzwarps.managers.CommandsManager.setDisplayCMD
import dev.kaato.notzwarps.managers.CommandsManager.setLoreCMD
import dev.kaato.notzwarps.managers.CommandsManager.setSlotCMD
import dev.kaato.notzwarps.managers.CommandsManager.setWarpCMD
import dev.kaato.notzwarps.managers.CommandsManager.setWarpLocCMD
import dev.kaato.notzwarps.managers.CommandsManager.setWarpLoreCMD
import dev.kaato.notzwarps.managers.CommandsManager.setwarpEnchantCMD
import dev.kaato.notzwarps.managers.CommandsManager.setwarpIconCMD
import dev.kaato.notzwarps.managers.CommandsManager.spawnToWarpCMD
import dev.kaato.notzwarps.managers.CommandsManager.spawnVipCMD
import dev.kaato.notzwarps.managers.CommandsManager.unsetWarpSlotCMD
import dev.kaato.notzwarps.managers.WarpManager.containsWarp
import dev.kaato.notzwarps.managers.WarpManager.resetMenu
import dev.kaato.notzwarps.managers.WarpManager.warpList
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class NWarpC : TabExecutor {
    override fun onCommand(p: CommandSender?, cmd: Command?, label: String?, args: Array<out String?>?): Boolean {
        if (p !is Player) return false

        val a = args?.map { it?.lowercase() ?: "" }

        if (!p.hasPermission("notzwarps.admin")) {
            p.sendMessage("&cNo permission.")
            return false
        }

        if (a.isNullOrEmpty()) {
            help(p, a)
            return true
        }

        var warp = containsWarp(a[0])
        var w = if (warp) a[0] else ""
        val help = { help(p, a, w) }

        when (a.size) {
            1 -> when (a[0]) {
                "list" -> listWarpCMD(p)
                "resetmenu" -> resetMenu()
                else -> help()
            }

            2 -> if (warp) when (a[1]) {
                "delete", "remove" -> removeWarpCMD(p, w)
                "get" -> getWarpIconCMD(p, w)
                "set" -> setWarpLocCMD(p, w)
                "setmaterial" -> setwarpIconCMD(p, w)
                "unsetslot" -> unsetWarpSlotCMD(p, w)
                else -> help()
            }
            else when (a[0]) {
                "autoslot" -> autoSlotCMD(p, a[1])
                "set" -> setWarpCMD(p, a[1])
                "setlore" -> setWarpLoreCMD(p, a[1])
                "spawntowarp" -> spawnToWarpCMD(p, a[1])
                "spawnvip" -> spawnVipCMD(p, a[1])
                else -> help()
            }

            3 -> if (warp) when (a[1]) {
                "setdisplay" -> setDisplayCMD(p, w, args[2] ?: "")
                "setenchant" -> setwarpEnchantCMD(p, w, a[2])
                "setlore" -> setLoreCMD(p, w, a[2])
                "setslot" -> setSlotCMD(p, w, a[2])
                "setmaterial" -> setwarpIconCMD(p, w, a[2])
                else -> help()

            } else if (a[0] == "setlore") setLoreCMD(p, a[1], a[2])
            else help()


            else -> if (warp) {
                when (a[1]) {
                    "setdisplay" -> setDisplayCMD(p, w, args.slice(2 until a.size).filterNotNull())
                    "setlore" -> setLoreCMD(p, w, a.slice(2 until a.size).toList())

                    else -> help()
                }
            } else help()
        }
        return true
    }

    override fun onTabComplete(p: CommandSender?, cmd: Command?, label: String?, args: Array<out String?>?): List<String?>? {
        return if (args?.size == 1 && args[0] == "remove") warpList().toList()
        else Collections.emptyList()
    }

    private fun help(p: Player, a: List<String>? = null, warp: String? = null) {
        val utilize = "&eUse &f/&enw &e"
        val help = """
            &7+ &f<&ewarp&f> &7- To see the warp customization options.
            &7+ &eautoslot &f<&eon&f/&eoff&f> &7- Enables/disables the auto-sort of warps in the Warp GUI.
            &7+ &elist &7- List all enabled warps.
            &7+ &eresetmenu &7- Reset the warp menu.
            &7+ &eset &f<&ewarp&f> &7- Sets the current location in a new or existing warp, creating it.
            &7+ &esetenchant &f<&eon&f/&eoff&f> &7- Changes if the warp item is enchanted or not.
            &7+ &esetlore &f<&elore...&f> &7- Changes the default lore of warps (be careful as it rewrites existing ones).
            &7+ &espawntowarp &f<&ewarp&f/&eon&f/&eoff&f> &7- Enables/disables or changes the default spawn warp.
            &7+ &espawnvip &f<&ewarp&f/&eon&f/&eoff&f> &7- Enables, disables, or changes the default spawn warp for VIP players.
        """.trimIndent()

        if (a.isNullOrEmpty()) sendHeader(p, "$utilize &7+\n$help")
        else if (!warp.isNullOrEmpty()) helpWarps(p, a, warp)
        else sendHeader(
            p, utilize + when (a[0]) {
                "autoslot" -> "autoslot &f<&eon&f/&eoff&f> &7- Enables/disables the auto-sort of warps in the Warp GUI"
                "list" -> "list &7- List all enabled warps"
                "resetmenu" -> "resetmenu &7- Reset the warp menu."
                "set" -> "set &f<&ewarp&f> &7- Sets the current location in an new or existing warp, creating it."
                "setlore" -> "setlore &f<&elore...&f> &7- Changes the default lore of warps (be careful as it rewrites existing ones)."
                "spawntowarp" -> "spawntowarp &f<&ewarp&f/&eon&f/&eoff&f> &7- Enables/disables or changes the default spawn warp."
                "spawnvip" -> "spawnvip &f<&ewarp&f/&eon&f/&eoff&f> &7- Enables, disables, or changes the default spawn warp for VIP players."

                else -> "&7+\n$help"
            }
        )
    }

    private fun helpWarps(p: Player, a: List<String>, warp: String) {
        val utilize = "&eUtilize &f/&enw &a$warp &e"
        val help = """
            &7+ &eget &7- Get the warp's item.
            &7+ &eremove &7- Deletes the warp.
            &7+ &eset &7- Sets the current location in the warp.
            &7+ &esetdisplay &f<&edisplay...&f> &7- Change warp's item display.
            &7+ &esetenchant &f<&eon&f/&eoff&f> &7- Changes if the warp item is enchanted or not.
            &7+ &esetlore &f<&elore...&f> &7- Change warp's item lore.
            &7+ &esetslot &f<&eslot&f> &7- Changes warp's slot.
            &7+ &esetMaterial &f<&ematerial&f> &7- Changes the warp's item material.
            &7+ &eunsetslot &7- Resets the warp's slot (useful to hide it if auto-sort is enabled).
        """.trimIndent()

        if (a.size == 1) sendHeader(p, "$utilize &7+\n$help")
        else sendHeader(
            p, utilize + when (a[1]) {
                "get" -> "get &7- Get the warp's item."
                "remove" -> "remove &7- Deletes the warp."
                "set" -> "set &7- Sets the current location in the warp."
                "setdisplay" -> "setdisplay &f<&edisplay...&f> &7- Change warp's item display."
                "setlore" -> "setlore &f<&elore...&f> &7- Change warp's item lore."
                "setslot" -> "setslot &f<&eslot&f> &7- Changes warp's slot."
                "setMaterial" -> "setMaterial &f<&ematerial&f> &7- Changes the warp's item material."
                "unsetslot" -> "unsetslot &7- Resets the warp's slot (useful to hide it if auto-sort is enabled)."

                else -> "&7+\n$help"
            }
        )
    }
}