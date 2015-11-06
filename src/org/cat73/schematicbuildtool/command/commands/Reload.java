package org.cat73.schematicbuildtool.command.commands;

import org.bukkit.ChatColor;
import org.cat73.schematicbuildtool.command.ICommand;
import org.cat73.schematicbuildtool.setting.SettingManager;

public class Reload extends ICommand {
    
    public Reload() {
        super("reload", "重新加载配置");
    }

    public boolean run() {
        SettingManager.reload();
        
        sender.sendMessage(ChatColor.GREEN + "配置重载完成。");
        return true;
    }
}
