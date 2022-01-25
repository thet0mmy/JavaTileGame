package page.rightshift.tilegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;

public class TileGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	
	Texture img;
	Texture selectedTileTex;

	TiledMap tiledMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;

	int currentBuildTile = 1;
	int currentTileId;

	TiledMapTileLayer.Cell currentTileCell;
	Player player;
	UIManager uiManager;

	private void updateTileSelection() {
		Vector3 mousePos_screen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePos_screen);
		player.selectedTile.x = (float)Math.floor(mousePos_screen.x / 64);
		player.selectedTile.y = (float)Math.floor(mousePos_screen.y / 64);
	}

	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,w,h);
		camera.update();
		tiledMap = new TmxMapLoader().load("map.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		Gdx.input.setInputProcessor(this);

		batch = new SpriteBatch();
		img = new Texture("character.png");
		selectedTileTex = new Texture("tileselect.png");
		camera.translate(-256, -192);

		uiManager = new UIManager();
		player = new Player();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 1, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		uiManager.update(player.selectedTile, currentBuildTile);
		player.draw(img, camera);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(selectedTileTex, player.selectedTile.x * 64, player.selectedTile.y * 64);
		batch.end();

		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("base");
		updateTileSelection();

		if(Gdx.input.isTouched()) {
			if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				try {
					currentTileCell.setTile(tiledMap.getTileSets().getTile(currentBuildTile));
				} catch (NullPointerException e) {
					System.out.println("NullPointerException");
				}
			}
		}

		try {
			currentTileId = layer.getCell((int) player.selectedTile.x, (int) player.selectedTile.y).getTile().getId();
		} catch(NullPointerException e) {
			// a
		}
		currentTileCell = layer.getCell((int)player.selectedTile.x, (int)player.selectedTile.y);
	}

	@Override
	public void dispose() {
		selectedTileTex.dispose();
		img.dispose();
		batch.dispose();

		tiledMap.dispose();
		uiManager.dispose();
	}

	private boolean isMoveAllowed(int dir) {
		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("base");

		boolean agj_up = false;
		boolean agj_down = false;
		boolean agj_left = false;
		boolean agj_right = false;

		try {
			agj_up = (boolean) layer.getCell((int) player.pos.x, (int) player.pos.y + 1).getTile().getProperties().get("solid");
			agj_down = (boolean) layer.getCell((int) player.pos.x, (int) player.pos.y - 1).getTile().getProperties().get("solid");
			agj_left = (boolean) layer.getCell((int) player.pos.x - 1, (int) player.pos.y).getTile().getProperties().get("solid");
			agj_right = (boolean) layer.getCell((int) player.pos.x + 1, (int) player.pos.y).getTile().getProperties().get("solid");
		} catch (NullPointerException e) {
			System.out.println("NullPointerException in isMoveAllowed(int) trying to get adjacent tiles");
		}

		try {
			switch(dir) {
				case 1:
					return !agj_left;
				case 2:
					return !agj_right;
				case 3:
					return !agj_up;
				case 4:
					return !agj_down;
			}
		} catch (NullPointerException e) {
			System.out.println("NullPointerException in isMoveAllowed(int)");
		}

		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.A) {
			if(isMoveAllowed(1)) {
				camera.translate(-64, 0);
				player.pos.x -= 1;
			}
		}
		if(keycode == Input.Keys.D) {
			if(isMoveAllowed(2)) {
				camera.translate(64, 0);
				player.pos.x += 1;
			}
		}
		if(keycode == Input.Keys.S) {
			if(isMoveAllowed(4)) {
				camera.translate(0, -64);
				player.pos.y -= 1;
			}
		}
		if(keycode == Input.Keys.W) {
			if(isMoveAllowed(3)) {
				camera.translate(0, 64);
				player.pos.y += 1;
			}
		}

		if(keycode == Input.Keys.NUM_1)
			tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
		if(keycode == Input.Keys.NUM_2)
			tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());

		if(keycode == Input.Keys.CONTROL_LEFT) {
			if(currentBuildTile - 1 > 0)
				currentBuildTile--;

			System.out.println(currentBuildTile);
		}

		if(keycode == Input.Keys.SHIFT_LEFT) {
			currentBuildTile++;
			System.out.println(currentBuildTile);
		}

		if(keycode == Input.Keys.ALT_LEFT) {
			camera.translate(0, -64);
			player.pos.y -= 1;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
