package dev.kaato.notzwarps.managers

import dev.kaato.notzapi.utils.MessageU.Companion.c
import dev.kaato.notzapi.utils.MessageU.Companion.createHoverCMD
import dev.kaato.notzwarps.Main.Companion.messageU
import dev.kaato.notzwarps.Main.Companion.plugin
import dev.kaato.notzwarps.managers.WarpManager.delay
import dev.kaato.notzwarps.managers.WarpManager.delayPlayer
import dev.kaato.notzwarps.managers.WarpManager.runWarp
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable


object TpaManager : Runnable {
    private val instance: TpaManager = this

    val tpaHoldings = hashMapOf<Player, Player>()
    val tpaTime = hashMapOf<Player, Int>()

    fun sendTpaRequest(p: Player, target: Player) {
        if (containRequest(p) && tpaHoldings[p] == target) messageU.send(p, "alreadySent")
        else messageU.send(p, "requestTpa", target.name)

        tpaHoldings[p] = target
        sendHoverRequest(p, target)
    }

    private fun sendHoverRequest(p: Player, target: Player) {
        val txt = TextComponent(c("\n &eO player &6${p.name}&e lhe enviou um pedido de TPA. \n&e    Deseja aceitar?  "))

        txt.addExtra(createHoverCMD("&2&lAccept", arrayOf("&fAccepts TPA"), "/tpaccept", true))
        txt.addExtra(c(" &eor "))
        txt.addExtra(createHoverCMD("&c&lRefuse", arrayOf("&fRefuse TPA"), "/tpadeny", true))
        txt.addExtra(c("\n&r"))

        target.spigot().sendMessage(txt)
    }

    fun tpaccept(p: Player) {
        val from = tpaHoldings.keys.filter { tpaHoldings[it] == p }[0]

        teleportTpa(from, p)
    }

    fun tpaccept(p: Player, from: Player) {
        teleportTpa(from, p)
    }

    fun tpadeny(p: Player, isTarget: Boolean) {
        val player = if (isTarget) tpaHoldings.keys.filter { tpaHoldings[it] == p }[0] else p

        messageU.send(player, "refuseTpa1", if (isTarget) p.name else tpaHoldings[p]!!.name)
        messageU.send(if (isTarget) p else tpaHoldings[p]!!, "refuseTpa2",if (isTarget) tpaHoldings.keys.filter { tpaHoldings[it] == p }[0].name else p.name)

        tpaHoldings.remove(player)
    }

    private fun teleportTpa(p: Player, target: Player) {
        tpaHoldings.remove(p)
        messageU.send(target, "acceptTpa", p.name)

        if (!p.hasPermission("notzwarps.nodelay")) {

            tpaTime[p] = delayPlayer + 1
            messageU.send(p, "teleporting")

            object : BukkitRunnable() {
                override fun run() {
                    if (tpaTime.containsKey(p)) {
                        p.teleport(target)
                        messageU.send(p, "playerTp1", target.name)
                        messageU.send(target, "playerTp2", p.name)
                    }
                }
            }.runTaskLater(plugin, delay)

        } else {
            p.teleport(target)
            messageU.send(p, "playerTp1", target.name)
            messageU.send(target, "playerTp2", p.name)
        }
    }

    fun containRequest(p: Player): Boolean {
        return tpaHoldings.containsValue(p)
    }

    fun containRequest(p: Player, from: Player): Boolean {
        return tpaHoldings[from] == p
    }

    fun clearRequests(p: Player) {
        if (tpaHoldings.containsKey(p)) tpaHoldings.remove(p)
        if (tpaHoldings.values.contains(p)) for (pp in tpaHoldings.filterKeys { tpaHoldings[it] == p }.keys) tpaHoldings.remove(pp)
    }

    override fun run() {
        tpaTime.keys.forEach {
            tpaTime[it] = tpaTime[it]!! - 1
        }

        for (p in tpaTime.keys) if (tpaTime[p] == 0) tpaTime.remove(p)

        runWarp()
    }

    fun getInstance(): TpaManager {
        return instance
    }
}