package org.cat73.schematicbuildtool.setting;

import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.schematicbuildtool.SchematicBuildTool;
import org.cat73.schematicbuildtool.common.IManager;
import org.cat73.schematicbuildtool.common.Log;

/**
 * 配置管理器
 * @author Cat73
 */
public class SettingManager implements IManager {
    
    private static HashMap<String, Object> config;
    public static SettingManager self;

    public void onEnable(JavaPlugin javaPlugin) {
        self = this;
        onReload();
        Log.debug("Setting部分启用完毕");
    }
    
    public void onDisable() {
        self = null;
        config = null;
        Log.debug("Setting部分禁用完毕");
    }
    
    public void onReload() {
        JavaPlugin javaPlugin = SchematicBuildTool.self;
        javaPlugin.saveDefaultConfig();
        javaPlugin.reloadConfig();
        config = new HashMap<>();
        getConfig(javaPlugin.getConfig());
        Log.debug("Setting部分重载完毕");
    }
    
    public static void reload() {
        self.onReload();
    }

    private void getConfig(FileConfiguration config) {        
        addIntConfig(config, "Build.speed");
    }

    @SuppressWarnings("unused")
    private static void addStringConfig(FileConfiguration config, String name) {
        SettingManager.config.put(name, config.getString(name));
    }
    
    private static void addIntConfig(FileConfiguration config, String name) {
        SettingManager.config.put(name, config.getInt(name));
    }
    
    @SuppressWarnings("unused")
    private static void addBoolConfig(FileConfiguration config, String name) {
        SettingManager.config.put(name, config.getBoolean(name));
    }
    
    public static String getStringConfig(String name) {
        return (String)config.get(name);
    }
    
    public static int getIntConfig(String name) {
        return (int)config.get(name);
    }
    
    public static boolean getBoolConfig(String name) {
        return (boolean)config.get(name);
    }
}