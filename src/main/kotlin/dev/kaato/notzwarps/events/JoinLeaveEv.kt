package dev.kaato.notzwarps.events

import dev.kaato.notzwarps.Main.Companion.wf
import dev.kaato.notzwarps.managers.TpaManager
import dev.kaato.notzwarps.managers.WarpManager.containsWarp
import dev.kaato.notzwarps.managers.WarpManager.getWarpLoc
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
        teporter(e.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun playerDeath(e: PlayerDeathEvent) {
        teporter(e.entity)
    }

    @EventHandler
    fun playerLeave(e: PlayerQuitEvent) {
        if (TpaManager.containRequest(e.player) || TpaManager.tpaHoldings.containsKey(e.player)) TpaManager.clearRequests(e.player)
    }

    private fun teporter(p: Player) {
        if (p.hasPermission("notzwarp.vip")) {
            when (wf.config.getString("spawnVip")?.lowercase()) {
                "false" -> {}
                "true" -> {
                    if (containsWarp("spawnvip")) p.teleport(getWarpLoc("spawnvip")!!)
                }

                else -> {
                    if (containsWarp(wf.config.getString("spawnVip"))) p.teleport(getWarpLoc(wf.config.getString("spawnVip"))!!)
                }
            }
        }

        when (wf.config.getString("spawnToWarp")?.lowercase()) {
            "false" -> return
            "true" -> {
                if (containsWarp("spawn")) p.teleport(getWarpLoc("spawn"))
            }

            else -> {
                if (containsWarp(wf.config.getString("spawnToWarp"))) p.teleport(getWarpLoc(wf.config.getString("spawnToWarp")))
            }
        }
    }
}