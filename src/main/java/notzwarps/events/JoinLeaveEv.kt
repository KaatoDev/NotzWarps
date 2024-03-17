package notzwarps.events

import notzwarps.Main.Companion.wf
import notzwarps.managers.TpaM.clearRequests
import notzwarps.managers.TpaM.containRequest
import notzwarps.managers.TpaM.tpaHoldings
import notzwarps.managers.WarpM.warps
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinLeaveEv : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerJoin(e: PlayerJoinEvent) {
        teporter(e.player as Player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerDeath(e: PlayerDeathEvent) {
        teporter(e.entity as Player)
    }

    @EventHandler
    fun playerLeave(e: PlayerQuitEvent) {
        if (containRequest(e.player) || tpaHoldings.containsKey(e.player))
            clearRequests(e.player)
    }

    private fun teporter(p: Player) {
        if (p.hasPermission("notzwarp.vip")) {
            when (wf.config!!.getString("spawnVip").lowercase()) {
                "false" -> {}
                "true" -> {
                    if (warps.containsKey("spawnvip"))
                        p.teleport(warps["spawnvip"]!!.location)
                }
                else -> {
                    if (warps.containsKey(wf.config!!.getString("spawnVip")))
                        p.teleport(warps[wf.config!!.getString("spawnVip")]!!.location)
                }
            }
        }

        when (wf.config!!.getString("spawnToWarp").lowercase()) {
            "false" -> return
            "true" -> {
                if (warps.containsKey("spawn"))
                    p.teleport(warps["spawn"]!!.location)
            }
            else -> {
                if (warps.containsKey(wf.config!!.getString("spawnToWarp")))
                    p.teleport(warps[wf.config!!.getString("spawnToWarp")]!!.location)
            }
        }
    }
}