package org.cat73.schematicbuildtool.common;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * 管理器接口
 * @author Cat73
 */
public interface IManager {

    /**
     * 插件启用时的触发
     */
    void onEnable(JavaPlugin javaPlugin);

    /**
     * 插件禁用时的触发
     */
    void onDisable();

    /**
     * 插件重载时的触发
     */
    void onReload();
}
