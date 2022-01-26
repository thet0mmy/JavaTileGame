package page.rightshift.tilegame;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public abstract class CollisionHandler {
    public static boolean isMoveAllowed(int dir, TiledMapTileLayer layer, Player player) {
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
}
