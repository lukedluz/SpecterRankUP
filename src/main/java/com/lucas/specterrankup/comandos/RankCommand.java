package com.lucas.specterrankup.comandos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.lucas.specterrankup.api.API;
import com.lucas.specterrankup.connection.Database;
import com.lucas.specterrankup.objetos.Rank;

public class RankCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		if (args.length < 1) {
			if (!s.hasPermission("specter.admin")) {
				Player p = (Player) s;
				p.performCommand("ranks");
				return false;
			}
			s.sendMessage(new String[] { "§b§lRankUP Caribe §7- §eAjuda", "",
					" §6/rank setar <jogador> <numero> §7- §eSetar o rank de um jogador",
					" §6/rank listar §7 §eVer a lista de ranks",
					" §6/rank resetar §7- §eResetar o rank de todos os jogadores", "" });
			return false;
		}
		if (args[0].equalsIgnoreCase("resetar")) {
			if (s instanceof Player) {
				s.sendMessage("§cComando disponível apenas via console.");
				return false;
			}
			if (Bukkit.getOnlinePlayers().size() > 0) {
				s.sendMessage("§cEste comando só pode ser utilizado sem jogadores online no servidor.");
				return false;
			}
			Connection con = Database.con;
			PreparedStatement ps = null;
			try {
				ps = con.prepareStatement("DELETE FROM `SpecterRanks`");
				ps.executeQuery();
			} catch (SQLException e) {
				s.sendMessage("§cNão foi possível resetar os ranks, confira o erro abaixo:");
				e.printStackTrace();
				return false;
			}
		}
		if (args[0].equalsIgnoreCase("listar")) {
			s.sendMessage("§6§lSpecter RankUP §7- §eLista de Ranks");
			s.sendMessage("");
			for (Rank r : API.getRanks()) {
				s.sendMessage("§6" + r.getPosicao() + " §7- " + r.getPrefix() + " §7- §2" + API.getFormat(r.getCoins())
						+ " §7- §c" + API.getFormat(r.getRunas()));
			}
			s.sendMessage("");
			return false;
		}
		if (args[0].equalsIgnoreCase("setar")) {
			if (args.length < 3) {
				s.sendMessage(
						"§cUtilize /rank setar <jogador> <numero>. Para saber o número do rank utilize /rank listar.");
				return false;
			}
			Player t = Bukkit.getPlayerExact(args[1]);
			if (t == null) {
				s.sendMessage("§cEste jogador está offline.");
				return false;
			}
			if (!isInt(args[2])) {
				s.sendMessage("§cO argumento '" + args[2] + "' não é um número.");
				return false;
			}
			int posicao = Integer.valueOf(args[2]);
			Rank r = API.getRank(posicao);
			if (r == null) {
				s.sendMessage("§cNão existe um rank com a posição " + posicao + ".");
				return false;
			}
			API.setRank(t, r);
			s.sendMessage("§aYEAH! Você setou o rank " + r.getPrefix() + " §apara o jogador §2" + t.getName());
			t.sendMessage("§aSeu rank foi setado para o rank " + r.getPrefix());
		}
		return false;
	}

	private boolean isInt(String valor) {
		try {
			if (Integer.parseInt(valor) >= 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
