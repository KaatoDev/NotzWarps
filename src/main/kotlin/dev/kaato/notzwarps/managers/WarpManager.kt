package dev.kaato.notzwarps.managers

import dev.kaato.notzapi.NotzAPI.Companion.plugin
import dev.kaato.notzapi.apis.NotzItems.buildItem
import dev.kaato.notzapi.utils.MessageU.c
import dev.kaato.notzapi.utils.MessageU.send
import dev.kaato.notzwarps.Main.Companion.cf
import dev.kaato.notzwarps.Main.Companion.itemM
import dev.kaato.notzwarps.Main.Companion.phM
import dev.kaato.notzwarps.Main.Companion.warpGUI
import dev.kaato.notzwarps.Main.Companion.wf
import dev.kaato.notzwarps.gui.WarpGUI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

object WarpManager {
    data class Warp(val name: String, var display: String, var location: Location, var slot: Int, var item: ItemStack)

    val delayPlayer: Int = cf.config.getInt("teleport-delay")
    val delay = delayPlayer * 20L

    private var warps = HashMap<String, Warp>()
    val warpTime = hashMapOf<Player, Int>()

    fun containsWarp(warp: String): Boolean {
        return warps.containsKey(warp)
    }

    fun setWarp(warp: String, location: Location): Boolean? {
        if (warps.containsKey(warp)) return null
        if (warpBlockedNames(warp)) return false

        createWarp(warp, location)

        return true
    }

    fun getWarpIcon(w: String): ItemStack? {
        return warps[w]?.item?.clone()
    }

    fun getWarpDisplay(w: String): String? {
        return warps[w]?.display
    }

    fun getWarpLoc(w: String): Location? {
        return warps[w]?.location
    }

    fun runWarp() {
        warpTime.keys.forEach {
            warpTime[it] = warpTime[it]!! - 1
        }

        for (p in warpTime.keys) if (warpTime[p] == 0) warpTime.remove(p)
    }

    /**
     * @param player O Player que será teleportado.
     * @param warpName Nome da warp de destino.
     */
    fun teleport(player: Player, warpName: String) {
        if (warpName != "spawn" && !player.hasPermission("notzwarps.warp.$warpName")) {
            send(player, "&cVocê não possui permissão para adentrar nesta warp!")
            return
        }

        val warp = warps[warpName]!!

        if (warpTime.containsKey(player)) {
            send(player, "&eJá há uma solicitação de teleport para warp em andamento.")
            return
        }

        if (!player.hasPermission("notzwarps.nodelay")) {

            warpTime[player] = delayPlayer + 1
            send(player, "&eVocê será teleportado em 3 segundos.")

            object : BukkitRunnable() {
                override fun run() {
                    if (warpTime.containsKey(player)) {
                        player.teleport(warp.location)
                        send(player, "&eVocê foi teleportado para a &lwarp ${warp.display}&e.")
                    }
                }
            }.runTaskLater(plugin, delay)

        } else {
            player.teleport(warp.location)
            send(player, "&eVocê foi teleportado para a &lwarp ${warp.display}&e.")
        }
    }

    /**
     * @param player O Player que será teleportado.
     * @param item Item da warp de destino.
     */
    fun teleport(player: Player, item: ItemStack) {
        teleport(player, warps.values.find { it.item == item }!!.name)
    }

    fun getWarpsSlot(): List<Warp> {
        return warps.values.filter { it.slot >= 0 }.toList().sortedBy { it.slot }
    }

