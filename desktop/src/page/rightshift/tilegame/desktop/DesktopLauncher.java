package page.rightshift.tilegame.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import page.rightshift.tilegame.TileGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setResizable(true);
		config.setWindowSizeLimits(1024, 768,1024,768);
		config.setForegroundFPS(30);
		config.setTitle("tilegame");

		new Lwjgl3Application(new TileGame(), config);
	}
}
