package dev.kaato.notzwarps.commands

import dev.kaato.notzwarps.Main.Companion.messageU
import dev.kaato.notzwarps.Main.Companion.warpGUI
import dev.kaato.notzwarps.managers.WarpManager.containsWarp
import dev.kaato.notzwarps.managers.WarpManager.teleport
import dev.kaato.notzwarps.managers.WarpManager.warpList
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class WarpC : TabExecutor {

    override fun onCommand(p: CommandSender?, cmd: Command?, label: String?, args: Array<out String?>?): Boolean {
        if (p !is Player) return false

        val a = args?.map { it?.lowercase()?:"" }

        val help = {help(p)}

        if (label == "spawn") {
            if (containsWarp("spawn"))
                teleport(p, "spawn")
            else
                messageU.send(p, "&cSpawn has not yet been set.")
            return true
        } else if (label == "spawnvip") {
            if (containsWarp("spawnvip"))
                teleport(p, "spawnvip")
            else
                messageU.send(p, "&cSpawnVIP has not yet been set.")
            return true
        }

        if (a.isNullOrEmpty()) {
            p.openInventory(warpGUI.menu.get())
            return true
        }

        when (a.size) {
            1 -> {
                if (containsWarp(a[0].lowercase()))
                    teleport(p, a[0].lowercase())
                else messageU.send(p, "warpNotFound", a[0])
            }

            2 -> {
                if (p.hasPermission("notzwarps.admin")) {
                    if (!containsWarp(a[0]))
                        messageU.send(p, "warpNotFound", a[0])
                    else if (Bukkit.getPlayer(a[1]) != null)
                        teleport(Bukkit.getPlayer(a[1])!!, a[0])
                    else messageU.send(p, "playerOffline", a[1])

                } else help.invoke()
            }

            else -> help.invoke()
        }
        return true
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, label: String, args: Array<out String?>): List<String?>? {
        return when (args.size) {
            0 -> warpList()
            1 -> Bukkit.getOnlinePlayers().map { it.name }.toMutableList()

            else -> Collections.emptyList()
        }
    }

    private fun help(p: Player) {
        messageU.sendHeader(
            p, """
            &f/&ewarp &7- Open warp menu.
            &f/&ewarp &f<&ewarp&f> &7- Teleports directly to a warp.
            ${
                if (p.hasPermission("notzwarps.admin"))
                    "&f/&ewarp &f<&ewarp&f> &f<&eplayer&f>&7- Teleports the player directly to a warp."
                else ""
            }
        """.trimIndent()
        )
    }
}