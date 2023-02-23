package id.rajaopak.serverinfo;

import dev.rajaopak.opaklibs.OpakLibs;
import dev.rajaopak.opaklibs.libs.IpChecker;
import id.rajaopak.serverinfo.command.MainCommand;
import id.rajaopak.serverinfo.util.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerInfo extends JavaPlugin {

    private static Utils utils;

    private static String serverIp;
    private static String serverIpLocation;

    @Override
    public void onEnable() {
        // Plugin startup logic
        utils = new Utils();
        serverIp = utils.getServerIP();
        serverIpLocation = IpChecker.getIpLocation(serverIp);
        OpakLibs.init(this);
        new MainCommand(this).register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Utils getUtils() {
        return utils;
    }

    public static String getServerIp() {
        return serverIp;
    }

    public static String getServerIpLocation() {
        return serverIpLocation;
    }
}
