package notzwarps.commands

import notzapi.utils.MessageU.c
import notzapi.utils.MessageU.send
import notzapi.utils.MessageU.sendHeader
import notzwarps.managers.TpaM.containRequest
import notzwarps.managers.TpaM.sendTpaRequest
import notzwarps.managers.TpaM.tpaccept
import notzwarps.managers.TpaM.tpadeny
import org.bukkit.Bukkit
import org.bukkit.Location
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
                "tp", "teleport" -> {
                    if (p.hasPermission("notzwarps.admin")) {
                        when (args!!.size) {
                            1 -> {
                                if (Bukkit.getPlayer(args[0]) != null) {
                                    p.teleport(Bukkit.getPlayer(args[0]))
                                    send(p, "&eVocê foi teleportado para &a${args[0]}&e.")
                                    send(Bukkit.getPlayer(args[0]), "&eO player &a${p.name}&e foi teleportado até você.")

                                } else send(p, "&cEste player não existe ou está offline.")
                            }

                            2 -> {
                                if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[1]) != null ) {
                                    Bukkit.getPlayer(args[0]).teleport(Bukkit.getPlayer(args[1]))
                                    send(Bukkit.getPlayer(args[0]), "&eVocê foi teleportado para &a${args[1]}&e.")
                                    send(Bukkit.getPlayer(args[1]), "&eO player &a${args[0]}&e foi teleportado até você.")

                                } else {
                                    try {
                                        p.teleport(Location(p.world, args[0].toDouble(), p.location.y, args[1].toDouble()))
                                    } catch (e: IllegalFormatConversionException) {
                                        send(p, "&cAlgum dos players não existe ou está offline.")
                                    }
                                }
                            }
                            3 -> {
                                try {
                                    p.teleport(Location(p.world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble()))
                                } catch (e: IllegalFormatConversionException) {
                                    send(p, "&cA localização está inválida!")
                                }
                            }
                            else -> helpTp()
                        }
                    } else send(p, "&cSem permissão.")
                }

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
            if (Bukkit.getPlayer(args[0]) != null && Bukkit.getOnlinePlayers().map { it.name.lowercase() }
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