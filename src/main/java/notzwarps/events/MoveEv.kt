package notzwarps.events

import notzapi.utils.MessageU.send
import notzwarps.managers.TpaM.tpaTime
import notzwarps.managers.WarpM.warpTime
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class MoveEv : Listener {

    @EventHandler
    fun moveEvent(e: PlayerMoveEvent) {
        if (walk(e.from, e.to)) {
            if (tpaTime.containsKey(e.player)) {
                tpaTime.remove(e.player)
                send(e.player, "&cSeu TPA foi cancelado.")
            }

            if (warpTime.containsKey(e.player)) {
                warpTime.remove(e.player)
                send(e.player, "&cSeu teleport foi cancelado.")
            }
        }
    }

    private fun walk(from: Location, to: Location): Boolean {
        return from.x != to.x && from.y != to.y && from.z != to.z
    }
}