    /**
     * Irá apenas setar o Location na warp caso exista ou então criará uma nova.
     * @param warp Nome da warp a ser setada.
     * @param location Local da warp a ser setada.
     */
    private fun createWarp(warp: String, location: Location) {

        val loc = location.clone()
        loc.pitch = 0F
        loc.yaw = (location.yaw / 45 - 0.5).toInt() * 45F
        if (location.x.toInt() >= 0) loc.x = location.x.toInt().toDouble() + 0.5
        else loc.x = location.x.toInt().toDouble() - 0.5
        loc.y = location.y.toInt().toDouble() + 0.1
        if (location.z.toInt() >= 0) loc.z = location.z.toInt().toDouble() + 0.5
        else loc.z = location.z.toInt().toDouble() - 0.5

        if (!warpList().contains(warp)) {
            warps[warp] = Warp(warp, c("&e&l$warp"), loc, 13, buildItemWarp(warp, true))

            val list = warpList().toMutableList()
            list.add(warp)
            wf.config.set("warpList", list)
            wf.config.set("warps.$warp.slot", -1)
            wf.config.set("warps.$warp.display", warps[warp]!!.display)

            if (warps[warp]!!.slot >= 0) resetMenu()

        } else if (!warps.containsKey(warp)) {
            warps[warp] = Warp(warp, c("&e&l$warp"), loc, 13, buildItemWarp(warp, true))
            wf.config.set("warps.$warp.slot", -1)
            wf.config.set("warps.$warp.display", warps[warp]!!.display)

            if (warps[warp]!!.slot >= 0) resetMenu()

        } else warps[warp]!!.location = loc

        wf.config.set("warps.$warp.location.world", warps[warp]!!.location.world?.name)
        wf.config.set("warps.$warp.location.x", warps[warp]!!.location.x)
        wf.config.set("warps.$warp.location.y", warps[warp]!!.location.y)
        wf.config.set("warps.$warp.location.z", warps[warp]!!.location.z)
        wf.config.set("warps.$warp.location.yaw", warps[warp]!!.location.yaw)
        wf.config.set("warps.$warp.location.pitch", warps[warp]!!.location.pitch)
        wf.saveConfig()

        phM.addPlaceholder("{${warp}}", warps[warp]!!.display)
        itemM.addItem(warp)
    }

    /**
     * @param warp Nome da warp a ser deletada.
     */
    fun removeWarp(warp: String) {
        warps.remove(warp)
        wf.config.set("warps.$warp", null)

        val l = warpList().toMutableList()
        l.remove(warp)

        wf.config.set("warpList", l)
        wf.saveConfig()

        resetMenu()
    }

    /**
     * Altera o DisplayName de uma warp existente.
     * @param warp Nome da warp a ser editada.
     * @param displayName O novo DisplayName da warp.
     */
    fun editWarp(warp: String, displayName: String): Boolean {
        val w = warps[warp] ?: return false
        val display = c(displayName)
        w.display = display
        phM.addPlaceholder(warp, display)

        wf.config.set("warps.$warp.display", display)
        wf.saveConfig()

        itemM.addItem(warp)
        w.item = buildItemWarp(warp, false)
        resetMenu()

        return true
    }

    /**
     * Altera o slot de uma warp existente.
     * @param warp Nome da warp a ser editada.
     * @param slot O novo slot da warp no menu.
     */
    fun editWarp(warp: String, slot: Int): Boolean {
        val w = warps[warp] ?: return false

        w.slot = slot
        wf.config.set("warps.$warp.slot", slot)
        wf.saveConfig()

        resetMenu()
        return true
    }

    /**
     * Altera o item de uma warp existente.
     * @param warp Nome da warp a ser editada.
     * @param material O novo material do item da warp no menu.
     */
    fun editWarp(warp: String, material: Material): Boolean {
        val w = warps[warp] ?: return false

        w.item = buildItemWarp(warp, false)
        wf.config.set("warps.$warp.item.material", material.name)
        wf.saveConfig()

        resetMenu()
        return true
    }

    /**
     * Altera o item de uma warp existente.
     * @param warp Nome da warp a ser editada.
     * @param lore A nova lore do item da warp no menu.
     */
    fun setWarpLore(warp: String, lore: List<String>): Boolean {
        if (!warps.containsKey(warp)) return false

        wf.config.set("warps.$warp.item.lore", lore)
        wf.saveConfig()

        resetMenu()
        return true
    }

    /**
     * Altera o item de uma warp existente.
     * @param lore A nova lore do item da warp no menu.
     */
    fun alterLore(lore: List<String>) {
        warpList().forEach {
            wf.config.set("warps.$it.item.lore", lore)
        }
        wf.saveConfig()
        warps.keys.forEach { warps[it]!!.item = buildItemWarp(it, false) }

        resetMenu()
    }

    /**
     * @param autoSlot Desativa ou habilita o autoSlot.
     */
    fun setAutoSlot(autoSlot: Boolean): Boolean {
        if (wf.config.getBoolean("autoSlot") == autoSlot) return false

        wf.config.set("autoSlot", autoSlot)
        wf.saveConfig()

        resetMenu()
        return true
    }

    /**
     * @param spawnToWarp Desativa ou habilita o autoSpawn.
     */
    fun setSpawnToWarp(spawnToWarp: Boolean) {
        wf.config.set("spawnToWarp", spawnToWarp)
        wf.saveConfig()
    }

