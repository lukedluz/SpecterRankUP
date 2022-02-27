package com.lucas.specterrankup.connection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.lucas.specterrankup.Main;

public class Database {
	public static Connection con;

	public static void openConnection() {
		if (!Main.c.getBoolean("MySQL.Usar")) {
			openConnectionSQLite();
			return;
		}
		String host = Main.c.getString("MySQL.IP");
		int port = Main.c.getInt("MySQL.Porta");
		String user = Main.c.getString("MySQL.Usuario");
		String password = Main.c.getString("MySQL.Senha");
		String database = Main.c.getString("MySQL.DataBase");
		String type = "jdbc:mysql://";
		String url = String.valueOf(type) + host + ":" + port + "/" + database;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, password);
			Bukkit.getConsoleSender().sendMessage("Conexao com o §2MySQL §asucedida!");
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("Conexao com o §4MySQL §cfalhou, alterando para §aSQLite");
			openConnectionSQLite();
		}
	}

	public static void openConnectionSQLite() {
		File file = new File(Main.getInstance().getDataFolder(), "ranks.db");
		String URL = "jdbc:sqlite:" + file;
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection(URL);
			criarTabela();
			Bukkit.getConsoleSender().sendMessage("Conexao com o §fSQLite §asucedida!");
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("Conexao com o §fSQLite §cfalhou, desabilitando plugin!");
			Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
		}
	}

	public static void close() {
		if (con != null) {
			try {
				con.close();
				con = null;
				Bukkit.getConsoleSender().sendMessage("Conexao com o banco de dados foi fechada.");
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage("Nao foi possivel fechar a conexao com o banco de dados.");
			}
		}
	}

	public static void criarTabela() {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `SpecterRanks` (`player` VARCHAR(24) NULL, `uuid` VARCHAR(45) NULL, `rank` VARCHAR(250));");
			st.executeUpdate();
			Bukkit.getConsoleSender()
					.sendMessage("§a[Main] §6Tabela §f`SpecterRanks` §6criada/carregada com sucesso");
		} catch (SQLException e) {
			Bukkit.getConsoleSender()
					.sendMessage("§a[Main] §cN§o foi possivel criar a tabela §f`SpecterRanks`");
			Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
			e.printStackTrace();
		}
	}

	public static boolean hasJogador(UUID uuid) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("SELECT * FROM `SpecterRanks` WHERE `uuid` = ?");
			st.setString(1, uuid.toString());
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			return false;
		}
	}

	public static String getJogador(UUID uuid) {
		PreparedStatement stm = null;
		try {
			stm = con.prepareStatement("SELECT * FROM `SpecterRanks` WHERE `uuid` = ?");
			stm.setString(1, uuid.toString());
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				return rs.getString("rank");
			}
			return "";
		} catch (SQLException e) {
			return "";
		}
	}

	public static void setJogador(UUID uuid, String rank) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE `SpecterRanks` SET `rank` = ? WHERE `uuid` = ?");
			st.setString(1, rank);
			st.setString(2, uuid.toString());
			st.executeUpdate();
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage("N§o foi poss§vel atualizar um jogador na database");
		}
	}

	public static void addJogador(Player p) {
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("INSERT INTO `SpecterRanks`(`player`, `uuid`, `rank`) VALUES (?,?,?)");
			st.setString(1, p.getName());
			st.setString(2, p.getUniqueId().toString());
			st.setString(3, Main.defaultRank.getNome());
			st.executeUpdate();
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage("N§o foi poss§vel inserir o jogador " + p.getName() + " na database");
		}
	}
}