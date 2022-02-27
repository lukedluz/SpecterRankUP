package com.lucas.specterrankup.comandos;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.lucas.specterrankup.Main;
import com.lucas.specterrankup.api.API;
import com.lucas.specterrankup.objetos.Rank;
import com.lucas.specterrunas.api.RunasAPI;

public class RankupCommand implements CommandExecutor {
	public static FileConfiguration cg = Main.getInstance().getConfig();

	public static Inventory getRankupInv(Player p) {
		Rank r = API.getRank(p.getUniqueId());
		Rank rp = API.getNextRank(p.getUniqueId());
		String coins = API.getFormat(rp.getCoins());
		String runas = API.getFormat(rp.getRunas());
		String rAtual = r.getPrefix();
		String rProx = rp.getPrefix();
		List<String> lore1 = new ArrayList<>();
		for (String s : cg.getStringList("Menu.Info.Descricao")) {
			lore1.add(s.replaceAll("&", "§").replace("@jogador_grupo", API.getCargo(p, 0))
					.replace("@jogador_moedas",
							API.getFormat(Main.economy.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId()))))
					.replace("@rank_atual", rAtual).replace("@rank_proximo", rProx)
					.replace("@jogador_runas", API.getFormat(RunasAPI.getRunas(p))));
		}
		List<String> lore2 = new ArrayList<>();
		for (String s : cg.getStringList("Menu.Aceitar.Descricao")) {
			lore2.add(s.replace("&", "§").replace("@rank_proximo", rProx).replace("@custo_coins", coins)
					.replace("@custo_runas", runas));
		}
		List<String> lore3 = new ArrayList<>();
		for (String s : cg.getStringList("Menu.Cancelar.Descricao")) {
			lore3.add(s.replace("&", "§").replace("@rank_proximo", rProx));
		}
		Inventory inv = Bukkit.createInventory(null, 3 * 9, "§7Evoluir seu Rank");
		inv.setItem(13,
				API.head64(cg.getString("Menu.Info.Titulo").replace("&", "§"), lore1, cg.getString("Menu.Info.Url")));
		inv.setItem(15, API.head64(cg.getString("Menu.Aceitar.Titulo").replace("&", "§"), lore2,
				cg.getString("Menu.Aceitar.Url")));
		inv.setItem(11, API.head64(cg.getString("Menu.Cancelar.Titulo").replace("&", "§"), lore3,
				cg.getString("Menu.Cancelar.Url")));
		return inv;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		if (!(s instanceof Player))
			return false;
		Player p = (Player) s;
		if (!Main.jogadores.containsKey(p.getUniqueId())) {
			p.playSound(p.getLocation(), Sound.valueOf(cg.getString("Sons.Erro")), 1F, 1F);
			for (String msg : Main.getInstance().getConfig().getStringList("Mensagens.Rank_Invalido")) {
				p.sendMessage(msg.replace("&", "§"));
			}
			return false;
		}
		Rank rp = API.getNextRank(p.getUniqueId());
		if (rp == null) {
			if (com.lucas.specterprestigio.api.API.getPrestigio(p.getUniqueId()) > 4) {
				p.playSound(p.getLocation(), Sound.valueOf(cg.getString("Sons.Erro")), 1F, 1F);
				for (String msg : Main.getInstance().getConfig().getStringList("Mensagens.Rank_Ultimo")) {
					p.sendMessage(msg.replace("&", "§"));
				}
				return false;
			} else {
				p.openInventory(com.lucas.specterprestigio.commands.PrestigioCommand.getInv(p));
				return false;
			}
		}
		for (String msg : cg.getStringList("Mensagens.Rank_Existente")) {
			p.sendMessage(msg.replace("&", "§"));
		}
		p.openInventory(RankupCommand.getRankupInv(p));
		return false;
	}

}
