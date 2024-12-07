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
        send(p, "&eA warp &f$w&e foi removida com sucesso.")

    }

    fun getWarpIconCMD(p: Player, w: String) {
        val icon = getWarpIcon(w)
        val display = getWarpDisplay(w)

        if (icon != null) {
            p.inventory.addItem(icon)
            send(p, "&eVocê revebeu o item da warp &f$display&e.")
        } else warpNotFound(p)
    }

    fun setWarpLocCMD(p: Player, w: String) {
        if (containsWarp(w) || (!containsWarp(w) && warpList().size < 31)) {
            setWarp(w, p.location)
            send(p, "&aA warp &f$w&a foi criada com sucesso.")
        } else send(p, "&cVocê atingiu o limite máximo de 30 (${warpList().size}) warps.")

    }

    fun setwarpIconCMD(p: Player, w: String, materialStr: String? = null) {
        val editWarp = if (materialStr != null) {
            var material = Material.entries.find { it.name == materialStr.uppercase() } ?: Material.STONE
            editWarp(w, material)
        } else editWarp(w, p.itemInHand.type)

        if (editWarp) send(p, "&eO material do item da warp &f$w &efoi alterado com sucesso.")
        else warpNotFound(p)
    }

    fun unsetWarpSlotCMD(p: Player, w: String) {
        editWarp(w, -1)
        send(p, "&eA warp &f$w&e teve seu slot resetado e não mais aparecerá no menu&a.")

    }

    fun autoSlotCMD(p: Player, string: String) {
        when (string) {
            "true", "on" -> {
                if (setAutoSlot(true)) send(p, "&eO autoslot foi &ahabilitado&e.")
                else send(p, "&cO autoslot já está &adesabilitado&e.")
            }

            "false", "off" -> {
                if (setAutoSlot(false)) send(p, "&eO autoslot foi &adehabilitado&e.")
                else send(p, "&cO autoslot já está &adesabilitado&e.")
            }

            else -> send(p, "&cUtilize apenas TRUE/ON ou FALSE/OFF como argumento.")
        }
    }

    fun setWarpCMD(p: Player, warp: String) {
        when (setWarp(warp, p.location)) {
            true -> send(p, "&aA warp &f$warp&a foi criada com sucesso.")
            false -> send(p, "&eA nova localização da &f$warp&e foi setada com sucesso.")
            null -> send(p, "&cEste nome de warp está bloqueado!")
        }
    }

    fun setWarpLoreCMD(p: Player, lore1: String, lore2: String? = null) {
        val lore = mutableListOf(lore1)
        if (!lore2.isNullOrEmpty()) lore.add(lore2)
        alterLore(lore)
        send(p, "&eA lore padrão foi alterada para ${lore.joinToString(separator = " ")}&e. (As warps existentes tiveram suas lores substituídas também)")
    }

    fun spawnToWarpCMD(p: Player, value: String) {
        when (value) {
            "true", "on" -> setSpawnToWarp(true)
            "false", "off" -> setSpawnToWarp(false)

            else -> if (containsWarp(value)) {
                setSpawnToWarp(value)
                send(p, "&aA warp &f${value}&a foi setada como Spawn padrão.")

            } else send(p, "&cA warp &f${value}&c não existe. (Ou então utilize ON/OFF)")
        }

    }

    fun spawnVipCMD(p: Player, value: String) {
        when (value) {
            "true", "on" -> setSpawnVip(true)
            "false", "off" -> setSpawnVip(false)

            else -> if (containsWarp(value)) {
                setSpawnVip(value)
                send(p, "&aA warp &f${value}&a foi setada como Spawn VIP padrão.")

            } else send(p, "&cA warp &f${value}&c não existe. (Ou então utilize TRUE/ON ou FALSE/OFF)")
        }

    }

    fun setDisplayCMD(p: Player, w: String, display: String) {
        if (editWarp(w, display)) send(p, "&eA warp &f$w&e teve seu display alterado para &r${display}&a.")
        else warpNotFound(p)

    }

    fun setDisplayCMD(p: Player, w: String, displayArr: List<String>) {
        val display = displayArr.joinToString(separator = " ")
        if (editWarp(w, display)) send(p, "&eA warp &f$w&e teve seu display alterado para &r${display}&a.")
        else warpNotFound(p)

    }

    fun setLoreCMD(p: Player, w: String, display: String) {
        if (editWarp(w, display)) send(p, "&eA warp &f$w&e teve a sua lore alterada&a.")
        else warpNotFound(p)
    }

    fun setLoreCMD(p: Player, w: String, lore: List<String>) {
        if (setWarpLore(w, lore)) send(p, "&eA warp &f$w&e teve a sua lore alterada&a.")
        else warpNotFound(p)
    }

    fun setSlotCMD(p: Player, w: String, slotStr: String) {
        var slot = 0

        try {
            slot = slotStr.toInt()
        } catch (e: NumberFormatException) {
            send(p, "&cÉ pra botar um número no slot, zézão.")
        }

        if (editWarp(w, slot)) send(p, "&eA warp &f$w&e teve seu slot alterado para &f&l${slotStr}&a.")
        else warpNotFound(p)
    }

    private fun warpNotFound(p: Player) {
        send(p, "&cEsta warp não foi encontrada.")
    }

}