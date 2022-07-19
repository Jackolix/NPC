// This class was created by Elix on 28.06.22


package codes.elix.papernpc.commands;

import codes.elix.papernpc.PaperNPC;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CreateCommand implements CommandExecutor, TabCompleter {
    private final Set<UUID> viewers = new HashSet<>();
    private ServerPlayer npc;
    private String signature;
    private String texture;
    private PaperNPC plugin;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (args.length <= 2) {
                if (args[0].equalsIgnoreCase("create")) {
                    createNPC(p, p.getLocation(), args[1]);

                } else if (args[0].equalsIgnoreCase("showall")) {
                    if (npc != null) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            showTo(player, npc);
                            player.sendMessage("Spawned an NPC!");
                        }
                    } else
                        p.sendMessage("Es gibt keinen NPC!");
                } else if (args[0].equalsIgnoreCase("showto")) { //TODO Kein Plan ob das funktioniert
                    if (npc != null) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            showTo(target, npc);
                        } else
                            p.sendMessage("§cDer Spieler ist nicht online");
                    }

                } else if (args[0].equalsIgnoreCase("loadconfignpc")) {
                    createConfigNPC(args[1]);

                } else if (args[0].equalsIgnoreCase("hide")) {
                    hidefrom(p);

                } else
                    p.sendMessage("§cDieses Kommando existiert nicht!");
            }
        } else
            sender.sendMessage("Nur ein Spieler kann diesen Befehl ausführen");

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> completerlist = new ArrayList<>();
        completerlist.add("create");
        completerlist.add("showall");
        completerlist.add("showto");
        completerlist.add("loadConfigNPC");
        if (args.length == 1) {
            return completerlist;
        }
        return null;
    }

    public void createNPC(Player p, Location location, String name) {
        if (name == null) return;
        CraftPlayer craftPlayer = (CraftPlayer) p;

        MinecraftServer server = craftPlayer.getHandle().getServer();
        ServerLevel level = craftPlayer.getHandle().getLevel();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");

        ServerPlayer npc = new ServerPlayer(server, level, gameProfile, null);
        npc.setPos(location.getX(), location.getY(), location.getZ());
        this.npc = npc;
        PaperNPC.getPlugin().getNpcID().add(npc.getId());
        System.out.println(npc.getId());


        //getSkin(name);
        // https://sessionserver.mojang.com/session/minecraft/profile/UUID?unsigned=false
        String signature = "KZ+zkWaFxnqnp0hUoUHAJN0j777WY9TAo0NGTWJ/iILQK7mesDOLodDs+Sq2F5CSxEFr5jGaRq0eKuMumUb6JrmlY2YAS6c+Kp6eManB6p4FjeiIJomw/r2DYqerVYxw/t068cBYDRaEFNAf2PO+Eqgmcpg3AsNvMCWeVH7z6nSrMCJiS/HIW4iPjcm3vPz+Wwjp9ea21B3Z/QKocaDothPr7TBoed7A40qWdS8dcOwOBQqJBZ1Neyjr+uFesGA3OyLESte8j33bIOYl8FZqgS8b2TXGelmTLklmGbPHVFu1LnajfV3K+B+Cied4QudP3mG2ehTgQA/4Oe7Nit096RfClp8sYIYOacc/sE+lkfm3hI74SDCWL6J211H6VBzEPvt1FbrFmaiwF9oB3DfkOCKcB1W8LJC8339XhQyQ1XzLqltjD8qutPL2SdebKqoP0rYhZ2F64pdzAqKQ327zpu78OtzM8OPiDKs36eafu9dWTYqwfErTjXyYiMLXvoGE+UDdLJS1izt9FafGRJaCBxpdoSKAu0O3iPrO3HTOvP8pY04UAdCCHj6u1YSdpruIlG8ipbvUAh41D2uPRk2eKGnIG2tXbRDzeia2MvSQDv7agq6wd91TURRChESzKGHpev+KrHzfN5DyHpOT6Iyy4VDue9irNgr+ngmOzIkgOLY=";
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY1NjkzOTY5NjY2OCwKICAicHJvZmlsZUlkIiA6ICJmYmRkOTFlYWQyODA0Y2ViODM1YjNmMDk2OGNjMWNjYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJKYWNrb2xpeCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83ZjhiMmRkNTRkMWU0OTljZDFiOTg4ZTM2ZWM1MTIwMmRiYjY5ODY3Y2RhNzg0OGQ0MjBiNGE4ZTQ3OWM3MDg1IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";
        npc.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        showTo(p, npc);

        p.sendMessage("Spawning an NPC");

        //Config Stuff
        FileConfiguration config = PaperNPC.getPlugin().getConfig();
        config.set(name + ".Name", name);
        config.set(name + ".Level", level.dimension().location().getPath()); //Object
        config.set(name + ".X", location.getX());
        config.set(name + ".Y", location.getY());
        config.set(name + ".Z", location.getZ());
        PaperNPC.getPlugin().saveConfig();

    }

    public void showTo(Player player, ServerPlayer npc) {
        viewers.add(player.getUniqueId());
        if (npc == null) return;
        ServerGamePacketListenerImpl ps = ((CraftPlayer) player).getHandle().connection;
        Byte skinFixByte = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        SynchedEntityData dataWatcher = npc.getEntityData();
        dataWatcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), skinFixByte);
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);

        ClientboundPlayerInfoPacket playerInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc);
        ps.send(playerInfoPacket);

        ClientboundAddPlayerPacket addPlayerPacket = new ClientboundAddPlayerPacket(npc);
        ps.send(addPlayerPacket);

        PlayerTeam team = new PlayerTeam(new Scoreboard(), npc.displayName);
        team.getPlayers().add("");
        team.setNameTagVisibility(Team.Visibility.NEVER);
        team.setCollisionRule(Team.CollisionRule.NEVER);
        ps.send(ClientboundSetPlayerTeamPacket.createRemovePacket(team));
        ps.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));

        Bukkit.getServer().getScheduler().runTaskTimer(PaperNPC.getPlugin(), task -> {
            Player currentlyOnline = Bukkit.getPlayer(player.getUniqueId());
            if (currentlyOnline == null ||
                    !currentlyOnline.isOnline() ||
                    !viewers.contains(player.getUniqueId())) {
                task.cancel();
                return;
            }

            PaperNPC.getPlugin().getNpcs().add(npc);
        }, 0, 2);

        Bukkit.getServer().getScheduler().runTaskLater(PaperNPC.getPlugin(), () -> {
            try {
                ClientboundPlayerInfoPacket removeFromTabPacket = new ClientboundPlayerInfoPacket(
                        ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,
                        npc
                );
                ps.send(removeFromTabPacket);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 20);

        Bukkit.getServer().getScheduler().runTaskLater(PaperNPC.getPlugin(), () -> {
            ps.send(new ClientboundSetEntityDataPacket(npc.getId(), dataWatcher, true));
        }, 8);

        ps.send(new ClientboundSetEquipmentPacket(npc.getBukkitEntity().getEntityId(), List.of(Pair.of(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(item)))));

    }

    public void hidefrom(Player p) {
        if (!viewers.contains(p.getUniqueId())) return;
        viewers.remove(p.getUniqueId());

        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(npc.getId());
        ServerGamePacketListenerImpl ps = ((CraftPlayer) p).getHandle().connection;
        ps.send(packet);
    }

    public void createConfigNPC(String name) { //TODO Unfinished
        FileConfiguration config = PaperNPC.getPlugin().getConfig();

        Server bukkitserver = Bukkit.getServer();
        CraftServer craftServer = (CraftServer) bukkitserver;
        MinecraftServer server = craftServer.getHandle().getServer();

        //TODO Level aus der Config laden
        ServerLevel level = craftServer.getHandle().getServer().getLevel(Level.OVERWORLD);
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        ServerPlayer npc = new ServerPlayer(server, level, gameProfile, null);
        this.npc = npc;
        PaperNPC.getPlugin().getNpcID().add(npc.getId());
        System.out.println(npc.getId());

        double x = config.getDouble(name + ".X");
        double y = config.getDouble(name + ".Y");
        double z = config.getDouble(name + ".Z");
        npc.setPos(x, y, z);

        //getSkin(name);
        String signature = "KZ+zkWaFxnqnp0hUoUHAJN0j777WY9TAo0NGTWJ/iILQK7mesDOLodDs+Sq2F5CSxEFr5jGaRq0eKuMumUb6JrmlY2YAS6c+Kp6eManB6p4FjeiIJomw/r2DYqerVYxw/t068cBYDRaEFNAf2PO+Eqgmcpg3AsNvMCWeVH7z6nSrMCJiS/HIW4iPjcm3vPz+Wwjp9ea21B3Z/QKocaDothPr7TBoed7A40qWdS8dcOwOBQqJBZ1Neyjr+uFesGA3OyLESte8j33bIOYl8FZqgS8b2TXGelmTLklmGbPHVFu1LnajfV3K+B+Cied4QudP3mG2ehTgQA/4Oe7Nit096RfClp8sYIYOacc/sE+lkfm3hI74SDCWL6J211H6VBzEPvt1FbrFmaiwF9oB3DfkOCKcB1W8LJC8339XhQyQ1XzLqltjD8qutPL2SdebKqoP0rYhZ2F64pdzAqKQ327zpu78OtzM8OPiDKs36eafu9dWTYqwfErTjXyYiMLXvoGE+UDdLJS1izt9FafGRJaCBxpdoSKAu0O3iPrO3HTOvP8pY04UAdCCHj6u1YSdpruIlG8ipbvUAh41D2uPRk2eKGnIG2tXbRDzeia2MvSQDv7agq6wd91TURRChESzKGHpev+KrHzfN5DyHpOT6Iyy4VDue9irNgr+ngmOzIkgOLY=";
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY1NjkzOTY5NjY2OCwKICAicHJvZmlsZUlkIiA6ICJmYmRkOTFlYWQyODA0Y2ViODM1YjNmMDk2OGNjMWNjYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJKYWNrb2xpeCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83ZjhiMmRkNTRkMWU0OTljZDFiOTg4ZTM2ZWM1MTIwMmRiYjY5ODY3Y2RhNzg0OGQ0MjBiNGE4ZTQ3OWM3MDg1IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";
        npc.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        for (Player p : Bukkit.getOnlinePlayers()) {
            showTo(p, npc);
        }
    }

    public void createConfigNPC(String name, Player player) { //TODO Unfinished
        FileConfiguration config = PaperNPC.getPlugin().getConfig();

        Server bukkitserver = Bukkit.getServer();
        CraftServer craftServer = (CraftServer) bukkitserver;
        MinecraftServer server = craftServer.getHandle().getServer();
        
        //TODO Level aus der Config laden
        ServerLevel level = craftServer.getHandle().getServer().getLevel(Level.OVERWORLD);
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        ServerPlayer npc = new ServerPlayer(server, level, gameProfile, null);
        this.npc = npc;
        PaperNPC.getPlugin().getNpcID().add(npc.getId());
        System.out.println(npc.getId());

        double x = config.getDouble("NPC." + name + ".X");
        double y = config.getDouble("NPC." + name + ".Y");
        double z = config.getDouble("NPC." + name + ".Z");
        npc.setPos(x, y, z);

        //getSkin(name);
        String signature = "KZ+zkWaFxnqnp0hUoUHAJN0j777WY9TAo0NGTWJ/iILQK7mesDOLodDs+Sq2F5CSxEFr5jGaRq0eKuMumUb6JrmlY2YAS6c+Kp6eManB6p4FjeiIJomw/r2DYqerVYxw/t068cBYDRaEFNAf2PO+Eqgmcpg3AsNvMCWeVH7z6nSrMCJiS/HIW4iPjcm3vPz+Wwjp9ea21B3Z/QKocaDothPr7TBoed7A40qWdS8dcOwOBQqJBZ1Neyjr+uFesGA3OyLESte8j33bIOYl8FZqgS8b2TXGelmTLklmGbPHVFu1LnajfV3K+B+Cied4QudP3mG2ehTgQA/4Oe7Nit096RfClp8sYIYOacc/sE+lkfm3hI74SDCWL6J211H6VBzEPvt1FbrFmaiwF9oB3DfkOCKcB1W8LJC8339XhQyQ1XzLqltjD8qutPL2SdebKqoP0rYhZ2F64pdzAqKQ327zpu78OtzM8OPiDKs36eafu9dWTYqwfErTjXyYiMLXvoGE+UDdLJS1izt9FafGRJaCBxpdoSKAu0O3iPrO3HTOvP8pY04UAdCCHj6u1YSdpruIlG8ipbvUAh41D2uPRk2eKGnIG2tXbRDzeia2MvSQDv7agq6wd91TURRChESzKGHpev+KrHzfN5DyHpOT6Iyy4VDue9irNgr+ngmOzIkgOLY=";
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY1NjkzOTY5NjY2OCwKICAicHJvZmlsZUlkIiA6ICJmYmRkOTFlYWQyODA0Y2ViODM1YjNmMDk2OGNjMWNjYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJKYWNrb2xpeCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83ZjhiMmRkNTRkMWU0OTljZDFiOTg4ZTM2ZWM1MTIwMmRiYjY5ODY3Y2RhNzg0OGQ0MjBiNGE4ZTQ3OWM3MDg1IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=";
        npc.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        showTo(player, npc);

    }

    public void delete(String name) {
        FileConfiguration config = PaperNPC.getPlugin().getConfig();

    }

    public void listconfigNPCs() {
        /*TODO
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

        private void listNPCs(Player player) {
          for (CloudNPC cloudNPC : this.npcManagement.getCloudNPCS()) {
            WorldPosition position = cloudNPC.getPosition();

            int x = (int) position.getX();
            int y = (int) position.getY();
            int z = (int) position.getZ();

            BaseComponent[] textComponent = new ComponentBuilder(String.format(
                "§8> %s §8- §7%d, %d, %d §8- §7%s",
                cloudNPC.getDisplayName(), x, y, z, position.getWorld()
            )).create();

            player.spigot().sendMessage(textComponent);
    }
  }


         */
    }

    public void getSkin(String name) {
        Gson gson = new Gson();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        String json = getStringFromURL(url);
        String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();

        url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
        json = getStringFromURL(url);
        JsonObject mainObject = gson.fromJson(json, JsonObject.class);
        JsonObject jObject = mainObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String texture = jObject.get("value").getAsString();
        String signature = jObject.get("signature").getAsString();

        this.signature = signature;
        this.texture = texture;

    }

    private String getStringFromURL(String url) {
        String text = "";
        try {
            Scanner scanner = new Scanner(new URL(url).openStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                while (line.startsWith(" ")) {
                    line = line.substring(1);
                }
                text = text + line;
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }


    /*
        Gson gson = new Gson();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        String json = getStringFromURL(url);
        String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();

        url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
        json = getStringFromURL(url);
        JsonObject mainObject = gson.fromJson(json, JsonObject.class);
        JsonObject jObject = mainObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String texture = jObject.get("value").getAsString();
        String signature = jObject.get("signature").getAsString();

         */
}
//TODO Config system
//TODO NPCHide system
//TODO PERFORMACE: NPC wird geladen/gezeigt wenn Spieler im selben Chunk ist -> CloudNPC | WorldEventListener