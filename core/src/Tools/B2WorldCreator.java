package Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import dev.fong.hackathongame.MainGame;

public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap map) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

       for(MapObject object : map.getLayers().get(0).getObjects().getByType(RectangleMapObject.class)){
           Rectangle rect = ((RectangleMapObject) object).getRectangle();

           bdef.type = BodyDef.BodyType.StaticBody;
           bdef.position.set((rect.getX() + rect.getWidth() / 2) / MainGame.PPM, (rect.getHeight() /2) / MainGame.PPM);

           body = world.createBody(bdef);

           shape.setAsBox(rect.getWidth() / 2 / MainGame.PPM, rect.getHeight() / 2 / MainGame.PPM);
           body.createFixture(fdef);
       }
    }
}
