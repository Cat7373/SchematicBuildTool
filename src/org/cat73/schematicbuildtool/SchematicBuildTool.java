package org.cat73.schematicbuildtool;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.schematicbuildtool.command.CommandManager;
import org.cat73.schematicbuildtool.common.IManager;
import org.cat73.schematicbuildtool.common.Log;
import org.cat73.schematicbuildtool.setting.SettingManager;
import org.cat73.schematicbuildtool.task.TaskManager;

/**
 * 插件主类
 * @author Cat73
 */
public class SchematicBuildTool extends JavaPlugin {
    /**
     * 所有的管理器
     */
    private static ArrayList<IManager> managers = new ArrayList<>();
    /**
     * 命令管理器(cache)
     */
    private static CommandManager commandManager;
    public static File dataFolder;
    public static JavaPlugin self;
    
    /**
     * 初始化
     */
    static {
        putManager(new SettingManager());
        putManager(new TaskManager());
        commandManager = new CommandManager();
        putManager(commandManager);
    }
    
    private static void putManager(IManager m) {
        managers.add(m);
    }
    
    /**
     * 插件被启用时的触发
     */
    public void onEnable() {
        self = this;
        dataFolder = getDataFolder();
        
        if(!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        
        for(IManager m : managers) {
            m.onEnable(this);
        }
        
        Log.info("启动成功!");
    }
    
    /**
     * 插件被禁用时的触发
     */
    public void onDisable() {
        for(IManager m : managers) {
            m.onDisable();
        }
        
        Log.info("已关闭!");
    }
}
