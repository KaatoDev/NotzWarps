package notzwarps.events

import notzwarps.Main.Companion.wf
import notzwarps.managers.TpaM.clearRequests
import notzwarps.managers.TpaM.containRequest
import notzwarps.managers.TpaM.tpaHoldings
import notzwarps.managers.WarpM.warps
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinLeaveEv : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerJoin(e: PlayerJoinEvent) {
        if (e.player.hasPermission("notzwarp.vip")) {
            when (wf.config!!.getString("spawnVip").lowercase()) {
                "false" -> {}
                "true" -> {
                    if (warps.containsKey("spawnvip"))
                        e.player.teleport(warps["spawnvip"]!!.location)
                }
                else -> {
                    if (warps.containsKey(wf.config!!.getString("spawnVip")))
                        e.player.teleport(warps[wf.config!!.getString("spawnVip")]!!.location)
                }
            }
        }

        when (wf.config!!.getString("spawnToWarp").lowercase()) {
            "false" -> return
            "true" -> {
                if (warps.containsKey("spawn"))
                    e.player.teleport(warps["spawn"]!!.location)
            }
            else -> {
                if (warps.containsKey(wf.config!!.getString("spawnToWarp")))
                    e.player.teleport(warps[wf.config!!.getString("spawnToWarp")]!!.location)
            }
        }
    }

    @EventHandler
    fun playerLeave(e: PlayerQuitEvent) {
        if (containRequest(e.player) || tpaHoldings.containsKey(e.player))
            clearRequests(e.player)
    }
}