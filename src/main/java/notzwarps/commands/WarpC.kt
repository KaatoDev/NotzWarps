package notzwarps.commands

import notzapi.utils.MessageU.c
import notzapi.utils.MessageU.send
import notzapi.utils.MessageU.sendHeader
import notzwarps.Main.Companion.warpGUI
import notzwarps.managers.WarpM.teleport
import notzwarps.managers.WarpM.warps
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class WarpC : TabExecutor {
    private lateinit var p: Player

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player)
            return false

        p = sender

        if (label == "spawn") {
            if (warps.containsKey("spawn"))
                teleport(p, "spawn")
            else
                send(p, "&cO Spawn ainda não foi setado.")
            return true
        }
        if (label == "spawn") {
            if (warps.containsKey("spawn"))
                teleport(p, "spawn")
            else
                send(p, "&cO Spawn ainda não foi setado.")
            return true
        }

        when (args!!.size) {
            0 -> p.openInventory(warpGUI.menu.get())
            1 -> {
                if (warps.containsKey(args[0].lowercase()))
                    teleport(p, args[0].lowercase())

                else send(p, "&cNão há uma warp com o nome de &f${args[0]}&c.")
            }
            2 -> {
                if (p.hasPermission("notzwarps.admin")) {
                    if (!warps.containsKey(args[0]))
                        send(p, "&cNão há uma warp com o nome de &f${args[0]}&c.")

                    else if (!Bukkit.getOnlinePlayers().map { it.name.lowercase() }.contains(args[1].lowercase()))
                        send(p, "&cO player &f${args[1]}&c não existe ou está offline.")

                    else teleport(Bukkit.getPlayer(args[1]), args[0])

                } else help()
            }
            else -> help()
        }
        return true
    }

    override fun onTabComplete(p: CommandSender?, p1: Command?, p2: String?, args: Array<out String>?): MutableList<String> {
        return when (args!!.size) {
            0 -> warps.keys.toMutableList()
            1 -> Bukkit.getOnlinePlayers().map { it.name }.toMutableList()

            else -> Collections.emptyList()
        }
    }

    private fun help() {
        sendHeader(p)
        p.sendMessage(c("&f/&ewarp &7- Abre o menu de warps."))
        p.sendMessage(c("&f/&ewarp &f<&ewarp&f> &7- Teleporta diretamente para uma warp."))
        if (p.hasPermission("notzwarps.admin"))
            p.sendMessage(c("&f/&ewarp &f<&ewarp&f> &f<&eplayer&f>&7- Teleporta o player diretamente para uma warp."))
        p.sendMessage("")
    }
}