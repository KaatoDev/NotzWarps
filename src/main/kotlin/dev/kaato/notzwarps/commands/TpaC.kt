package dev.kaato.notzwarps.commands

import dev.kaato.notzapi.utils.MessageU.send
import dev.kaato.notzapi.utils.MessageU.sendHeader
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

        val a = args?.map { it?.lowercase() }?.filterNotNull()

        if (a == null) {
            help(p)
            return true
        }

        if (cmd?.name != label) when (label) {
            "tpaccept", "tpaceitar" -> {
                when (a.size) {
                    0 -> {
                        if (TpaManager.containRequest(p)) TpaManager.tpaccept(p)
                        else send(p, "noRequest1")
                    }

                    1 -> {
                        if (Bukkit.getPlayer(a[0]) != null && TpaManager.containRequest(p, Bukkit.getPlayer(a[0])!!)) TpaManager.tpaccept(p, Bukkit.getPlayer(a[0])!!)
                        else send(p, "noRequest2", a[0])
                    }

                    else -> send(p, "&cUtilize apenas &f/&c$label &f(&cPlayer&f)")
                }
            }

            "tpacancel", "tpadeny", "tparefuse", "tpacancelar", "tparecusar" -> {
                when (a.size) {
                    0 -> {
                        if (TpaManager.containRequest(p)) TpaManager.tpadeny(p, true)
                        else send(p, "noRequest1")
                    }

                    1 -> {
                        if (Bukkit.getPlayer(a[0]) != null && TpaManager.containRequest(p, Bukkit.getPlayer(a[0])!!)) TpaManager.tpadeny(Bukkit.getPlayer(a[0])!!, false)
                        else send(p, "noRequest2",a[0])
                    }

                    else -> send(p, "&cUtilize apenas &f/&c$label &f(&cPlayer&f)")
                }
            }

            else -> help(p)
        }
        else if (a.size == 1) {
            if (a[0].lowercase() == p.name.lowercase()) send(p, "selfTpa")
            else if (Bukkit.getPlayer(a[0]) != null && Bukkit.getOnlinePlayers().map { it.name.lowercase() }.contains(a[0].lowercase())) TpaManager.sendTpaRequest(p, Bukkit.getPlayer(a[0])!!)
            else send(p, "&cO player &f${a[0]}&c não existe ou está offline.")

        } else send(p, "&cUtilize &f/&ctpa &f<&cPlayer&f>")

        return true
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, label: String, args: Array<out String?>): List<String?>? {
        return if (args.isEmpty()) Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
        else Collections.emptyList()
    }

    private fun help(p: Player) {
        sendHeader(
            p, """
            &f/&etpaceitar &7- Aceita o tpa requisitado.
            &f/&etpaceitar &f<&ePlayer&f> &7- Aceita o tpa de um player específico.
            &f/&etparecusar &7- Recusa o tpa requisitado.
            &f/&etparecusar &f<&ePlayer&f> &7- Recusa o tpa de um player específico.
        """.trimIndent()
        )
    }

    private fun helpTp(p: Player) {
        sendHeader(
            p, """
            &f/&etp &f<&ePlayer&f> &7- Teleporte-se a um player.
            &f/&etp &f<&ePlayer&f> (&ePlayer&f) &7- Teleporte um player à outro.
            &f/&etp &f<&eX&f> <&eZ&f> &7- Teleporte-se a um coordenada com a mesma altura.
            &f/&etp &f<&eX&f> <&eY&f> <&eZ&f> &7- Teleporte-se a uma coordenada exata.
        """.trimIndent()
        )
    }
}