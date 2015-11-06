package org.cat73.schematicbuildtool.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class ICommand {
    /* 命令的名称 */
    private final String name;
    /* 命令的帮助信息 */
    private final String helpMsg;
    
    /* 谁调用的这个命令 */
    protected CommandSender sender;
    /* 被执行的命令 */
    protected Command command;
    /* 被使用的别名 */
    protected String label;
    /* 参数列表 */
    protected String[] args;
    
    /**
     * 命令类的构造器
     * @param name 命令的名称
     */
    protected ICommand(String name, String helpMsg) {
        this.name = name;
        this.helpMsg = helpMsg;
    }
    
    /**
     * 初始化命令执行前的参数
     */
    public void init(CommandSender sender, Command command, String commandLabel, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = commandLabel;
        this.args = new String[args.length - 1];
        
        for(int i = 1; i < args.length; i++) {
            this.args[i - 1] = args[i];
        }
    }
    
    /**
     * 获取命令的名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取命令的帮助信息
     */
    public String getHelpMsg() {
        return this.helpMsg;
    }
    
    /**
     * 运行这条命令
     * @return 如果参数格式不正确可以返回false，控制器会自动显示该命令的用法
     */
    public abstract boolean run() throws Exception;
}
