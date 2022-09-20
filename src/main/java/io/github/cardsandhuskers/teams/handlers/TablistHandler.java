package io.github.cardsandhuskers.teams.handlers;

import com.mojang.authlib.GameProfile;
import io.github.cardsandhuskers.teams.objects.Team;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;

import static io.github.cardsandhuskers.teams.Teams.handler;
import static io.github.cardsandhuskers.teams.Teams.teamListArray;

public class TablistHandler {
    PointsHandler pointsHandler;

    public TablistHandler() {

    }


    public void buildTabList(Player player) {
        pointsHandler = new PointsHandler();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        MinecraftServer server = serverPlayer.getServer();
        ServerLevel level = serverPlayer.getLevel(); //level = world


        //Remove Fake Players
        for (ServerPlayer fakeP : teamListArray) {
            for(Player p:Bukkit.getOnlinePlayers()) {

                CraftPlayer craftP = (CraftPlayer) p;
                ServerPlayer sp = craftP.getHandle();

                ServerGamePacketListenerImpl packetListener = sp.connection;
                packetListener.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, fakeP));
            }
        }
        teamListArray.clear();

        //build the fake player array
        for(Team t: handler.getTeams()) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), t.getTeamName());

            ServerPlayer npc = new ServerPlayer(server, level, profile, null);
            teamListArray.add(npc);
        }

        //send the NPC to each player online
        for(Player p: Bukkit.getOnlinePlayers()) {
            for(ServerPlayer fakeP:teamListArray) {
                CraftPlayer craftP = (CraftPlayer) p;
                ServerPlayer sp = craftP.getHandle();
                ServerGamePacketListenerImpl packetListener = sp.connection;
                packetListener.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, fakeP));

                Player fakePlayer = fakeP.getBukkitEntity();
                fakePlayer.setPlayerListName(ChatColor.BOLD + handler.getTeam(fakePlayer.getName()).color + ChatColor.BOLD + fakePlayer.getName() + ChatColor.RESET + ChatColor.WHITE + " Points: " + ChatColor.GOLD + pointsHandler.getTeamPoints(handler.getTeam(fakePlayer.getName())));
            }
        }
    }
}
