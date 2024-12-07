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
            p.sendMessage("&cSem permissão.")
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
                "setdisplay" -> setDisplayCMD(p, w, args[2]?:"")
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
        val utilize = "&eUtilize &f/&enw &e"
        val help = """
            &7+ &f<&ewarp&f> &7- Para ver as opções de customização da warp.
            &7+ &eautoslot &f<&eon&f/&eoff&f> &7- Habilita ou desabilita a auto distribuição das warps no menu.
            &7+ &elist &7- Lista as warps que estão habilitadas.
            &7+ &eresetmenu &7- Reseta o menu das warps.
            &7+ &eset &f<&ewarp&f> &7- Seta a localização atual numa warp existente ou nova, criando-a.
            &7+ &esetlore &f<&elore...&f> &7- Altera a lore padrão das warps. (Cuidado pois reescreve as existentes.)
            &7+ &espawntowarp &f<&ewarp&f/&eon&f/&eoff&f> &7- Habilita, desabilita ou altera a warp de spawn padrão.
            &7+ &espawnvip &f<&ewarp&f/&eon&f/&eoff&f> &7- Habilita, desabilita ou altera a warp de spawn padrão dos players VIPs.
        """.trimIndent()

        if (a.isNullOrEmpty()) sendHeader(p, "$utilize &7+\n$help")
        else if (!warp.isNullOrEmpty()) helpWarps(p, a, warp)
        else sendHeader(
            p, utilize + when (a[0]) {
                "autoslot" -> "autoslot &f<&eon&f/&eoff&f> &7- Habilita ou desabilita a auto distribuição das warps no menu."
                "list" -> "list &7- Lista as warps que estão habilitadas."
                "resetmenu" -> "resetmenu &7- Reseta o menu das warps."
                "set" -> "set &f<&ewarp&f> &7- Seta a localização atual numa warp existente ou nova, criando-a."
                "setlore" -> "setlore &f<&elore...&f> &7- Altera a lore padrão das warps. (Cuidado pois reescreve as existentes.)"
                "spawntowarp" -> "spawntowarp &f<&ewarp&f/&eon&f/&eoff&f> &7- Habilita, desabilita ou altera a warp de spawn padrão."
                "spawnvip" -> "spawnvip &f<&ewarp&f/&eon&f/&eoff&f> &7- Habilita, desabilita ou altera a warp de spawn padrão dos players VIPs."

                else -> "&7+\n$help"
            }
        )
    }

    private fun helpWarps(p: Player, a: List<String>, warp: String) {
        val utilize = "&eUtilize &f/&enw &a$warp &e"
        val help = """
            &7+ &eget &7- Recebe o item da warp, a qual é setada no menu.
            &7+ &eremove &7- Deleta uma warp.
            &7+ &eset &7- Seta a localização atual na warp.
            &7+ &esetdisplay &f<&edisplay...&f> &7- Altera o display do item de uma warp.
            &7+ &esetlore &f<&elore...&f> &7- Altera a lore do item de uma warp.
            &7+ &esetslot &f<&eslot&f> &7- Altera o slot padrão de uma warp no menu.
            &7+ &esetMaterial &f<&ematerial&f> &7- Altera o material do item de uma warp no menu.
            &7+ &eunsetslot &7- Resetará o slot da warp, não será mostrado no menu.
        """.trimIndent()

        if (a.size == 1) sendHeader(p, "$utilize &7+\n$help")
        else sendHeader(
            p, utilize + when (a[1]) {
                "get" -> "get &7- Recebe o item da warp, a qual é setada no menu."
                "remove" -> "remove &7- Deleta uma warp."
                "set" -> "set &7- Seta a localização atual na warp."
                "setdisplay" -> "setdisplay &f<&edisplay...&f> &7- Altera o display do item de uma warp."
                "setlore" -> "setlore &f<&elore...&f> &7- Altera a lore do item de uma warp."
                "setslot" -> "setslot &f<&eslot&f> &7- Altera o slot padrão de uma warp no menu."
                "setMaterial" -> "setMaterial &f<&ematerial&f> &7- Altera o material do item de uma warp no menu."
                "unsetslot" -> "unsetslot &7- Resetará o slot da warp, não será mostrado no menu."

                else -> "&7+\n$help"
            }
        )
    }
}