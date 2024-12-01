package dev.kaato.notzwarps.managers

import dev.kaato.notzapi.NotzAPI.Companion.plugin
import dev.kaato.notzapi.utils.MessageU.c
import dev.kaato.notzapi.utils.MessageU.createHoverCMD
import dev.kaato.notzapi.utils.MessageU.send
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
        if (containRequest(p) && tpaHoldings[p] == target) send(p, "&eVocê já enviou um tpa para este player")
        else send(p, "&eUm pedido de TPA foi enviado ao player &f${target.name}&e.")


        tpaHoldings[p] = target
        sendHoverRequest(p, target)
    }

    private fun sendHoverRequest(p: Player, target: Player) {
        val txt = TextComponent(c("\n &eO player &6${p.name}&e lhe enviou um pedido de TPA. \n&e    Desejas aceitar?  "))

        txt.addExtra(createHoverCMD("&2&lAceitar", arrayOf("&fAceita o TPA"), "/tpaccept", true))
        txt.addExtra(c(" &eou "))
        txt.addExtra(createHoverCMD("&c&lRecusar", arrayOf("&fRecusa o TPA"), "/tpadeny", true))
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

        send(player, "&eVocê recusou o pedido de tpa de &f${if (isTarget) p.name else tpaHoldings[p]!!.name}&e.")
        send(if (isTarget) p else tpaHoldings[p]!!, "&cO seu pedido de tpa para &f${if (isTarget) tpaHoldings.keys.filter { tpaHoldings[it] == p }[0].name else p.name}&c foi recusado.")

        tpaHoldings.remove(player)
    }

    private fun teleportTpa(p: Player, target: Player) {
        tpaHoldings.remove(p)
        send(target, "&aVocê aceitou o pedido de TPA de &f${p.name}&a.")

        if (!p.hasPermission("notzwarps.nodelay")) {

            tpaTime[p] = delayPlayer + 1
            send(p, "&eVocê será teleportado em 3 segundos.")

            object : BukkitRunnable() {
                override fun run() {
                    if (tpaTime.containsKey(p)) {
                        p.teleport(target)
                        send(p, "&eVocê foi teleportado para o player &a${target.name}&e.")
                        send(target, "&eO player &a${p.name}&e foi teleportado até você.")
                    }
                }
            }.runTaskLater(plugin, delay)

        } else {
            p.teleport(target)
            send(p, "&eVocê foi teleportado para o player &a${target.name}&e.")
            send(target, "&eO player &a${p.name}&e foi teleportado até você.")
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