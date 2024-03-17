package notzwarps.commands

import notzapi.utils.MessageU.c
import notzapi.utils.MessageU.send
import notzapi.utils.MessageU.sendHeader
import notzwarps.managers.WarpM
import notzwarps.managers.WarpM.alterLore
import notzwarps.managers.WarpM.editWarp
import notzwarps.managers.WarpM.removeWarp
import notzwarps.managers.WarpM.resetMenu
import notzwarps.managers.WarpM.setAutoSlot
import notzwarps.managers.WarpM.setWarp
import notzwarps.managers.WarpM.warpBlockedNames
import notzwarps.managers.WarpM.warpList
import notzwarps.managers.WarpM.warps
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class NWarpC : TabExecutor {
    private lateinit var p: Player

    override fun onCommand(sender: CommandSender?, cmd: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player)
            return false

        p = sender

        if (!p.hasPermission("notzwarps.admin")) {
            p.sendMessage("&cSem permissão.")
            return false
        }

        var warp = ""
        var w = false
        if (args!!.isNotEmpty() && warps.containsKey(args[0])) {
            warp = args[0]
            w = true
        }

        when (args.size) {
            0 -> help()

            1 -> when (args[0].lowercase()) {
                "list" -> send(p, warps.keys.toString())
                "resetmenu" -> resetMenu()
                "set" -> send(p, "&f/&enwarp set &f<&enome&f>")

                else -> if (w) helpWarps(warp)
                    else help()
            }

            2 -> if (w) when (args[1].lowercase()) {

                    "delete", "remove" -> {
                        removeWarp(warp)
                        send(p, "&eA warp &f$warp&e foi removida com sucesso.")
                    }

                    "get" -> {
                        p.inventory.addItem(warps[warp]!!.item)
                        send(p, "&eVocê revebeu o item da warp &f${warps[warp]!!.display}&e.")
                    }

                    "set" -> {
                        if (warpList().size < 31) {
                            setWarp(warp, p.location)
                            send(p, "&aA warp &f$warp&a foi criada com sucesso.")
                        } else send(p, "&cVocê atingiu o limite máximo de 30 (${warpList().size}) warps.")
                    }

                    "setmaterial" -> {
                        editWarp(warp, p.itemInHand.type)
                        send(p, "&eO material do item da warp &f$warp&e foi alterado com sucesso.")
                    }

                    "unsetslot" -> {
                        editWarp(warp, -1)
                        send(p, "&eA warp &f$warp&e teve seu slot resetado e não mais aparecerá no menu&a.")
                    }

                    else -> helpWarps(warp)
                }
            else when (args[0].lowercase()) {
                "autoslot" -> {
                    when (args[1].lowercase()) {
                        "true", "on" -> setAutoSlot(true)
                        "false", "off" -> setAutoSlot(false)

                        else -> send(p, "&cUtilize apenas TRUE/ON ou FALSE/OFF como argumento.")
                    }
                }

                "set" -> {
                    if (!warps.containsKey(args[1].lowercase())) {
                        if (warpBlockedNames(args[1])) {
                            setWarp(args[1].lowercase(), p.location)
                            send(p, "&aA warp &f${args[1].lowercase()}&a foi criada com sucesso.")
                        } else send(p, "&cEste nome de warp está bloqueado!")

                    } else {
                        setWarp(args[1].lowercase(), p.location)
                        send(p, "&eA nova localização da &f${args[1].lowercase()}&e foi setada com sucesso.")
                    }
                }

                "setlore" -> {
                    alterLore(listOf(args[1], args[2]))
                    send(p, "&eA lore padrão foi alterada para ${args[1]} ${args[2]}&e. (As warps existentes tiveram suas lores substituídas também)")
                }

                "spawntowarp" -> {
                    when (args[1].lowercase()) {
                        "true", "on" -> WarpM.setSpawnToWarp(true)
                        "false", "off" -> WarpM.setSpawnToWarp(false)

                        else -> if (warps.containsKey(args[1].lowercase())) {
                            WarpM.setSpawnToWarp(args[1].lowercase())
                            send(p, "&aA warp &f${args[1]}&a foi setada como Spawn padrão.")

                        } else send(p, "&cA warp &f${args[1]}&c não existe. (Ou então utilize TRUE/ON ou FALSE/OFF)")
                    }
                }
                "spawnvip" -> {
                    when (args[1].lowercase()) {
                        "true", "on" -> WarpM.setSpawnVip(true)
                        "false", "off" -> WarpM.setSpawnVip(false)

                        else -> if (warps.containsKey(args[1].lowercase())) {
                            WarpM.setSpawnVip(args[1].lowercase())
                            send(p, "&aA warp &f${args[1]}&a foi setada como Spawn VIP padrão.")

                        } else send(p, "&cA warp &f${args[1]}&c não existe. (Ou então utilize TRUE/ON ou FALSE/OFF)")
                    }
                }

                else -> help()
            }

            3 -> if (w) when (args[1].lowercase()) {
                "setdisplay" -> {
                    editWarp(warp, args[2])
                    send(p, "&eA warp &f$warp&e teve seu display alterado para &r${args[2]}&a.")
                }

                "setlore" -> {
                    editWarp(warp, args[2])
                    send(p, "&eA warp &f$warp&e teve a sua lore alterada&a.")
                }

                "setslot" -> {
                    try {
                        editWarp(warp, args[2].toInt())
                        send(p, "&eA warp &f$warp&e teve seu slot alterado para &f&l${args[2]}&a.")
                    } catch (e: java.lang.NumberFormatException) {
                        send(p, "&cÉ pra botar um número no slot, zézão.")
                    }
                }

                "setmaterial" -> {
                    try {
                        @Suppress("DEPRECATION")
                        if (Material.entries.map { it.name }.contains(args[2].uppercase())) {
                            editWarp(warp, Material.valueOf(args[2].uppercase()))
                            send(p, "&eO material do item da warp &f$warp&e foi alterado com sucesso.")

                        } else if (Material.entries.contains(ItemStack(args[2].toInt()).type)) {
                            editWarp(warp, ItemStack(args[2].toInt()).type)
                            send(p, "&eO material do item da warp &f$warp&e foi alterado com sucesso.")

                        } else send(p, "&cO material inserido é inválido.")

                    } catch (e: NumberFormatException) {
                        send(p, "&cO material inserido é inválido.")
                    }
                }

                else -> helpWarps(warp)
            } else {
                if (args[0] == "setlore") {
                    alterLore(listOf(args[1], args[2]))
                    send(p, "&eA lore padrão foi alterada para ${args[1]} ${args[2]}&e. (As warps existentes tiveram suas lores substituídas também)")

                } else help()
            }
            else -> if (w) {
                when (args[1]) {
                    "setdisplay" -> {
                        editWarp(warp, args.sliceArray(2 until args.size).joinToString(separator = " ", prefix = "", postfix = ""))
                        send(p, "&eA warp &f$warp&e teve seu display alterado para &r${warps[warp]!!.display}&a.")
                    }

                    "setlore" -> {
                        editWarp(warp, args.sliceArray(2 until args.size).toList())
                        send(p, "&eA lore da warp $warp foi alterada para ${args.sliceArray(2 until args.size).joinToString(separator = " ", prefix = "", postfix = "")}&e. (As warps existentes tiveram suas lores substituídas também)")
                    }

                    else -> helpWarps(warp)
                }
            } else help()
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): MutableList<String> {
        return if (p3!!.size == 1 && p3[0] == "remove")
            warps.keys.toMutableList()
        else Collections.emptyList()
    }

    private fun help() {
        sendHeader(p)
        p.sendMessage(c("&6(&f/&enwarp &7| &f/&enw&6)"))
        p.sendMessage(c("&f/&enw &f<&ewarp&f> &7- Para ver as opções de customização da warp."))
        p.sendMessage(c("&f/&enw autoslot &f<&eon&f/&eoff&f> &7- Habilita ou desabilita a auto distribuição das warps no menu."))
        p.sendMessage(c("&f/&enw list &7- Lista as warps que estão habilitadas."))
        p.sendMessage(c("&f/&enw resetmenu &7- Reseta o menu das warps."))
        p.sendMessage(c("&f/&enw set &f<&ewarp&f> &7- Seta a localização atual numa warp existente ou nova, criando-a."))
        p.sendMessage(c("&f/&enw setlore &f<&elore...&f> &7- Altera a lore padrão das warps. (Cuidado pois reescreve as existentes.)"))
        p.sendMessage(c("&f/&enw spawntowarp &f<&ewarp&f/&eon&f/&eoff&f> &7- Habilita, desabilita ou altera a warp de spawn padrão."))
        p.sendMessage(c("&f/&enw spawnvip &f<&ewarp&f/&eon&f/&eoff&f> &7- Habilita, desabilita ou altera a warp de spawn padrão dos players VIPs."))
        p.sendMessage("")
    }

    private fun helpWarps(warp: String) {
        sendHeader(p)
        p.sendMessage(c("&cUtilize: &f/&enwarp $warp&7 +"))
        p.sendMessage(c("&7+ &eget &7- Recebe o item da warp, a qual é setada no menu."))
        p.sendMessage(c("&7+ &eremove &7- Deleta uma warp."))
        p.sendMessage(c("&7+ &eset &7- Seta a localização atual na warp."))
        p.sendMessage(c("&7+ &esetdisplay <&edisplay...&f> &7- Altera o display do item de uma warp."))
        p.sendMessage(c("&7+ &esetlore <&elore...&f> &7- Altera a lore do item de uma warp."))
        p.sendMessage(c("&7+ &esetslot <&eslot&f> &7- Altera o slot padrão de uma warp no menu."))
        p.sendMessage(c("&7+ &esetMaterial <&ematerial&f> &7- Altera o material do item de uma warp no menu."))
        p.sendMessage(c("&7+ &eunsetslot &7- Resetará o slot da warp, não será mostrado no menu."))
        p.sendMessage("")
    }
}