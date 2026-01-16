package io.xeros.database.players_online;

import io.xeros.model.entity.player.Right;


public class PlayerOnlineEntry {

	private final String name;

	private final Right right;

	private final String title;

	public PlayerOnlineEntry(String name, Right right, String title) {
		this.name = name;
		this.right = right;
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public Right getRight() {
		return right;
	}

	public String getTitle() {
		return title;
	}

}
