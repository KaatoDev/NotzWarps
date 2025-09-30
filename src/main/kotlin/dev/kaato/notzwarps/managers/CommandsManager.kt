package dev.kaato.notzwarps.managers

import dev.kaato.notzapi.utils.MessageU.send
import dev.kaato.notzwarps.managers.WarpManager.alterLore
import dev.kaato.notzwarps.managers.WarpManager.containsWarp
import dev.kaato.notzwarps.managers.WarpManager.editWarp
import dev.kaato.notzwarps.managers.WarpManager.getWarpDisplay
import dev.kaato.notzwarps.managers.WarpManager.getWarpIcon
import dev.kaato.notzwarps.managers.WarpManager.removeWarp
import dev.kaato.notzwarps.managers.WarpManager.setAutoSlot
import dev.kaato.notzwarps.managers.WarpManager.setSpawnToWarp
import dev.kaato.notzwarps.managers.WarpManager.setSpawnVip
import dev.kaato.notzwarps.managers.WarpManager.setWarp
import dev.kaato.notzwarps.managers.WarpManager.setWarpLore
import dev.kaato.notzwarps.managers.WarpManager.warpList
import org.bukkit.Material
import org.bukkit.entity.Player

object CommandsManager {
    fun listWarpCMD(p: Player) {
        send(p, warpList().joinToString(prefix = "&e", separator = "&f, &e", postfix = "&f."))
    }

    fun removeWarpCMD(p: Player, w: String) {
        removeWarp(w)
        send(p, "removeWarp", w)
    }

    fun getWarpIconCMD(p: Player, w: String) {
        val icon = getWarpIcon(w)
        val display = getWarpDisplay(w)

        if (icon != null) {
            p.inventory.addItem(icon)
            send(p, "&eYou have received the item of the warp &f$display&e.")
        } else warpNotFound(p)
    }

    fun setWarpLocCMD(p: Player, w: String) {
        if (containsWarp(w) || (!containsWarp(w) && warpList().size < 31)) {
            setWarp(w, p.location)
            send(p, "&aThe &fwarp $w &ahas been successfully created.")
        } else send(p, "&cYou have reached the maximum limit of 30 (${warpList().size}) warps.")

    }

    fun setwarpIconCMD(p: Player, w: String, materialStr: String? = null) {
        val editWarp = if (materialStr != null) {
            var material = Material.entries.find { it.name == materialStr.uppercase() } ?: Material.STONE
            editWarp(w, material)
        } else editWarp(w, p.itemInHand.type)

        if (editWarp) send(p, "&eThe material of the item of &fwarp $w &ehas been successfully changed.")
        else warpNotFound(p)
    }

    fun setwarpEnchantCMD(p: Player, w: String, enchant: String) {
        
        
        val editWarp = when (enchant) {
            "true", "on" -> editWarp(w, true)
            "false", "off" -> editWarp(w, false)
            else -> {
                send(p, "&cUse only TRUE/ON or FALSE/OFF as argument.")
                return
            }
        }

        if (editWarp) send(p, "&eThe enchantment of the item of &fwarp $w &ehas been successfully changed.")
        else warpNotFound(p)
    }

    fun unsetWarpSlotCMD(p: Player, w: String) {
        editWarp(w, -1)
        send(p, "&eThe &fwarp $w &ehas had its slot reset and will no longer appear in the menu&a.")

    }

    fun autoSlotCMD(p: Player, string: String) {
        when (string) {
            "true", "on" -> {
                if (setAutoSlot(true)) send(p, "&eThe autoslot has been &aenabled&e.")
                else send(p, "&cAuto-slot is already &aenabled&c.")
            }

            "false", "off" -> {
                if (setAutoSlot(false)) send(p, "&eThe autoslot has been &adisabled&e.")
                else send(p, "&cAuto-slot is already &adisabled&c.")
            }

            else -> send(p, "&cUse only TRUE/ON or FALSE/OFF as argument.")
        }
    }

    fun setWarpCMD(p: Player, warp: String) {
        when (setWarp(warp, p.location)) {
            true -> send(p, "&aThe &fwarp $warp&a has been successfully created.")
            false -> send(p, "&eThe new location of &f$warp&e has been successfully set.")
            null -> send(p, "&cThis warp name is unavailable!")
        }
    }

    fun setWarpLoreCMD(p: Player, lore1: String, lore2: String? = null) {
        val lore = mutableListOf(lore1)
        if (!lore2.isNullOrEmpty()) lore.add(lore2)
        alterLore(lore)
        send(p, "&eThe default lore has been changed to ${lore.joinToString(separator = " ")}&e. (Existing warps have had their lore replaced as well)")
    }

    fun spawnToWarpCMD(p: Player, value: String) {
        when (value) {
            "true", "on" -> setSpawnToWarp(true)
            "false", "off" -> setSpawnToWarp(false)

            else -> if (containsWarp(value)) {
                setSpawnToWarp(value)
                send(p, "&aThe warp &f${value}&a has been set as the default Spawn.")

            } else send(p, "&cThe warp &f${value}&c does not exist. (Or use ON/OFF)")
        }

    }

    fun spawnVipCMD(p: Player, value: String) {
        when (value) {
            "true", "on" -> setSpawnVip(true)
            "false", "off" -> setSpawnVip(false)

            else -> if (containsWarp(value)) {
                setSpawnVip(value)
                send(p, "&aThe warp &f${value}&a has been set as the default VIP Spawn.")

            } else send(p, "&cThe warp &f${value}&c does not exist. (Or use TRUE/ON or FALSE/OFF)")
        }

    }

    fun setDisplayCMD(p: Player, w: String, display: String) {
        if (editWarp(w, display)) send(p, "&eThe &fwarp $w &ehad its display changed to &r$display&e.")
        else warpNotFound(p)

    }

    fun setDisplayCMD(p: Player, w: String, displayArr: List<String>) {
        val display = displayArr.joinToString(separator = " ")
        if (editWarp(w, display)) send(p, "&eThe &fwarp $w &ehad its display changed to &r$display&e.")
        else warpNotFound(p)

    }

    fun setLoreCMD(p: Player, w: String, display: String) {
        if (editWarp(w, display)) send(p, "&eThe &fwarp $w &ehad its lore changed.")
        else warpNotFound(p)
    }

    fun setLoreCMD(p: Player, w: String, lore: List<String>) {
        if (setWarpLore(w, lore)) send(p, "&eThe &fwarp $w &ehad its lore changed.")
        else warpNotFound(p)
    }

    fun setSlotCMD(p: Player, w: String, slotStr: String) {
        var slot = 0

        try {
            slot = slotStr.toInt()
        } catch (e: NumberFormatException) {
            send(p, "&cYou should put a number in the slot.")
        }

        if (editWarp(w, slot)) send(p, "&eThe &fwarp $w &ehad its slot changed to &f&l${slotStr}&e.")
        else warpNotFound(p)
    }

    private fun warpNotFound(p: Player) {
        send(p, "warpNotFound2")
    }

}