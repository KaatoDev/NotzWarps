package notzwarps.events

import notzapi.utils.MenuU.equalsMenu
import notzwarps.managers.WarpM.teleport
import notzwarps.managers.WarpM.warps
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GuiEv : Listener {

    @EventHandler
    fun menuEvent(e: InventoryClickEvent) {
        if (e.clickedInventory != null && equalsMenu("warpmenu", e.clickedInventory.title) && e.currentItem != null) {
            e.isCancelled = true

            val p = e.whoClicked as Player
            val item = e.currentItem

            if (warps.values.map { it.item }.contains(item))
                teleport(p, item)
        }
    }
}