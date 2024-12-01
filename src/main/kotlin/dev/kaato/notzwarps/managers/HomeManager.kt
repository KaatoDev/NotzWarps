package dev.kaato.notzwarps.managers

import org.bukkit.Location
import org.bukkit.entity.Player

object HomeManager {
    data class Home(val player: Player, val homes: HashMap<String, Location>)

    val homes = hashMapOf<Player, Home>()

    fun teleportToHome(p: Player, home: String) {
        p.teleport(getHome(p, home))
    }

    fun teleportToHome(p: Player, owner: Player, home: String) {
        p.teleport(getHome(p, home))
    }

    fun getHome(p: Player, home: String): Location {
        return homes[p]!!.homes[home]!!
    }

    fun setHome(p: Player, home: String, loc: Location) {
        homes[p]!!.homes[home] = loc
    }

    fun remHome(p: Player, home: String) {
        homes[p]!!.homes.remove(home)
    }
}