package com.lucas.specterrankup.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.lucas.specterrankup.api.API;

public class LegendChatAPI implements Listener {
	@EventHandler
	public void chatLc(ChatMessageEvent e) {
		if (e.isCancelled())
			return;
		if (!e.getTags().contains("rank"))
			return;
		e.setTagValue("rank", API.getRank(e.getSender().getUniqueId()).getPrefix() + " §7");
	}
}
