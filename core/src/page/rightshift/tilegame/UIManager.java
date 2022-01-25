package page.rightshift.tilegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UIManager {
    private final Label tileLabel;
    private final Label posLabel;
    private BitmapFont font;
    private final SpriteBatch batch;

    private String tileLabelString;
    private String posLabelString;

    public Stage stage;

    void update(Vector2 pos, int buildType) {
        tileLabelString = "Tile: " + buildType;
        posLabelString = "Pos: (" + (int)pos.x + ", " + (int)pos.y + ")";

        tileLabel.setText(tileLabelString);
        posLabel.setText(posLabelString);

        batch.begin();
        stage.draw();
        batch.end();
    }

    void dispose() {
        stage.dispose();
        font.dispose();
    }

    UIManager() {
        stage = new Stage();
        font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle();
        batch = new SpriteBatch();

        tileLabelString = "Tile: 0";
        posLabelString = "Pos: (0, 0)";

        font = new BitmapFont();
        System.out.println(font.getScaleX());

        style.font = font;
        tileLabel = new Label(tileLabelString, style);
        posLabel = new Label(posLabelString, style);

        tileLabel.setPosition(0, Gdx.graphics.getHeight() - 16);
        posLabel.setPosition(0, Gdx.graphics.getHeight() - 32);

        stage.addActor(posLabel);
        stage.addActor(tileLabel);
    }
}
