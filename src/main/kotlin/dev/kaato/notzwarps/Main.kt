package dev.kaato.notzwarps

import dev.kaato.notzapi.NotzAPI
import dev.kaato.notzapi.NotzAPI.Companion.itemManager
import dev.kaato.notzapi.NotzAPI.Companion.messageManager
import dev.kaato.notzapi.NotzAPI.Companion.placeholderManager
import dev.kaato.notzapi.apis.NotzYAML
import dev.kaato.notzapi.managers.ItemManager
import dev.kaato.notzapi.managers.MessageManager
import dev.kaato.notzapi.managers.PlaceholderManager
import dev.kaato.notzapi.utils.MessageU.send
import dev.kaato.notzapi.utils.MessageU.sendHoverURL
import dev.kaato.notzapi.utils.MessageU.set
import dev.kaato.notzapi.utils.OthersU.isAdmin
import dev.kaato.notzwarps.commands.NWarpC
import dev.kaato.notzwarps.commands.TpaC
import dev.kaato.notzwarps.commands.WarpC
import dev.kaato.notzwarps.events.GuiEv
import dev.kaato.notzwarps.events.JoinLeaveEv
import dev.kaato.notzwarps.events.MoveEv
import dev.kaato.notzwarps.gui.WarpGUI
import dev.kaato.notzwarps.managers.TpaManager
import dev.kaato.notzwarps.managers.WarpManager.load
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Main : JavaPlugin() {
    companion object {
        lateinit var pathRaw: String


        lateinit var cf: NotzYAML
        lateinit var wf: NotzYAML
        lateinit var msgf: NotzYAML

        lateinit var warpGUI: WarpGUI

        lateinit var notzAPI: NotzAPI
        lateinit var phM: PlaceholderManager
        lateinit var msgM: MessageManager
        lateinit var itemM: ItemManager
    }

    override fun onEnable() {
        pathRaw = dataFolder.absolutePath
        notzAPI = NotzAPI(this)

        cf = NotzYAML("config")
        wf = NotzYAML("warps")
        msgf = messageManager.messageFile

        phM = placeholderManager
        msgM = messageManager
        itemM = itemManager

        object : BukkitRunnable() {
            override fun run() {
                load()
                warpGUI = WarpGUI()

                setupMain()
            }
        }.runTaskLater(this, 5 * 20L)

        Bukkit.getScheduler().runTaskTimer(this, TpaManager.getInstance(), 0, 20)
    }

    private fun setupMain() {
        regCommands()
        regEvents()
        regTab()
        letters()
        if (!Bukkit.getOnlinePlayers().isEmpty()) Bukkit.getOnlinePlayers().filter { it.hasPermission("notzwarps.admin") }.forEach { send(it, "&aWarps have been initialized.") }
    }


    private fun regEvents() {
        Bukkit.getPluginManager().registerEvents(JoinLeaveEv(), this)
        Bukkit.getPluginManager().registerEvents(GuiEv(), this)
        Bukkit.getPluginManager().registerEvents(MoveEv(), this)
    }

    private fun regCommands() {
        getCommand("warp")?.executor = WarpC()
        getCommand("nwarp")?.executor = NWarpC()
        getCommand("tpa").executor = TpaC()
    }

    private fun regTab() {
        getCommand("warp")?.tabCompleter = WarpC()
        getCommand("nwarp")?.tabCompleter = NWarpC()
        getCommand("tpa").tabCompleter = TpaC()
    }


    private fun letters() {
        send(
            Bukkit.getConsoleSender(), """
                &2Inicializado com sucesso.
                &2Initialized successfully.
                &f┳┓    &6┓ ┏       
                &f┃┃┏┓╋┓&6┃┃┃┏┓┏┓┏┓┏
                &f┛┗┗┛┗┗&6┗┻┛┗┻┛ ┣┛┛
                
                ${set("{prefix}")} &6Para mais plugins como este, acesse &bhttps://kaato.dev/plugins&6!!
                ${set("{prefix}")} &6For more plugins like this, visit &bhttps://kaato.dev/plugins&6!!
                
            """.trimIndent()
        )
        Bukkit.getOnlinePlayers().forEach {
            if (isAdmin(it)) {
                sendHoverURL(it, set("{prefix}") + " &6For more plugins like this, visit &e&oour website&6!", arrayOf("&b&okaato.dev/plugins"), "https://kaato.dev/plugins"); it.sendMessage(" ")
                sendHoverURL(it, set("{prefix}") + " &6Para mais plugins como este, acesse o &e&onosso site&6!", arrayOf("&b&okaato.dev/plugins"), "https://kaato.dev/plugins"); it.sendMessage(" ")
            }
        }
    }
}