package com.instantdamagenumbers;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Instant Damage Numbers"
)
public class InstantDamageNumbersPlugin extends Plugin
{
	private int xp = -1;

	@Getter
	private int hit = 0;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InstantDamageNumbersOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);

		log.info("InstantDamageNumbers started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("InstantDamageNumbers stopped!");
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		if (statChanged.getSkill() == Skill.HITPOINTS)
		{
			int newXp = client.getSkillExperience(Skill.HITPOINTS);

			if (xp == -1)
			{
				xp = newXp;
				return;
			}

			long diff = newXp - xp;

			if (diff > 0)
			{
				hit = (int) Math.round(diff / 1.33);
				xp = newXp;
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", hit + "", null);
			}
		}
	}

	@Provides
	InstantDamageNumbersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InstantDamageNumbersConfig.class);
	}
}
