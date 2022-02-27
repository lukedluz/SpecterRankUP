
package com.lucas.specterrankup.api;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.lucas.specterrankup.Main;
import com.lucas.specterrankup.objetos.Rank;

public class API {

	public static Rank getRank(UUID p) {
		return Main.jogadores.get(p);
	}

	public static Rank getRank(Integer posicao) {
		return Main.ranks.stream().filter(t -> t.getPosicao() == posicao).findFirst().orElse(null);
	}

	public static Rank getRank(String nome) {
		return Main.ranks.stream().filter(t -> t.getNome().equals(nome)).findFirst().orElse(null);
	}

	public static void setRank(Player p, Rank r) {
		PermissionUser pu = PermissionsEx.getUser(p);
		pu.addGroup(r.getNome());
		UUID t = p.getUniqueId();
		Rank f = getRank(t);		
		pu.removeGroup(f.getNome());		
		Main.jogadores.replace(t, r);
	}

	public static Rank getNextRank(UUID p) {
		return getRank(getRank(p).getPosicao() + 1);
	}

	public static void evoluir(Player p) {
		setRank(p, getNextRank(p.getUniqueId()));
		for (String rank : Main.ranksc.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Main.ranksc.getConfig().getString("Ranks." + rank + ".Comando").replace("@jogador", p.getName()));
		}
	}

	public static List<Rank> getRanks() {
		return Main.ranks;
	}

	public static ItemStack head64(String nome, List<String> lore, String url) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = Base64.getEncoder()
				.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		try {
			profileField = itemMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(itemMeta, profile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		itemMeta.setLore(lore);
		itemMeta.setDisplayName(nome);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static String getCargo(Player p, int numero) {
		if (Main.permission.getPlayerGroups(p).length < numero)
			return "";
		if (Main.chat.getGroupPrefix(p.getWorld(), Main.permission.getPlayerGroups(p)[numero]) == "")
			return "";
		return Main.chat.getGroupPrefix(p.getWorld(), Main.permission.getPlayerGroups(p)[numero])
				.replace(" &7", "").replace("&", "§");
	}

	public static String format(Double valor) {
		DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(new Locale("pt", "BR")));
		return decimalFormat.format(valor);
	}

	public static String getFormat(Double valor) {
		String[] simbols = new String[] { "", "k", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D", "UN", "DD", "TD",
				"QD", "QID", "SD", "SSD", "OD", "ND" };
		int index;
		for (index = 0; valor / 1000.0 >= 1.0; valor /= 1000.0, ++index) {
		}
		return format(valor) + simbols[index];
	}
}
