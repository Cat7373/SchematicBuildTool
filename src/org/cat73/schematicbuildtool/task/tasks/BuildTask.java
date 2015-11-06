package org.cat73.schematicbuildtool.task.tasks;

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

public class BuildTask implements ITask{
    // Schematic配置信息
    private short length;
    private short width;
    private byte[] blocks;
    private byte[] damages;
    // 新旧方块置换表
    private Map<Short, Short> oldToNew = new HashMap<Short, Short>();
    // loop用变量
    private int sx, sy, sz, ex, ey, ez, x, y, z, index;
    private World world;
    // 状态标记
    private boolean over = false;
    // Config配置信息
    private int buildCount = SettingManager.getIntConfig("Build.speed");
    // 统计信息
    private int blockCount;
    private long time;
    // APIs
    private CommandSender sender;
    
    public BuildTask(Map<String, Tag> tags, CommandSender sender) {
        this.sender = sender;
        init(tags);
    }

    @SuppressWarnings("deprecation")
    public void run() {
        int buildCount = this.buildCount;
        Block b;
        do {
            int blockID = blocks[index] & 0xFF;
            b = world.getBlockAt(x, y, z);
            if(blockID != 0) {
                byte damage = damages[index];

                if (oldToNew.get(blockID) != null) {
                    blockID = oldToNew.get(blockID);
                }

                b.setTypeId(blockID);
                b.setData(damage);
            } else {
                b.setTypeId(0);
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
                        over = true;
                        int time = (int) ((System.currentTimeMillis() - this.time) / 1000);
                        sender.sendMessage(ChatColor.GREEN + "建造完成，平均速度：" + (blockCount / time) + "/s(" + blockCount + "/" + time + "s)");
                        break;
                    }
                }
            }
        } while(buildCount != 0);
    }
    
    public boolean isOver() {
        return over;
    }
    
    @SuppressWarnings("deprecation")
    private void init(Map<String, Tag> tags) {
        // 计时
        this.time = System.currentTimeMillis();
        
        // 读建造参数
        length = (short) tags.get("Length").getValue();
        width = (short) tags.get("Width").getValue();
        blocks = (byte[]) tags.get("Blocks").getValue();
        damages = (byte[]) tags.get("Data").getValue();
        
        // 读新旧方块替换表
        Tag schematicaMapping = tags.get("SchematicaMapping");
        if (schematicaMapping != null) {
            @SuppressWarnings("unchecked")
            Map<String, Tag> list = (Map<String, Tag>) schematicaMapping.getValue();
            Set<String> names = list.keySet();
            for (String name : names) {
                String blockName = name;
                if(blockName.startsWith("minecraft:")) {
                    blockName = blockName.substring(10, blockName.length());
                }
                blockName = blockName.toUpperCase();
                short oldBlockId = (short) list.get(name).getValue();
                Material tmp = Material.getMaterial(blockName);
                if(tmp != null) {
                    short newBlockId = (short) tmp.getId();
                    if(oldBlockId != newBlockId) {
                        oldToNew.put(oldBlockId, newBlockId);
                    }
                }
            }
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
