package org.cat73.schematicbuildtool.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 日志类
 * @author Cat73
 */
public class Log {
    /* 日志输出流 */
    private static final Logger logger = Logger.getLogger("SchematicBuildTool");

    /**
     * 向日志输出流输出日志
     * @param message 要输出的日志
     * @param level 日志的级别
     */
    private static void log(String message, Level level) {
        if(message != null && !message.trim().isEmpty()) {
            logger.log(level, ("[" + PluginInfo.name + "] " + message));
        }
    }
    
    /**
     * 向日志输出流输出信息
     * @param message 要输出的信息
     */
    public static void info(String message) {
        log(message, Level.INFO);
    }
    
    /**
     * 向日志输出流输出警告
     * @param message 要输出的信息
     */
    public static void warning(String message) {
        log(message, Level.WARNING);
    }
    
    /**
     * 向日志输出流输出错误
     * @param message 要输出的信息
     */
    public static void error(String message) {
        log("[ERROR]" + message, Level.WARNING);
    }
    
    /**
     * 向日志输出流输出调试信息
     * @param message 要输出的调试信息
     */
    public static void debug(String message) {
        if(PluginInfo.debug) {
            log("[DEBUG] " + message, Level.INFO);
        }
    }
}
