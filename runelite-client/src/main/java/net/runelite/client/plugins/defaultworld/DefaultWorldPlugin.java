/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.defaultworld;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

import static net.runelite.client.RuneLite.DEFAULT_WORLD;

@PluginDescriptor(
	name = "Default World",
	description = "Enable a default world to be selected when launching the client",
	tags = {"home"},
		hidden = true
)
@Slf4j
public class DefaultWorldPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private DefaultWorldConfig config;

	@Inject
	private WorldService worldService;

	@Override
	protected void startUp()
	{
		clientThread.invokeLater(() ->
		{
			if (client.getGameState().getState() < GameState.LOGIN_SCREEN.getState())
			{
				return false;
			}

			applyWorld();
			return true;
		});
	}

	@Override
	protected void shutDown()
	{
	}

	@Provides
	DefaultWorldConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DefaultWorldConfig.class);
	}

	@Subscribe
	public void onSessionOpen(SessionOpen event)
	{
		clientThread.invokeLater(this::applyWorld);
	}

	@Subscribe
	public void onWorldChanged(WorldChanged worldChanged)
	{
		int world = client.getWorld();
		config.lastWorld(world);
		log.debug("Saving last world {}", world);
	}

	private void applyWorld()
	{
		if (client.getGameState() != GameState.LOGIN_SCREEN)
		{
			return;
		}

		if (System.getProperty("cli.world") != null)
		{
			return;
		}

		World world = DEFAULT_WORLD;

		final net.runelite.api.World rsWorld = client.createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

		client.changeWorld(rsWorld);
		log.debug("Applied new world {}", rsWorld);
	}
}
