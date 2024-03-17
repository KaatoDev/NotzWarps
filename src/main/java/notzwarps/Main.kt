package notzwarps

import notzapi.NotzAPI
import notzapi.NotzAPI.Companion.itemManager
import notzapi.NotzAPI.Companion.messageManager
import notzapi.NotzAPI.Companion.placeholderManager
import notzapi.apis.NotzYAML
import notzapi.managers.ItemManager
import notzapi.managers.MessageManager
import notzapi.managers.PlaceholderManager
import notzapi.utils.MessageU
import notzapi.utils.MessageU.send
import notzwarps.commands.NWarpC
import notzwarps.commands.TpaC
import notzwarps.commands.WarpC
import notzwarps.events.GuiEv
import notzwarps.events.JoinLeaveEv
import notzwarps.events.MoveEv
import notzwarps.gui.WarpGUI
import notzwarps.managers.TpaM
import notzwarps.managers.WarpM
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Main : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var cf: NotzYAML
        lateinit var wf: NotzYAML
        lateinit var msgf: NotzYAML
        lateinit var warpGUI: WarpGUI

        lateinit var api: NotzAPI
        lateinit var phM: PlaceholderManager
        lateinit var msgM: MessageManager
        lateinit var itemM: ItemManager
    }

    override fun onEnable() {
        plugin = this

        cf = NotzYAML(this, "config")
        wf = NotzYAML(this, "warps")
        msgf = NotzYAML(this, "messages")

        api = NotzAPI(msgf)
        phM = placeholderManager
        msgM = messageManager
        itemM = itemManager


        server.scheduler.runTaskLater(this, object : BukkitRunnable() {
            override fun run() {
                WarpM.load()
                warpGUI = WarpGUI()

                setupMain()
            }
        }, 5 * 20)

        Bukkit.getScheduler().runTaskTimer(this, TpaM.getInstance(), 0, 20)
    }

    private fun setupMain() {
        regCommands()
        regEvents()
        regTab()
        letters()
        if (!Bukkit.getOnlinePlayers().isEmpty())
            Bukkit.getOnlinePlayers().filter { it.hasPermission("notzwarps.admin") }.forEach { send(it, "&aAs warps foram inicializadas.") }
    }


    private fun regEvents() {
        Bukkit.getPluginManager().registerEvents(JoinLeaveEv(), this)
        Bukkit.getPluginManager().registerEvents(GuiEv(), this)
        Bukkit.getPluginManager().registerEvents(MoveEv(), this)
    }

    private fun regCommands() {
        getCommand("warp").executor = WarpC()
        getCommand("nwarp").executor = NWarpC()
        getCommand("tpa").executor = TpaC()
    }

    private fun regTab() {
        getCommand("warp").tabCompleter = WarpC()
        getCommand("nwarp").tabCompleter = NWarpC()
        getCommand("tpa").tabCompleter = TpaC()
    }

    private fun letters() {
        Bukkit.getConsoleSender().sendMessage((phM.set("{prefix} &2Inicializado com sucesso.").plus(
            MessageU.c("\n&f┳┓    &6┓ ┏       "
                     + "\n&f┃┃┏┓╋┓&6┃┃┃┏┓┏┓┏┓┏"
                     + "\n&f┛┗┗┛┗┗&6┗┻┛┗┻┛ ┣┛┛"
                     + "\n&f      &6       ┛  "
            )
        )))
    }
}