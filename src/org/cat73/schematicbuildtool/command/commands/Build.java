package org.cat73.schematicbuildtool.command.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.bukkit.ChatColor;
import org.cat73.schematicbuildtool.SchematicBuildTool;
import org.cat73.schematicbuildtool.command.ICommand;
import org.cat73.schematicbuildtool.task.TaskManager;
import org.cat73.schematicbuildtool.task.tasks.BuildTask;

import com.worldcretornica.schematic.jnbt.NBTInputStream;
import com.worldcretornica.schematic.jnbt.Tag;

public class Build extends ICommand {
    public Build() {
        super("build", "将一个Schematic文件在游戏中建造出来");
    }

    public boolean run() {
        if(args.length < 1) {
            return false;
        }
        
        String fileName = args[0] + ".schematic";
        File file = new File(SchematicBuildTool.dataFolder, fileName);

        if(file.exists()) {
            sender.sendMessage(ChatColor.GREEN + "正在建造，请稍等。。。");
            build(file);
        } else {
            sender.sendMessage(ChatColor.RED + "指定的Schematic文件不存在。");
        }
        
        return true;
    }
    
    private boolean build(File file) {
        Map<String, Tag> tags = getTags(file);
        if(tags == null) {
            return false;
        }

        TaskManager.post(new BuildTask(tags, sender));
        
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Tag> getTags(File file) {
        NBTInputStream tagCompound = null;
        try {
            tagCompound = new NBTInputStream(new FileInputStream(file));
            return (Map<String, Tag>) tagCompound.readTag().getValue();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(tagCompound != null) {
                try {
                    tagCompound.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
