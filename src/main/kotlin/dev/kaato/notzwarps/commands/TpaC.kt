package dev.kaato.notzwarps.commands

import dev.kaato.notzwarps.Main.Companion.messageU
import dev.kaato.notzwarps.managers.TpaManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class TpaC : TabExecutor {
    override fun onCommand(p: CommandSender?, cmd: Command?, label: String?, args: Array<out String?>?): Boolean {
        if (p !is Player) return false

        val a = args?.mapNotNull { it?.lowercase() }

        if (a == null) {
            help(p)
            return true
        }

        if (cmd?.name != label) when (label) {
            "tpaccept", "tpaceitar" -> {
                when (a.size) {
                    0 -> {
                        if (TpaManager.containRequest(p)) TpaManager.tpaccept(p)
                        else messageU.send(p, "noRequest1")
                    }

                    1 -> {
                        if (Bukkit.getPlayer(a[0]) != null && TpaManager.containRequest(p, Bukkit.getPlayer(a[0])!!)) TpaManager.tpaccept(p, Bukkit.getPlayer(a[0])!!)
                        else messageU.send(p, "noRequest2", a[0])
                    }

                    else -> messageU.send(p, "&cUse only &f/&c$label &f(&cPlayer&f)")
                }
            }

            "tpacancel", "tpadeny", "tparefuse", "tpacancelar", "tparecusar" -> {
                when (a.size) {
                    0 -> {
                        if (TpaManager.containRequest(p)) TpaManager.tpadeny(p, true)
                        else messageU.send(p, "noRequest1")
                    }

                    1 -> {
                        if (Bukkit.getPlayer(a[0]) != null && TpaManager.containRequest(p, Bukkit.getPlayer(a[0])!!)) TpaManager.tpadeny(Bukkit.getPlayer(a[0])!!, false)
                        else messageU.send(p, "noRequest2", a[0])
                    }

                    else -> messageU.send(p, "&cUse only &f/&c$label &f(&cPlayer&f)")
                }
            }

            else -> help(p)
        }
        else if (a.size == 1) {
            if (a[0].lowercase() == p.name.lowercase()) messageU.send(p, "selfTpa")
            else if (Bukkit.getPlayer(a[0]) != null && Bukkit.getOnlinePlayers().map { it.name.lowercase() }.contains(a[0].lowercase())) TpaManager.sendTpaRequest(p, Bukkit.getPlayer(a[0])!!)
            else messageU.send(p, "playerOffline", a[0])

        } else messageU.send(p, "&cUse &f/&ctpa &f<&cPlayer&f>")

        return true
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, label: String, args: Array<out String?>): List<String?>? {
        return if (args.isEmpty()) Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
        else Collections.emptyList()
    }

    private fun help(p: Player) {
        messageU.sendHeader(
            p, """
            &f/&etpaccept &7- Accepts the requested TPA.
            &f/&etpaccept &f<&ePlayer&f> &7- Accepts the tpa of a specific player.
            &f/&etpadeny &7- Denies the requested TPA.
            &f/&etpadeny &f<&ePlayer&f> &7- Denies the TPA for a specific player.
        """.trimIndent()
        )
    }

    private fun helpTp(p: Player) {
        messageU.sendHeader(
            p, """
            &f/&etp &f<&ePlayer&f> &7- Teleport to a player.
            &f/&etp &f<&ePlayer&f> (&ePlayer&f) &7- Teleport one player to another.
            &f/&etp &f<&eX&f> <&eZ&f> &7- Teleport to a coordinate with the same height.
            &f/&etp &f<&eX&f> <&eY&f> <&eZ&f> &7- Teleport to an exact coordinate.
        """.trimIndent()
        )
    }
}