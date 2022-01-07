package page.rightshift.tilegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TileGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;

	BitmapFont font;
	Label label;
	Label.LabelStyle style;
	Stage stage;

	Texture img;
	Texture selectedTileTex;
	TiledMap tiledMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;
	String selectedTileText = "Tile: 1";

	int currentBuildTile = 1;
	int currentTileId;
	boolean isPossiblyStuck = false;

	TiledMapTileLayer.Cell currentTileCell;
	Vector2 tilepos;
	Vector2 campos;

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

		campos = new Vector2();

		batch = new SpriteBatch();
		img = new Texture("character.png");
		selectedTileTex = new Texture("tileselect.png");
		tilepos = new Vector2();

		camera.translate(-256, -192);
		font = new BitmapFont();
		stage = new Stage();

		style = new Label.LabelStyle();
		style.font = font;
		label = new Label("Tile: 1",style);
		label.setPosition(0, 462);

		stage.addActor(label);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 1, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		selectedTileText = "Tile: " + currentBuildTile;
		label.setText(selectedTileText);


		batch.begin();
		stage.draw();
		batch.end();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, campos.x, campos.y);
		batch.draw(selectedTileTex, tilepos.x * 64, tilepos.y * 64);
		batch.end();

		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("base");

		tilepos.x = (float) Math.floor(campos.x / 64);
		tilepos.y = (float) Math.floor(campos.y / 64);

		try {
			currentTileId = layer.getCell((int) tilepos.x, (int) tilepos.y).getTile().getId();
		} catch(NullPointerException e) {
			// a
		}
		currentTileCell = layer.getCell((int)tilepos.x, (int)tilepos.y);
	}

	@Override
	public void dispose() {
		selectedTileTex.dispose();
		img.dispose();
		batch.dispose();

		tiledMap.dispose();
	}

	private boolean isMoveAllowed(int dir) {
		TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("base");

		switch (dir) {
			case 1:
				try {
					if ((int) tilepos.x - 1 > 0) {
						if ((boolean) layer.getCell((int) tilepos.x - 1, (int) tilepos.y).getTile().getProperties().get("solid")) {
							return false;
						}
					}
				} catch (NullPointerException e) {
					return true;
				}
			case 2:
				try {
					if ((int) tilepos.x + 1 < 16) {
						if ((boolean) layer.getCell((int) tilepos.x + 1, (int) tilepos.y).getTile().getProperties().get("solid")) {
							return false;
						}
					}
				} catch (NullPointerException e) {
					return true;
				}
			case 3:
				try {
					if ((int) tilepos.y - 1 > 0) {
						if(!isPossiblyStuck) {
							if ((boolean) layer.getCell((int) tilepos.x, (int) tilepos.y - 1).getTile().getProperties().get("solid")) {
								return false;
							}
						} else if (isPossiblyStuck) {
							isPossiblyStuck = false;
							return true;
						}
					}
				} catch (NullPointerException e) {
					return true;
				}
			case 4:
				try {
					if ((int) tilepos.y + 1 < 16) {
						if ((boolean) layer.getCell((int) tilepos.x, (int) tilepos.y + 1).getTile().getProperties().get("solid")) {
							isPossiblyStuck = true;
							return false;
						}
					}
				} catch (NullPointerException e) {
					return true;
				}
		}

		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.LEFT) {
			if(isMoveAllowed(1)) {
				camera.translate(-64, 0);
				campos.x -= 64;
			}
		}
		if(keycode == Input.Keys.RIGHT) {
			if(isMoveAllowed(2)) {
				camera.translate(64, 0);
				campos.x += 64;
			}
		}
		if(keycode == Input.Keys.DOWN) {
			if(isMoveAllowed(3)) {
				camera.translate(0, -64);
				campos.y -= 64;
			}
		}
		if(keycode == Input.Keys.UP) {
			if(isMoveAllowed(4)) {
				camera.translate(0, 64);
				campos.y += 64;
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

		if(keycode == Input.Keys.SPACE) {
			try {
				currentTileCell.setTile(tiledMap.getTileSets().getTile(currentBuildTile));
			} catch (NullPointerException e) {
				System.out.println("NullPointerException");
			}
		}

		if(keycode == Input.Keys.ALT_LEFT) {
			camera.translate(0, -64);
			campos.y -= 64;
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
