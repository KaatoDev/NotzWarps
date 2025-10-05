package dev.kaato.notzwarps

import dev.kaato.notzapi.NotzAPI.addPlugin
import dev.kaato.notzapi.NotzAPI.removePlugin
import dev.kaato.notzapi.apis.NotzYAML
import dev.kaato.notzapi.managers.ItemManager
import dev.kaato.notzapi.managers.MessageManager
import dev.kaato.notzapi.managers.NotzManager
import dev.kaato.notzapi.managers.PlaceholderManager
import dev.kaato.notzapi.utils.*
import dev.kaato.notzapi.utils.MessageU.Companion.sendHoverURL
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

        lateinit var plugin: JavaPlugin
        lateinit var napi: NotzManager
        lateinit var phM: PlaceholderManager
        lateinit var msgM: MessageManager
        lateinit var itemM: ItemManager
        lateinit var eventU: EventU
        lateinit var mainU: MainU
        lateinit var menuU: MenuU
        lateinit var messageU: MessageU
        lateinit var othersU: OthersU
    }

    override fun onEnable() {
        pathRaw = dataFolder.absolutePath
        plugin = this
        napi = addPlugin(plugin)

        msgM = napi.messageManager
        itemM = napi.itemManager
        phM = napi.placeholderManager
        eventU = napi.eventU
        mainU = napi.mainU
        menuU = napi.menuU
        messageU = napi.messageU
        othersU = napi.othersU

        cf = NotzYAML(this, "config")
        wf = NotzYAML(this, "warps")
        msgf = msgM.messageFile

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
        if (!Bukkit.getOnlinePlayers().isEmpty()) Bukkit.getOnlinePlayers().filter { it.hasPermission("notzwarps.admin") }.forEach { messageU.send(it, "&aWarps have been initialized.") }
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
        messageU.send(
            Bukkit.getConsoleSender(), """
                &2Inicializado com sucesso.
                &2Initialized successfully.
                &f┳┓    &6┓ ┏       
                &f┃┃┏┓╋┓&6┃┃┃┏┓┏┓┏┓┏
                &f┛┗┗┛┗┗&6┗┻┛┗┻┛ ┣┛┛
                
                ${messageU.set("{prefix}")} &6Para mais plugins como este, acesse &bhttps://kaato.dev/plugins&6!!
                ${messageU.set("{prefix}")} &6For more plugins like this, visit &bhttps://kaato.dev/plugins&6!!
                
            """.trimIndent()
        )
        Bukkit.getOnlinePlayers().forEach {
            if (othersU.isAdmin(it)) {
                sendHoverURL(it, messageU.set("{prefix}") + " &6For more plugins like this, visit &e&oour website&6!", arrayOf("&b&okaato.dev/plugins"), "https://kaato.dev/plugins"); it.sendMessage(" ")
                sendHoverURL(it, messageU.set("{prefix}") + " &6Para mais plugins como este, acesse o &e&onosso site&6!", arrayOf("&b&okaato.dev/plugins"), "https://kaato.dev/plugins"); it.sendMessage(" ")
            }
        }
    }

    override fun onDisable() {
        removePlugin(plugin)
    }
}