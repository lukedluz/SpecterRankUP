package com.lucas.specterrankup.listeners;

import java.util.UUID;

import com.lucas.specterrankup.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.lucas.specterrankup.api.API;
import com.lucas.specterrankup.connection.Database;

public class PlayerJoin implements Listener {
	@EventHandler
	public void playerj(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (Main.jogadores.get(uuid) == null) {
			if (!Database.hasJogador(uuid)) {
				Database.addJogador(p);
			}
			if (!p.hasPlayedBefore()) {
				PermissionUser pu = PermissionsEx.getUser(p);
				pu.addGroup("Membro");
				pu.addGroup(ChatColor.stripColor(Main.defaultRank.getPrefix().replace("]", "").replace("[", "")));
			}
			Main.jogadores.put(uuid, API.getRank(Database.getJogador(uuid)));
		}
	}
}
