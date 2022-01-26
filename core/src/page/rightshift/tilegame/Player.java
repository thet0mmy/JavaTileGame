package page.rightshift.tilegame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    public Vector2 pos;
    public Vector2 selectedTile;
    public int buildType;
    private final SpriteBatch batch;

    public Vector2 toPixelPos() {
        Vector2 v = new Vector2();

        v.x = this.pos.x * 64;
        v.y = this.pos.y * 64;

        return v;
    }

    /*
    public void move(int x, int y) {
        this.pos.x = x;
        this.pos.y = y;
    }

    public void move(Vector2 newPos) {
        this.pos = newPos;
    }

    Keep this stuff for the networking!!!

    */

    public void draw(Texture t, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(t, this.toPixelPos().x, this.toPixelPos().y);
        batch.end();
    }

    Player() {
        pos = new Vector2();
        selectedTile = new Vector2();
        batch = new SpriteBatch();
    }
}
