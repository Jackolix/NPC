package codes.elix.papernpc;

import codes.elix.papernpc.commands.CreateCommand;
import codes.elix.papernpc.listeners.Movementlistener;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class PaperNPC extends JavaPlugin {

    private List<ServerPlayer> npcs = new ArrayList<>();
    private List<Integer> npcID = new ArrayList<>();
    private static PaperNPC plugin;
    private CreateCommand createCommand;

    @Override
    public void onEnable() {
        plugin = this;
        System.out.println("Enabled");


        getCommand("npc").setExecutor(new CreateCommand());
        getServer().getPluginManager().registerEvents(new Movementlistener(), this);
        plugin.saveDefaultConfig();

        //ProtocolLib
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                //super.onPacketReceiving(event);
                PacketContainer packet = event.getPacket();

                Integer entityID = packet.getIntegers().read(0);
                if (npcID.contains(entityID)) {
                    if (packet.getEnumEntityUseActions().read(0).getAction() == EnumWrappers.EntityUseAction.ATTACK) {

                        //EnumWrappers.Hand hand = packet.getEnumEntityUseActions().read(0).getHand();
                        EnumWrappers.EntityUseAction action = packet.getEnumEntityUseActions().read(0).getAction();

                        //System.out.println(hand);
                        System.out.println(action);

                        getServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                Inventory inventory = Bukkit.createInventory(null, 9 * 5);
                                ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
                                inventory.setItem(0, item);
                                event.getPlayer().openInventory(inventory);
                            }
                        });
                    }
                }
            }
        });
        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.ENTITY_ACTION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                //super.onPacketReceiving(event);
                PacketContainer packet = event.getPacket();
                //TODO wiki.vg | Entity Action
                
            }
        });
    }

    public List<ServerPlayer> getNpcs() {
        return npcs;
    }

    public static PaperNPC getPlugin() {
        return plugin;
    }
    public List<Integer> getNpcID() { return npcID; }

}

