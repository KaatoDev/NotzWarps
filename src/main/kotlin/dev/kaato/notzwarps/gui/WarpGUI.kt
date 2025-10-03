package dev.kaato.notzwarps.gui

import dev.kaato.notzapi.apis.NotzGUI
import dev.kaato.notzwarps.Main.Companion.cf
import dev.kaato.notzwarps.Main.Companion.wf
import dev.kaato.notzwarps.managers.WarpManager.getWarpsSlot

class WarpGUI {
    val menu: NotzGUI

    init {
        val rows = if (!wf.config.getBoolean("autoSlot")) {
            wf.config.getInt("rows").coerceAtMost(6)
        } else if (getWarpsSlot().size % 5 == 0) getWarpsSlot().size / 5 + 2 else getWarpsSlot().size / 5 + 3

        menu = NotzGUI(null, rows, "warpmenu", cf.config.getString("titleGUI"))
        menu()
    }

    private fun menu() {
        menu.setPanel(0, false)
        menu.setPanel(cf.config.getInt("panelGlass")?:0, true)

        if (wf.config.getBoolean("autoSlot")) {
            val wps = getWarpsSlot()

            val slots = when (wps.size) {
                2 -> "11 15"
                3 -> "11 13 15"
                4 -> "10 12 14 16"
                5 -> "11 12 13 15 16"
                6 -> "10 12 14 19 21 23"
                7 -> "10 12 14 16 19 21 23"
                8 -> "10 12 14 16 19 21 23 25"
                9 -> "10 11 13 15 16 19 21 23 25"
                10 -> "10 11 13 15 16 19 21 22 24 25"
                11 -> "10 12 14 16 20 22 24 28 30 32 34"
                12 -> "10 12 14 16 19 21 23 25 28 30 32 34"
                13 -> "10 12 14 16 19 21 22 23 25 28 30 32 34"
                14 -> "10 11 13 15 16 19 21 23 25 28 29 31 33 34"
                15 -> "10 11 13 15 16 19 20 22 24 25 28 29 31 33 34"
                16 -> "10 11 13 15 16 19 20 21 23 24 25 28 29 31 33 34"
                17 -> "10 11 12 14 15 16 19 20 22 24 25 28 29 30 32 33 34"
                else -> "13"
            }.split(" ")

            var a = 0

            if (wps.size == 1) menu.setItem(13, wps[0].item)
            else if (slots.size == 1) wps.forEach { menu.setItem(it.slot, it.name) }
            else wps.forEach { menu.setItem(slots[a++].toInt(), it.item) }

        } else if (getWarpsSlot().isNotEmpty()) getWarpsSlot().forEach { menu.setItem(it.slot, it.item) }
    }
}