package id.rajaopak.serverinfo.task;

import dev.rajaopak.opaklibs.inventory.SimpleInventory;
import id.rajaopak.serverinfo.ServerInfo;
import org.bukkit.scheduler.BukkitRunnable;

public class RefreshGui extends BukkitRunnable {

    private ServerInfo plugin;
    private SimpleInventory gui;
    private Callback callback;


    public RefreshGui(ServerInfo plugin, SimpleInventory gui) {
        this.plugin = plugin;
        this.gui = gui;
    }

    public void start(Callback callback) {
        this.callback = callback;
        runTaskTimer(plugin, 0, 5);
    }

    @Override
    public void run() {
        if (gui.getInventory().getViewers().isEmpty()) {
            cancel();
        }

        callback.call();
    }

    public interface Callback {
        void call();
    }
}
