package dev.fong.hackathongame;

import Sprites.Akemi;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Level1 extends ScreenAdapter implements Screen {
    MainGame game;
    Texture texture;
    private TextureAtlas atlas;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private World world;
    private B2WorldCreator creator;
    private Akemi player;
    private int jump_count = 0;
    public static final int MAX_JUMP_COUNT = 2;
    public static Music levelMusic;


    public Level1(MainGame game) {
        atlas = new TextureAtlas("akemi.pack");
        this.game = game;
        texture = new Texture("textures.png");
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MainGame.V_Width, MainGame.V_Height, gamecam);

        levelMusic = Gdx.audio.newMusic(Gdx.files.internal("level1music.mp3"));
        levelMusic.setLooping(true);
        levelMusic.setVolume(0.1f);
        levelMusic.play();
        MainGame.menuMusic.stop();

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        world = new World(new Vector2(0, -100), true);

        creator = new B2WorldCreator(this);

        player = new Akemi(world, this);

        world.setContactListener(new WorldContactListener());
        gamecam.setToOrtho(false, gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2);

    }

    public void handleInput(float dt) {
        if (player.currentState == Akemi.State.STANDING || player.currentState == Akemi.State.RUNNING) {
            jump_count = 0;
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && (jump_count < MAX_JUMP_COUNT)) {
            player.b2body.applyLinearImpulse(new Vector2(0, 1000f), player.b2body.getWorldCenter(), true);
            jump_count++;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            player.b2body.applyLinearImpulse(new Vector2(500f, 0), player.b2body.getWorldCenter(), true);

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
            player.b2body.applyLinearImpulse(new Vector2(-500f, 0), player.b2body.getWorldCenter(), true);

        if ((Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) && (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && (jump_count < MAX_JUMP_COUNT)) {
            player.b2body.applyLinearImpulse(new Vector2(500f, 500f), player.b2body.getWorldCenter(), true);
            jump_count++;
        }
    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);
        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.position.y = player.b2body.getPosition().y;
        player.update(dt);
        gamecam.update();
        renderer.setView(gamecam);
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show(){

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


    @Override
    public void render(float delta){
        update(delta);

        Gdx.gl.glClearColor(33/255f,27/255f,37/255f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        //b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height){
        //gamePort.update(width , height);
    }

    @Override
    public void dispose() {
        //dispose of all our opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        levelMusic.dispose();
        MainGame.menuMusic.dispose();
        //b2dr.dispose();
    }
}
