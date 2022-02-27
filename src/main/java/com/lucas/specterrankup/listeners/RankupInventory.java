package com.lucas.specterrankup.listeners;

import com.ystoreplugins.yminas.api.MinaAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.lucas.specterrankup.Main;
import com.lucas.specterrankup.api.API;
import com.lucas.specterrankup.objetos.Rank;
import com.lucas.specterrunas.api.RunasAPI;

public class RankupInventory implements Listener {
	FileConfiguration c = Main.getInstance().getConfig();

	@EventHandler
	public void InventoryClickEvent(InventoryClickEvent e) {
		if (e.getCurrentItem() == null) {
			return;
		}
		if (e.getInventory().getName().equals("§7Evoluir seu Rank")) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			Rank r = API.getNextRank(p.getUniqueId());
			Double coins = r.getCoins();
			Double runas = r.getRunas();
			Double blocos = r.getBlocos();
			if (e.getSlot() == 15) {
				if (Main.economy.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId())) >= coins
						&& RunasAPI.getRunas(p) >= runas && MinaAPI.getPlayer(p.getName()).getBlocos() >= blocos) {
					p.closeInventory();
					p.playSound(p.getLocation(), Sound.valueOf(c.getString("Sons.Upou")), 1F, 1F);
					RunasAPI.removerRunas(p, r.getRunas());
					Main.economy.withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), r.getCoins());
					MinaAPI.getPlayer(p.getName()).removeBlocks(r.getBlocos());
					API.evoluir(p);
				} else {
					String coinsdf = "0k";
					String runasdf = "0k";
					String blocosdf = "0k";
					if ((coins - Main.economy.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId()))) >= 0) {
						coinsdf = API.getFormat(
								coins - Main.economy.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId())));
					}
					if ((runas - RunasAPI.getRunas(p)) >= 0) {
						runasdf = API.getFormat(runas - RunasAPI.getRunas(p));
					}
					if ((blocos - com.ystoreplugins.yminas.api.MinaAPI.getPlayer(p.getName()).getBlocos()) >= 0) {
						blocosdf = API.getFormat(blocos - MinaAPI.getPlayer(p.getName()).getBlocos());
					}
					p.playSound(p.getLocation(), Sound.valueOf(c.getString("Sons.Sem_Coins")), 1F, 1F);
					p.sendMessage(
							c.getString("Mensagens.Sem_Coins").replace("&", "§").replace("@rank_proximo", r.getPrefix())
									.replace("{coins}", coinsdf).replace("{runas}", runasdf).replace("{blocos}", blocosdf));
					p.closeInventory();
					return;
				}
			}
			if (e.getSlot() == 11) {
				p.playSound(p.getLocation(), Sound.valueOf(c.getString("Sons.Cancelou")), 1F, 1F);
				for (String msg : c.getStringList("Mensagens.Cancelou")) {
					p.sendMessage(msg.replace("&", "§").replace("@rank_proximo", r.getPrefix()));
				}
				p.closeInventory();
				return;
			}
		}
	}
}
