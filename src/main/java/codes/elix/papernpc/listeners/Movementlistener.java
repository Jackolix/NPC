// This class was created by Elix on 28.06.22


package codes.elix.papernpc.listeners;

import codes.elix.papernpc.PaperNPC;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Movementlistener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();

        //Loop through each NPC
        PaperNPC.getPlugin().getNpcs().stream()
                .forEach(npc -> {

                    Location loc = npc.getBukkitEntity().getLocation();
                    loc.setDirection(p.getLocation().subtract(loc).toVector());

                    float yaw = loc.getYaw();
                    float pitch = loc.getPitch();

                    ServerGamePacketListenerImpl ps = ((CraftPlayer) p).getHandle().connection;

                    ps.send(new ClientboundRotateHeadPacket(npc, (byte) ((yaw % 360) * 256 / 360)));
                    ps.send(new ClientboundMoveEntityPacket.Rot(npc.getBukkitEntity().getEntityId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));

                });
    }
}
