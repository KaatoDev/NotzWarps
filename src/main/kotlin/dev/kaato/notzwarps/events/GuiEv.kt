package dev.kaato.notzwarps.events

import dev.kaato.notzapi.utils.MenuU.equalsMenu
import dev.kaato.notzwarps.managers.WarpManager.teleport
import dev.kaato.notzwarps.managers.WarpManager.warpListItems
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GuiEv : Listener {

    @EventHandler
    fun menuEvent(e: InventoryClickEvent) {
        if (e.clickedInventory != null && equalsMenu("warpmenu", e.view.title) && e.currentItem != null) {
            e.isCancelled = true

            val p = e.whoClicked as Player
            val item = e.currentItem

            if (warpListItems().contains(item)) teleport(p, item)
        }
    }
}