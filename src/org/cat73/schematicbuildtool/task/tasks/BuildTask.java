package org.cat73.schematicbuildtool.task.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cat73.schematicbuildtool.setting.SettingManager;
import org.cat73.schematicbuildtool.task.ITask;

import com.worldcretornica.schematic.jnbt.Tag;

public class BuildTask implements ITask {
    // 二次放置方块表
    private static short[] secondBlocks;
    // Schematic配置信息
    private short length;
    private short width;
    private byte[] blocks;
    private byte[] damages;
    private boolean[] isSecondBlocks;
    // loop用变量
    private int sx, sy, sz, ex, ey, ez, x, y, z, index;
    private World world;
    // 状态标记
    private boolean over = false;
    private int step = 0;
    // Config配置信息
    private int buildCount = SettingManager.getIntConfig("Build.speed");
    // 统计信息
    private int blockCount;
    private long time;
    // APIs
    private CommandSender sender;
    
    static {
        String[] secondBlockNames = new String[] {
            // 装饰性方块
            "torch",              // 火把
            "ladder",             // 梯子
            "vine",               // 藤蔓
            "sign",               // 告示牌
            "banner",             // 旗子

            // 红石
            "lever",              // 拉杆
            "redstone_torch",     // 红石火把
            "stone_button",       // 石质按钮
            "trapdoor",           // 活板门
            "tripwire_hook",      // 绊线钩
            "wooden_button",      // 木质按钮
            "iron_trapdoor"       // 铁活板门
        };
        
        ArrayList<Short> secondBlocks = new ArrayList<>();
        for(String name : secondBlockNames) {
            short blockId = getBlockIdByName(name);
            if(blockId != -1) {
                secondBlocks.add(blockId);
            }
        }
        
        BuildTask.secondBlocks = new short[secondBlocks.size()];
        for(int i = 0; i < secondBlocks.size(); i++) {
            BuildTask.secondBlocks[i] = secondBlocks.get(i);
        }
    }
    
    public BuildTask(Map<String, Tag> tags, CommandSender sender) {
        this.sender = sender;
        init(tags);
    }

    @SuppressWarnings("deprecation")
    public void run() {
        int buildCount = this.buildCount;
        Block b;
        short blockID;
        boolean isSecondBlock;
        do {
            blockID = (short) (blocks[index] & 0xFF);
            isSecondBlock = isSecondBlocks[index];
            b = world.getBlockAt(x, y, z);
            
            if(step == 0 && !isSecondBlock) {
                if(blockID != 0) {
                    byte damage = damages[index];
                    b.setTypeIdAndData(blockID, damage, false);
                } else {
                    b.setTypeId(0);
                }
            } else if(step == 1 && isSecondBlock) {
                byte damage = damages[index];
                b.setTypeIdAndData(blockID, damage, false);
            }

            index++;
            buildCount--;
            if(x < ex) {
                x++;
            } else {
                x = sx;
                
                if(z < ez) {
                    z++;
                } else {
                    z = sz;
                    
                    if(y < ey) {
                        y++;
                    } else {
                        if(step < 1) {
                            index = 0;
                            x = sx;
                            y = sy;
                            z = sz;
                            
                            step++;
                            break;
                        } else {
                            over = true;
                            int time = (int) ((System.currentTimeMillis() - this.time) / 1000);
                            if(time == 0) {
                                time = 1;
                            }
                            sender.sendMessage(ChatColor.GREEN + "建造完成，平均速度：" + (blockCount / time) + "/s(" + blockCount + "/" + time + "s)");
                            sender.sendMessage(ChatColor.GREEN + "配置文件参考速度：" + (blockCount / time * 2));
                            break;
                        }
                    }
                }
            }
        } while(buildCount != 0);
    }
    
    public boolean isOver() {
        return over;
    }
    
    private void init(Map<String, Tag> tags) {
        // 计时
        this.time = System.currentTimeMillis();
        
        // 读建造参数
        length = (short) tags.get("Length").getValue();
        width = (short) tags.get("Width").getValue();
        byte[] blocks = (byte[]) tags.get("Blocks").getValue();
        damages = (byte[]) tags.get("Data").getValue();
        this.blocks = new byte[blocks.length];
        isSecondBlocks = new boolean[blocks.length];
        
        // 读新旧方块替换表
        Map<Short, Short> oldToNew = new HashMap<Short, Short>();
        Tag schematicaMapping = tags.get("SchematicaMapping");
        if (schematicaMapping != null) {
            @SuppressWarnings("unchecked")
            Map<String, Tag> list = (Map<String, Tag>) schematicaMapping.getValue();
            Set<String> names = list.keySet();
            for (String name : names) {
                short oldBlockId = (short) list.get(name).getValue();
                short newBlockId = getBlockIdByName(name);
                if(oldBlockId != newBlockId && newBlockId != -1) {
                    oldToNew.put(oldBlockId, newBlockId);
                }
            }
        }
        
        short block;
        for(int i = 0; i < blocks.length; i++) {
            block = blocks[i];
            if (oldToNew.get(block) != null) {
                this.blocks[i] = (byte) oldToNew.get(block).shortValue();
            } else {
                this.blocks[i] = blocks[i];
            }
            isSecondBlocks[i] = findShortArray(secondBlocks, this.blocks[i]);
        }
        
        // 计算坐标数据
        short height = (short) tags.get("Height").getValue();
        Player player = Bukkit.getPlayer(sender.getName());
        world = player.getWorld();
        Location loc = player.getLocation();
        sx = (int) loc.getX() + 1;
        sy = (int) loc.getY();
        sz = (int) loc.getZ() + 1;
        ex = sx + width - 1;
        ey = sy + height - 1;
        ey = ey > 256 ? 256 : ey;
        ez = sz + length - 1;
        index = 0;
        x = sx; y = sy; z = sz;
        
        blockCount = width * length * (ey - sy + 1);
    }
    
    private boolean findShortArray(short[] array, short b) {
        for(short num : array) {
            if(num == b) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private static short getBlockIdByName(String blockName) {
        if(blockName.startsWith("minecraft:")) {
            blockName = blockName.substring(10, blockName.length());
        }
        blockName = blockName.toUpperCase();
        Material tmp = Material.getMaterial(blockName);
        if(tmp != null) {
            short blockId = (short) tmp.getId();
            return blockId;
        }
        return -1;
    }

//    private void tilefix() {
//        @SuppressWarnings("unchecked")
//        List<Tag> list = (List<Tag>) tags.get("TileEntities").getValue();
//        Log.debug(tags.get("TileEntities").toString());
//        Iterator<Tag> it = list.iterator();
//        while(it.hasNext()) {
//            Tag tag = it.next();
//        }
//    }
}
