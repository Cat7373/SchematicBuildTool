package org.cat73.schematicbuildtool.command.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.cat73.schematicbuildtool.command.CommandManager;
import org.cat73.schematicbuildtool.command.ICommand;
import org.cat73.schematicbuildtool.common.PluginInfo;

public class Help extends ICommand {

    public Help() {
        super("help", "查看帮助信息");
    }

    public boolean run() {
        //首先来判断是不是有参数
        if(args.length > 1) {
            //判断是不是请求某个已存在命令的帮助
            String msg = CommandManager.getHelpMsg(args[1]);
            if(msg != null) {
                sender.sendMessage(ChatColor.GREEN + args[1] + msg);
            } else { //如果不是则视为页码并打印
                int page;
                try {
                    page = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    page = 1;
                }
                echoHelp(page);
            }
        } else { //没参数就打印第一页
            echoHelp(1);
        }
        
        return true;
    }
    
    private void echoHelp(int page) {
        //每页输出多少条帮助
        final int pageCommandCount = 5;
        //帮助列表的Set对象
        Set<Map.Entry<String, String>> helpList = CommandManager.getHelpList();
        //帮助的总量
        int helpCommandCount = helpList.size();
        //计算总页数
        int maxPage = helpCommandCount / pageCommandCount + ((helpCommandCount % pageCommandCount == 0) ? 0 : 1);
        //防止超出总数
        page = (page > maxPage || page < 1) ? 1 : page;
        
        sender.sendMessage(ChatColor.GREEN + PluginInfo.name + "(Ver" + PluginInfo.version + ", By: " + PluginInfo.author + ") 帮助页面(" + page + "/" + maxPage + ")");
        
        Iterator<Map.Entry<String, String>> it = helpList.iterator();
        for(int i = 0; i < (page - 1) * pageCommandCount; i++) {
            it.next();
        }
        for(int i = 0; i < pageCommandCount && it.hasNext(); i++) {
            Map.Entry<String, String> entry = it.next();
            String name = entry.getKey();
            while(name.length() < 16) {
                name += " ";
            }
            sender.sendMessage(ChatColor.GREEN + name + entry.getValue());
        }
    }
}
