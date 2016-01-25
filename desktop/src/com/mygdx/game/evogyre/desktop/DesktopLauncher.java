package com.mygdx.game.evogyre.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.evogyre.EvoGyreGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Evo-Gyre";
		cfg.height = 800;
		cfg.width = 800;
		new LwjglApplication(new EvoGyreGame(), cfg);
	}
}
