package org.cat73.schematicbuildtool.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.schematicbuildtool.command.commands.*;
import org.cat73.schematicbuildtool.common.IManager;
import org.cat73.schematicbuildtool.common.Log;

/**
 * 命令管理器
 * @author Cat73
 */
public class CommandManager implements IManager,CommandExecutor {
    /* 存储的命令列表 */
    private static final HashMap<String, Class<? extends ICommand>> commandList = new HashMap<>();
    /* 存储的帮助信息列表 */
    private static final HashMap<String, String> helpList = new HashMap<>();

    public void onEnable(JavaPlugin javaPlugin) {
        javaPlugin.getCommand("schematicbuildtool").setExecutor(this);
        
        Log.debug("Command部分启用完毕");
    }

    public void onDisable() {
        Log.debug("Command部分禁用完毕");
    }
    
    public void onReload() {
    }
    
    /**
     * 初始化
     */
    static {
        registerCommand(Help.class);
        registerCommand(Build.class);
        registerCommand(List.class);
        registerCommand(Reload.class);
    }
    
    /**
     * 由Class自动注册一条命令
     * @param commandClass Command类的Class
     */
    private static void registerCommand(Class<? extends ICommand> commandClass) {
        ICommand cmd = newCommand(commandClass);
        String name = cmd.getName();
        registerCommand(name, cmd.getHelpMsg(), commandClass);
    }
    
    /**
     * 由Class自动注册一条命令 并指定名称
     * @param commandClass Command类的Class
     */
    @SuppressWarnings("unused")
    private static void registerCommand(String name, Class<? extends ICommand> commandClass) {
        ICommand cmd = newCommand(commandClass);
        registerCommand(name, cmd.getHelpMsg(), commandClass);
    }
    
    /**
     * 注册一条命令
     * @param name 命令的名称
     * @param help 命令的帮助信息, 如果为null则不添加帮助信息
     * @param commandClass Command类的Class
     */
    private static void registerCommand(String name, String help, Class<? extends ICommand> commandClass) {
        commandList.put(name.toLowerCase(), commandClass);
        if(help != null) {
            helpList.put(name, help);
        }
    }
    
    /**
     * 实例化Command类的对象
     * @param commandClass 要实例化的Class
     * @return 实例化后的Command
     */
    private static ICommand newCommand(Class<? extends ICommand> commandClass) {
        try {
            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 获取一个命令的执行器
     * @return
     */
    private static ICommand getCommand(String name) {
        Class<? extends ICommand> commandClass = commandList.get(name);
        if(commandClass == null) {
            return null;
        }
        
        return newCommand(commandClass);
    }
    
    /**
     * 收到命令时的触发
     */
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        Log.debug(command.getName());
        switch(command.getName().toLowerCase()) {
        case "schematicbuildtool":
            if(args.length == 0) {
                args = new String[]{"help"};
            }
            
            ICommand commandExecer = getCommand(args[0].toLowerCase());
            if(commandExecer == null) {
                commandExecer = getCommand("help");
            }
            
            commandExecer.init(sender, command, commandLabel, args);
            
            try {
                if(commandExecer.run() == false) {
                    sender.sendMessage(command.getUsage());
                }
            } catch(Exception e) {
                sender.sendMessage(ChatColor.RED + "执行命令的过程中出现了一个未处理的错误.");
                e.printStackTrace();
            }
            
            return true;
        
        default:
            return false;
        }
    }
    
    /**
     * 获取帮助信息列表
     * @return 帮助信息列表
     */
    public static Set<Map.Entry<String, String>> getHelpList() {
        return helpList.entrySet();
    }
    
    /**
     * 获取某个命令的帮助信息
     * @return 指定命令的帮助信息, 未找到则返回null
     */
    public static String getHelpMsg(String commandName) {
        return helpList.get(commandName);
    }
}
