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
	
	Texture playerTexture;
	Texture selectedTileTex;

	TiledMap tiledMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;

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
		playerTexture = new Texture("character.png");
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
		uiManager.update(player);
		player.draw(playerTexture, camera);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(selectedTileTex, player.selectedTile.x * 64, player.selectedTile.y * 64);
		batch.end();

		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("base");
		updateTileSelection();

		if(Gdx.input.isTouched()) {
			if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
				try {
					currentTileCell.setTile(tiledMap.getTileSets().getTile(player.buildType));
				} catch (NullPointerException e) {
					System.out.println("NullPointerException");
				}
			}
		}

		currentTileCell = layer.getCell((int)player.selectedTile.x, (int)player.selectedTile.y);
	}

	@Override
	public void dispose() {
		selectedTileTex.dispose();
		playerTexture.dispose();
		batch.dispose();

		tiledMap.dispose();
		uiManager.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("base");

		if(keycode == Input.Keys.A) {
			if(CollisionHandler.isMoveAllowed(1, layer, player)) {
				camera.translate(-64, 0);
				player.pos.x -= 1;
			}
		}
		if(keycode == Input.Keys.D) {
			if(CollisionHandler.isMoveAllowed(2, layer, player)) {
				camera.translate(64, 0);
				player.pos.x += 1;
			}
		}
		if(keycode == Input.Keys.S) {
			if(CollisionHandler.isMoveAllowed(4, layer, player)) {
				camera.translate(0, -64);
				player.pos.y -= 1;
			}
		}
		if(keycode == Input.Keys.W) {
			if(CollisionHandler.isMoveAllowed(3, layer, player)) {
				camera.translate(0, 64);
				player.pos.y += 1;
			}
		}

		if(keycode == Input.Keys.NUM_1)
			tiledMap.getLayers().get(0).setVisible(!tiledMap.getLayers().get(0).isVisible());
		if(keycode == Input.Keys.NUM_2)
			tiledMap.getLayers().get(1).setVisible(!tiledMap.getLayers().get(1).isVisible());

		if(keycode == Input.Keys.CONTROL_LEFT) {
			if(player.buildType - 1 > 0)
				player.buildType--;

			System.out.println(player.buildType);
		}

		if(keycode == Input.Keys.SHIFT_LEFT) {
			player.buildType++;
			System.out.println(player.buildType);
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
