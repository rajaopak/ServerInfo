package id.rajaopak.serverinfo.util;

import com.sun.management.OperatingSystemMXBean;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;

import static id.rajaopak.serverinfo.util.Nms.*;

public class Utils {

    private static final Class<?> MinecraftServer_class = needNMSClassOrElse(
            "MinecraftServer",
            "net.minecraft.server.MinecraftServer"
    );
    private static final MethodHandle MinecraftServer_getServer_method = needStaticMethod(MinecraftServer_class, "getServer", MinecraftServer_class);
    private static final Field MinecraftServer_recentTps_field = needField(MinecraftServer_class, "recentTps");

    private final Field MinecraftServer_recentTickTimes_field = tickTimesField();

    public String getCpuName() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        return processor.getProcessorIdentifier().getName();
    }

    private @NonNull Field tickTimesField() {
        final String tickTimes;
        final int ver = PaperLib.getMinecraftVersion();
        if (ver < 13) {
            tickTimes = "h";
        } else if (ver == 13) {
            tickTimes = "d";
        } else if (ver == 14 || ver == 15) {
            tickTimes = "f";
        } else if (ver == 16) {
            tickTimes = "h";
        } else if (ver == 17) {
            tickTimes = "n";
        } else if (ver == 18) {
            tickTimes = "o";
        } else if (ver == 19) {
            tickTimes = "k";
        } else {
            throw new IllegalStateException("Don't know tickTimes field name!");
        }
        return needField(MinecraftServer_class, tickTimes);
    }

    public double averageTickTime() {
        final Object server = invokeOrThrow(MinecraftServer_getServer_method);
        try {
            final long[] recentMspt = (long[]) this.MinecraftServer_recentTickTimes_field.get(server);
            return toMilliseconds((long) average(recentMspt));
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Failed to get server mspt", e);
        }
    }

    public double @NonNull [] recentTps() {
        final Object server = invokeOrThrow(MinecraftServer_getServer_method);
        try {
            return (double[]) MinecraftServer_recentTps_field.get(server);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Failed to get server TPS", e);
        }
    }

    public double processCpuLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return osBean.getProcessCpuLoad() * 100;
    }

    public double systemCpuLoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return osBean.getCpuLoad() * 100;
    }

    public double ramUsage() {
        return bytesToMegabytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    public long diskUsage() {
        return new File(".").getTotalSpace() - new File(".").getFreeSpace();
    }

    public int averagePing() {
        return (int) average(Bukkit.getOnlinePlayers().stream().mapToLong(Player::getPing).toArray());
    }

    public double bytesToMegabytes(long bytes) {
        return new BigDecimal(bytes).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP).doubleValue();
    }

    public double toMilliseconds(final long time) {
        return time * 1.0E-6D;
    }

    public double average(final long @NonNull [] longs) {
        long i = 0L;
        for (final long l : longs) {
            i += l;
        }
        return i / (double) longs.length;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getServerIP() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("https://checkip.amazonaws.com").openStream())).readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String formatTime(int seconds) {
        if (seconds == 0) {
            return "0s";
        }

        long minute = seconds / 60;
        seconds = seconds % 60;
        long hour = minute / 60;
        minute = minute % 60;
        long day = hour / 24;
        hour = hour % 24;

        StringBuilder time = new StringBuilder();
        if (day != 0) {
            time.append(day).append("d ");
        }
        if (hour != 0) {
            time.append(hour).append("h ");
        }
        if (minute != 0) {
            time.append(minute).append("m ");
        }
        if (seconds != 0) {
            time.append(seconds).append("s");
        }

        return time.toString().trim();
    }
}
