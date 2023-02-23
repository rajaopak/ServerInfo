package id.rajaopak.serverinfo.command;

import dev.rajaopak.opaklibs.commands.BaseCommand;
import dev.rajaopak.opaklibs.inventory.SimpleInventory;
import dev.rajaopak.opaklibs.libs.ItemBuilder;
import id.rajaopak.serverinfo.ServerInfo;
import id.rajaopak.serverinfo.task.RefreshGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MainCommand extends BaseCommand {

    public MainCommand(ServerInfo plugin) {
        super(plugin, "serverinfo", Arrays.asList("si", "sinfo"), "serverinfo.use",
                sender -> {
                    if (sender instanceof Player player) {
                        SimpleInventory gui = new SimpleInventory(36, "&eServer Info");
                        RefreshGui refreshGui = new RefreshGui(plugin, gui);

                        // animated item
                        refreshGui.start(() -> {
                            gui.setFilterItem(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).build());
                            gui.setItem(11, ItemBuilder.from(Material.CHEST)
                                    .setName("&6&lPerformance")
                                    .addLore("&7TPS&f: &a" + ServerInfo.getUtils().round(ServerInfo.getUtils().recentTps()[0], 2))
                                    .addLore("&7MSPT&f: &a" + ServerInfo.getUtils().round(ServerInfo.getUtils().averageTickTime(), 2))
                                    .addLore("&7RAM&f: &a" + ServerInfo.getUtils().round(ServerInfo.getUtils().ramUsage(), 2) + "&f/&a" +
                                            ServerInfo.getUtils().round(ServerInfo.getUtils().bytesToMegabytes(Runtime.getRuntime().totalMemory()), 2) + " &7MB &f(&a" +
                                            ServerInfo.getUtils().round(ServerInfo.getUtils().bytesToMegabytes(Runtime.getRuntime().maxMemory()), 2) + "&7MB&f)")
                                    .addLore("&7CPU&f: &a" + ServerInfo.getUtils().round(ServerInfo.getUtils().processCpuLoad(), 2) + "%" + "&f, &a" +
                                            ServerInfo.getUtils().round(ServerInfo.getUtils().systemCpuLoad(), 2) + "%" + " &f(&7proc, sys&f)")
                                    .addLore("&7Disk&f: &a" + ServerInfo.getUtils().round(
                                            ServerInfo.getUtils().bytesToMegabytes(ServerInfo.getUtils().diskUsage()), 2) + "&f/&a" +
                                            ServerInfo.getUtils().round(ServerInfo.getUtils().bytesToMegabytes(new File(".").getTotalSpace()), 2) + " MB")
                                    .build());
                            gui.setItem(13, ItemBuilder.from(Material.PAPER)
                                    .setName("&6&lUpTime")
                                    .addLore("&7&o" + ServerInfo.getUtils().formatTime((int) ManagementFactory.getRuntimeMXBean().getUptime() / 1000))
                                    .build());
                            gui.setItem(14, ItemBuilder.from(Material.ANVIL)
                                    .setName("&6&lOnline Players")
                                    .addLore("&7Online&f: &a" + Bukkit.getOnlinePlayers().size() + "&f/&a" + Bukkit.getMaxPlayers() + " &f(&7online&f/&7max&f)")
                                    .addLore("&7Average Ping&f: &a" + ServerInfo.getUtils().averagePing() + "&7ms")
                                    .build());
                            player.updateInventory();
                        });

                        // non animated item
                        gui.setItem(10, ItemBuilder.from(Material.BOOK)
                                .setName("&6&lServer IP")
                                .addLore("&7Ip: &b" + ServerInfo.getServerIp())
                                .addLore("&7Port: &3" + Bukkit.getServer().getPort())
                                .addLore("&7IpLocation: &2" + ServerInfo.getServerIpLocation())
                                .build());
                        gui.setItem(12, ItemBuilder.from(Material.BOOKSHELF)
                                .setName("&6&lSystem Info")
                                .addLore("&7Platform: &e" + Bukkit.getServer().getName())
                                .addLore("&7Version: &e" + Bukkit.getServer().getVersion())
                                .addLore("&7Java: &e" + System.getProperty("java.version"))
                                .addLore("&7OS: &e" + System.getProperty("os.name") + " (version: " + System.getProperty("os.version") + ")")
                                .addLore("&7Cpu: &e" + ServerInfo.getUtils().getCpuName())
                                .build());
                        gui.setItem(15, ItemBuilder.from(Material.ENDER_CHEST)
                                .setName("&6&lWorld Info")
                                .addLore("&7Worlds&f: &e" + Bukkit.getWorlds().size())
                                .addLore("&7World List&f:")
                                .addLore("&e" + (Bukkit.getWorlds().stream().map(world -> {
                                    if (world.getEnvironment() == World.Environment.NORMAL) {
                                        return "&a" + world.getName();
                                    } else if (world.getEnvironment() == World.Environment.NETHER) {
                                        return "&c" + world.getName();
                                    } else if (world.getEnvironment() == World.Environment.THE_END) {
                                        return "&5" + world.getName();
                                    } else {
                                        return "&e" + world.getName();
                                    }
                                }).collect(Collectors.joining("&7, "))))
                                .build());
                        gui.setItem(16, ItemBuilder.from(Material.CRAFTING_TABLE)
                                .setName("&6&lInformation")
                                .addLore("&7Server Name&f: &e" + Bukkit.getServer().getName())
                                .addLore("&7Default GameMode&f: &e" + Bukkit.getServer().getDefaultGameMode().name())
                                .addLore("&7MOTD&f: &e" + Bukkit.getServer().getMotd())
                                .addLore("&7Whitelist&f: &e" + Bukkit.getServer().hasWhitelist())
                                .addLore("&7Online Mode&f: &e" + Bukkit.getServer().getOnlineMode())
                                .addLore("&7View Distance&f: &e" + Bukkit.getServer().getViewDistance())
                                .addLore("&7Hardcore&f: &e" + Bukkit.getServer().isHardcore())
                                .build());
                        gui.setItem(31, ItemBuilder.from(Material.BARRIER)
                                .setName("&c&lClose")
                                .build());

                        // click handler
                        gui.addClickHandler(event -> {
                            if (event.getSlot() == 31) {
                                gui.close(event.getWhoClicked());
                            }
                        });

                        // open gui to the player
                        gui.open(player);
                    }
                },
                sender -> sender.sendMessage("&cYou don't have permission to use this command."),
                sender -> sender.sendMessage("&cNo Subcommand found."),
                null, null);
    }
}
