package dev.kaato.notzwarps.events

import dev.kaato.notzapi.utils.MessageU.send
import dev.kaato.notzwarps.managers.TpaManager
import dev.kaato.notzwarps.managers.WarpManager
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class MoveEv : Listener {

    @EventHandler
    fun moveEvent(e: PlayerMoveEvent) {
        if (walk(e.from, e.to!!)) {
            if (TpaManager.tpaTime.containsKey(e.player)) {
                TpaManager.tpaTime.remove(e.player)
                send(e.player, "cancelTpa")
            }

            if (WarpManager.warpTime.containsKey(e.player)) {
                WarpManager.warpTime.remove(e.player)
                send(e.player, "cancelTp")
            }
        }
    }

    private fun walk(from: Location, to: Location): Boolean {
        return from.x != to.x && from.y != to.y && from.z != to.z
    }
}