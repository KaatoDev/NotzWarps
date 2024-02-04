package notzwarps.managers

import net.md_5.bungee.api.chat.TextComponent
import notzapi.utils.MessageU.c
import notzapi.utils.MessageU.createHoverCMD
import notzapi.utils.MessageU.send
import notzwarps.Main
import notzwarps.Main.Companion.cf
import org.bukkit.Bukkit
import org.bukkit.entity.Player


object TpaM : Runnable {
    private val instance: TpaM = this
    private val delay = cf.config!!.getInt("teleport-delay") * 20L

    val tpaHoldings = hashMapOf<Player, Player>()
    val tpaTime = hashMapOf<Player, Int>()

    fun sendTpaRequest(p: Player, target: Player) {
        if (containRequest(p) && tpaHoldings[p] == target)
            send(p, "&eVocê já enviou um tpa para este player")
        else send(p, "&eUm pedido de TPA foi enviado ao player &f${target.name}&e.")


        tpaHoldings[p] = target
        sendHoverRequest(p, target)
    }

    private fun sendHoverRequest(p: Player, target: Player) {
        val txt = TextComponent(c("\n&eO player &6${p.name}&e lhe enviou um pedido de TPA. \n&e  Desejas aceitar?  "))

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
        if (isTarget)
            tpaHoldings.remove(tpaHoldings.keys.filter { tpaHoldings[it] == p }[0])
        else tpaHoldings.remove(p)
    }

    private fun teleportTpa(p: Player, target: Player) {
        tpaHoldings.remove(p)
        send(target, "&eVocê aceitou o pedido de TPA de &f${p.name}&e.")

        if (!p.hasPermission("notzwarps.nodelay")) {
            tpaTime[p] = 4
            send(p, "&eVocê será teleportado em 3 segundos.")
            Bukkit.getServer().scheduler.runTaskLater(Main.plugin, {
                if (tpaTime.containsKey(p)) {
                    p.teleport(target)
                    send(p, "&eVocê foi teleportado para o player &a${target.name}&e.")
                    send(target, "&eO player &a${p.name}&e foi teleportado até você.")
                }
            }, delay)

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
        if (tpaHoldings.containsKey(p))
            tpaHoldings.remove(p)
        if (tpaHoldings.values.contains(p))
            for (pp in tpaHoldings.filterKeys { tpaHoldings[it] == p }.keys)
                tpaHoldings.remove(pp)
    }

    override fun run() {
        tpaTime.keys.forEach {
            tpaTime[it] = tpaTime[it]!! - 1
        }

        for (p in tpaTime.keys)
            if (tpaTime[p] == 0)
                tpaTime.remove(p)

        WarpM.run()
    }

    fun getInstance(): TpaM {
        return instance
    }
}