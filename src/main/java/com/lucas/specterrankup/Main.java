package com.lucas.specterrankup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.lucas.specterrankup.objetos.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import com.lucas.specterrankup.comandos.RankCommand;
import com.lucas.specterrankup.comandos.RankupCommand;
import com.lucas.specterrankup.connection.Database;
import com.lucas.specterrankup.listeners.LegendChatAPI;
import com.lucas.specterrankup.listeners.PlayerJoin;
import com.lucas.specterrankup.listeners.RankupInventory;
import com.lucas.specterrankup.objetos.Rank;

public class Main extends JavaPlugin {
	public static Main m;
	public static ArrayList<Rank> ranks = new ArrayList<>();
	public static HashMap<UUID, Rank> jogadores = new HashMap<>();
	public static Permission permission = null;
	public static Economy economy = null;
	public static Chat chat = null;
	public static Rank defaultRank;
	public static FileConfiguration c;
	private static Player p;
	public static Config ranksc;
	public static Config config;

	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("");
		Bukkit.getConsoleSender().sendMessage("§7==========================");
		Bukkit.getConsoleSender().sendMessage("§7| §bSpecterRankUP          §7|");
		Bukkit.getConsoleSender().sendMessage("§7| §bVersão 1.0             §7|");
		Bukkit.getConsoleSender().sendMessage("§7| §fStatus: §aLigado         §7|");
		Bukkit.getConsoleSender().sendMessage("§7==========================");
		Bukkit.getConsoleSender().sendMessage("");
		m = this;
		carregarRanks();
		c("Todos os ranks foram carregados com sucesso.");
		loadVault();
		c("Plugin associado com o vault com sucesso.");
		registrarEventos();
		c("Eventos registrados com sucesso.");
		registrarComandos();
		registrarArquivos();
		c("Comandos registrados com sucesso.");
		Database.openConnection();
		Database.criarTabela();
		c("Plugin iniciado com sucesso.");
	}

	@Override
	public void onDisable() {
		saveJogadores();
		Database.close();
		m = null;
		jogadores.clear();
		ranks.clear();
		c("Plugin desbilitado.");
	}


	public void registrarArquivos(){

		config = new Config(this, "config.yml");
		ranksc = new Config(this, "ranks.yml");

		config.saveDefaultConfig();
		ranksc.saveDefaultConfig();

		//createFile(this, "Geral/", false);

	}


	public void createFile(Main main, String fileName, boolean isFile) {
		try {
			File file = new File(main.getDataFolder() + File.separator + fileName);
			if (isFile) file.createNewFile();
			else if (!file.exists()) file.mkdirs();
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}



	private void saveJogadores() {		
		jogadores.keySet().stream().forEach(p -> {
			Database.setJogador(p, jogadores.get(p).getNome());
		});		
	}

	private void carregarRanks() {
		for (String rank : ranksc.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
			String nome = rank;
			String prefix = ranksc.getConfig().getString("Ranks." + rank + ".Prefix").replace("&", "§");
			int posicao = ranksc.getConfig().getInt("Ranks." + rank + ".Posicao");
			boolean defaultb = false;
			if (ranksc.getConfig().contains("Ranks." + rank + ".Padrao")) {
				defaultb = ranksc.getConfig().getBoolean("Ranks." + rank + ".Padrao");
			}
			if (defaultb == true) {
				Rank r = new Rank(nome, prefix, 0.0, 0.0, 0.0, posicao, defaultb);
				ranks.add(r);
				defaultRank = r;
			} else {
				double coins = ranksc.getConfig().getDouble("Ranks." + rank + ".Coins");
				double runas = ranksc.getConfig().getDouble("Ranks." + rank + ".Runas");
				double blocos = ranksc.getConfig().getDouble("Ranks." + rank + ".Blocos");
				Rank r = new Rank(nome, prefix, coins, runas, blocos, posicao, defaultb);
				ranks.add(r);
				}
		}
	}

	private void registrarComandos() {
		getCommand("rank").setExecutor(new RankCommand());
		getCommand("rankup").setExecutor(new RankupCommand());
	}

	private void registrarEventos() {
		Bukkit.getPluginManager().registerEvents(new RankupInventory(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new LegendChatAPI(), this);
	}

	private void c(String s) {
		Bukkit.getConsoleSender().sendMessage("[SpecterRankup] " + s);
	}

	public static Main getInstance() {
		return m;
	}

	private void loadVault() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
	}
}