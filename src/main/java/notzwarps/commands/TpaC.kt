package notzwarps.commands

import notzapi.utils.MessageU.c
import notzapi.utils.MessageU.send
import notzapi.utils.MessageU.sendHeader
import notzwarps.managers.TpaM.containRequest
import notzwarps.managers.TpaM.sendTpaRequest
import notzwarps.managers.TpaM.tpaccept
import notzwarps.managers.TpaM.tpadeny
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class TpaC : TabExecutor {
    private lateinit var p: Player

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player)
            return false
        sender.also { p = it }

        if (cmd!!.name != label)
            when (label) {
                "tpaccept", "tpaceitar" -> {
                    when (args!!.size) {
                        0 -> {
                            if (containRequest(p))
                                tpaccept(p)
                            else send(p, "&cNão há request de tpa.")
                        }

                        1 -> {
                            if (Bukkit.getPlayer(args[0]) != null && containRequest(p, Bukkit.getPlayer(args[0])))
                                tpaccept(p, Bukkit.getPlayer(args[0]))
                            else send(p, "&cNão há request de tpa do player &f${args[0]}&c.")
                        }

                        else -> send(p, "&cUtilize apenas &f/&c$label &f(&cPlayer&f)")
                    }
                }

                "tpacancel", "tpadeny", "tparefuse", "tpacancelar", "tparecusar" -> {
                    when (args!!.size) {
                        0 -> {
                            if (containRequest(p))
                                tpadeny(p, true)
                            else send(p, "&cNão há request de tpa.")
                        }

                        1 -> {
                            if (Bukkit.getPlayer(args[0]) != null && containRequest(p, Bukkit.getPlayer(args[0])))
                                tpadeny(Bukkit.getPlayer(args[0]), false)
                            else send(p, "&cNão há request de tpa do player &f${args[0]}&c.")
                        }

                        else -> send(p, "&cUtilize apenas &f/&c$label &f(&cPlayer&f)")
                    }
                }

                else -> help()
            }
        else if (args!!.size == 1) {
            if (args[0].lowercase() == p.name.lowercase())
                send(p, "&cVocê não pode mandar tpa a si próprio.")

            else if (Bukkit.getPlayer(args[0]) != null && Bukkit.getOnlinePlayers().map { it.name.lowercase() }
                    .contains(args[0].lowercase()))
                sendTpaRequest(p, Bukkit.getPlayer(args[0]))

            else send(p, "&cO player &f${args[0]}&c não existe ou está offline.")

        } else send(p, "&cUtilize &f/&ctpa &f<&cPlayer&f>")

        return true
    }

    override fun onTabComplete(p: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): MutableList<String> {
        return if (args!!.isEmpty()) Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
            else Collections.emptyList()
    }

    private fun help() {
        sendHeader(p)
        p.sendMessage(c("&f/&etpaceitar &7- Aceita o tpa requisitado."))
        p.sendMessage(c("&f/&etpaceitar &f<&ePlayer&f> &7- Aceita o tpa de um player específico."))
        p.sendMessage(c("&f/&etparecusar &7- Recusa o tpa requisitado."))
        p.sendMessage(c("&f/&etparecusar &f<&ePlayer&f> &7- Recusa o tpa de um player específico."))
    }

    private fun helpTp() {
        sendHeader(p)
        p.sendMessage(c("&f/&etp &f<&ePlayer&f> &7- Teleporte-se a um player."))
        p.sendMessage(c("&f/&etp &f<&ePlayer&f> (&ePlayer&f) &7- Teleporte um player à outro."))
        p.sendMessage(c("&f/&etp &f<&eX&f> <&eZ&f> &7- Teleporte-se a um coordenada com a mesma altura."))
        p.sendMessage(c("&f/&etp &f<&eX&f> <&eY&f> <&eZ&f> &7- Teleporte-se a uma coordenada exata."))
    }
}