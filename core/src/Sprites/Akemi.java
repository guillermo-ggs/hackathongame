package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import dev.fong.hackathongame.Level1;
import dev.fong.hackathongame.MainGame;

public class Akemi extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion akemiStand;
    private TextureRegion akemiJump;
    private TextureRegion akemiFall;
    private Animation<TextureRegion> akemiRun;
    private float stateTimer;
    private boolean runningRight;
    private Level1 level;

    public Akemi(World world, Level1 level){
        super (level.getAtlas().findRegion("akemistand"));
        this.level = level;
        this.world = world;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<>();

        for(int i = 0; i < 3; i++)
            frames.add(new TextureRegion(level.getAtlas().findRegion("akemirun1"), i* 32, 8, 32, 86));
        frames.add(new TextureRegion(level.getAtlas().findRegion("akemirun2"), 0, 8, 32, 86));
        akemiRun = new Animation<>(0.2f, frames);
        frames.clear();

        akemiJump = new TextureRegion(level.getAtlas().findRegion("akemijump"), 0, 8, 32, 86);
        akemiFall = new TextureRegion(level.getAtlas().findRegion("akemifall"), 0, 8, 32, 86);
        akemiStand = new TextureRegion(level.getAtlas().findRegion("akemistand"), 0, 8, 32, 86);

        defineAkemi();
        setBounds(0, 0, 32, 86);
        setRegion(akemiStand);
    }

    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public void defineAkemi(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(400, 370);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody (bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5);

        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape head = new EdgeShape();
        head.set (new Vector2(-2, 10), new Vector2(2, 10));
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData("head");

    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case FALLING:
                region = akemiFall;
                break;
            case JUMPING:
                region = akemiJump;
                break;
            case RUNNING:
                region = (TextureRegion) akemiRun.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = akemiStand;
                break;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }
    public State getState(){
        if (b2body.getLinearVelocity().y > 0)
            return State.JUMPING;
        if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

}
