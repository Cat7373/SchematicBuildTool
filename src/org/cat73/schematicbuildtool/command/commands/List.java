package org.cat73.schematicbuildtool.command.commands;

import java.io.File;

import org.cat73.schematicbuildtool.SchematicBuildTool;
import org.cat73.schematicbuildtool.command.ICommand;

public class List extends ICommand {
    
    public List() {
        super("listall", "列出所有的Schematic文件列表");
    }

    public boolean run() {
        File[] files = SchematicBuildTool.dataFolder.listFiles();
        
        for(File file : files) {
            if(!file.isDirectory()) {
                sender.sendMessage(file.getName());
            }
        }
        
        return true;
    }
}
