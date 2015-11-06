package org.cat73.schematicbuildtool.task;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.cat73.schematicbuildtool.SchematicBuildTool;
import org.cat73.schematicbuildtool.common.IManager;
import org.cat73.schematicbuildtool.common.Log;

public class TaskManager extends BukkitRunnable implements IManager {
    private static ArrayList<ITask> tasks;
    private int taskId;
    private Boolean work = false;
    
    public void onEnable(JavaPlugin javaPlugin) {
        taskId = 0;
        tasks = new ArrayList<>();
        this.runTaskTimer(SchematicBuildTool.self, 10, 20);
        Log.debug("Task部分启用完毕");
    }

    public void onDisable() {
        tasks = null;
        Log.debug("Task部分禁用完毕");
    }
    
    public void onReload() {
    }

    public void run() {
        synchronized(work) {
            if(work) {
                return;
            } else {
                work = true;
            }
        }
        
        try {
            int count = 100;
            while(count-- != 0) {
                if(tasks.size() == 0) {
                    return;
                }
                
                taskId++;
                if(taskId >= tasks.size()) {
                    taskId = 0;
                }
                
                if(tasks.get(taskId).isOver()) {
                    tasks.remove(taskId);
                    taskId--;
                    continue;
                }
                
                tasks.get(taskId).run();
                return;
            }
        } finally {
            work = false;
        }
    }

    public static void post(ITask task) {
        tasks.add(task);
    }
}
