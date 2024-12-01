package dev.kaato.notzwarps.commands

import dev.kaato.notzapi.utils.MessageU.send
import dev.kaato.notzapi.utils.MessageU.sendHeader
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
                send(p, "&cO Spawn ainda não foi setado.")
            return true
        } else if (label == "spawnvip") {
            if (containsWarp("spawnvip"))
                teleport(p, "spawnvip")
            else
                send(p, "&cO SpawnVIP ainda não foi setado.")
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
                else send(p, "&cNão há uma warp com o nome de &f${a[0]}&c.")
            }

            2 -> {
                if (p.hasPermission("notzwarps.admin")) {
                    if (!containsWarp(a[0]))
                        send(p, "&cNão há uma warp com o nome de &f${a[0]}&c.")
                    else if (Bukkit.getPlayer(a[1]) != null)
                        teleport(Bukkit.getPlayer(a[1])!!, a[0])
                    else send(p, "&cO player &f${a[1]}&c não existe ou está offline.")

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
        sendHeader(
            p, """
            &f/&ewarp &7- Abre o menu de warps.
            &f/&ewarp &f<&ewarp&f> &7- Teleporta diretamente para uma warp.
            ${
                if (p.hasPermission("notzwarps.admin"))
                    "&f/&ewarp &f<&ewarp&f> &f<&eplayer&f>&7- Teleporta o player diretamente para uma warp."
                else ""
            }
        """.trimIndent()
        )
    }
}