    /**
     * @param warp Nome da warp a ser teleportado ao entrar.
     */
    fun setSpawnToWarp(warp: String) {
        wf.config.set("spawnToWarp", warp)
        wf.saveConfig()
    }

    /**
     * @param warp Nome da warp a ser teleportado ao vip entrar.
     */
    fun setSpawnVip(warp: String) {
        wf.config.set("spawnVip", warp)
        wf.saveConfig()
    }

    /**
     * @param spawnVip Desativa ou habilita o spawnVip.
     */
    fun setSpawnVip(spawnVip: Boolean) {
        wf.config.set("spawnVip", spawnVip)
        wf.saveConfig()
    }

    /**
     * @return Lista de Warps salvas na config.
     */
    fun warpList(): List<String> {
        return wf.config.getStringList("warpList").map { it.lowercase() }
    }

    /**
     * @return Lista de Warps salvas na config.
     */
    fun warpListItems(): List<ItemStack> {
        return warps.values.map(Warp::item)
    }

    fun resetMenu() {
        warpGUI = WarpGUI()
    }

    fun warpBlockedNames(name: String): Boolean {
        return arrayOf("autoslot", "list", "resetmenu", "spawntowarp", "get", "remove", "set", "setdisplay", "setslot", "setMaterial", "unsetslot").contains(name)
    }

    /**
     * Carrega todas as warps existentes nos arquivos.
     */
    fun load() {
        val warpsList = mutableListOf<String>()
        val warpsListOff = mutableListOf<String>()

        warpList().forEach {
            if (wf.config.contains("warps.$it.location.world")) {

                val wr = Bukkit.getWorld(wf.config.getString("warps.$it.location.world") ?: "")

                if (wr != null) warpsList.add(it)
                else warpsListOff.add(it)
            } else {
                val l = warpList().toMutableList()
                l.remove(it)
                wf.config.set("warpList", l)
                wf.saveConfig()
            }
        }

        if (warpsList.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(c("&cNenhuma das warps foram carregadas pois não existe os mundos requisitados."))
            return
        } else if (warpsListOff.isNotEmpty()) warpsListOff.forEach {
            Bukkit.getConsoleSender().sendMessage(c("&cA warp &f$it&c não pôde ser carregada pois o mundo &f${wf.config.getString("warps.$it.location.world")}&c não existe."))
        }

        warpsList.forEach {
            val display = c(wf.config.getString("warps.$it.display") ?: "")

            warps[it] = Warp(
                it, display, getLoc(it), wf.config.getInt("warps.$it.slot"), buildItemWarp(it, false)
            )

            phM.addPlaceholder("{${it}}", warps[it]!!.display)
            itemM.addItem(it)
        }
    }

    private fun getLoc(warp: String): Location {
        return Location(
            Bukkit.getWorld(wf.config.getString("warps.$warp.location.world") ?: ""),
            wf.config.getDouble("warps.$warp.location.x"),
            wf.config.getDouble("warps.$warp.location.y"),
            wf.config.getDouble("warps.$warp.location.z"),
            wf.config.getDouble("warps.$warp.location.yaw").toFloat(),
            wf.config.getDouble("warps.$warp.location.pitch").toFloat()
        )
    }

    private fun buildItemWarp(warp: String, create: Boolean): ItemStack {
        var item: ItemStack

        val ender_eye = Material.entries.find { it.name.contains("ENDER") && it.name.contains("EYE") } ?: Material.STONE

        if (!wf.config.contains("warps.$warp.item.material") || create) {
            item = buildItem(ender_eye, "&e&l$warp", listOf("&7&oClique para ir", "&7&oaté a warp."), false)
            wf.config.set("warps.$warp.item.material", ender_eye.name)
            wf.config.set("warps.$warp.item.enchanted", false)
            wf.config.set("warps.$warp.item.lore", listOf("&7&oClique para ir", "&7&oaté a warp."))
            wf.saveConfig()

        } else item = buildItem(
            Material.valueOf(wf.config.getString("warps.$warp.item.material")?.uppercase() ?: ""), wf.config.getString("warps.$warp.display") ?: "", wf.config.getStringList("warps.$warp.item.lore"), wf.config.getBoolean("warps.$warp.item.enchanted")
        )


        return item
    }
}