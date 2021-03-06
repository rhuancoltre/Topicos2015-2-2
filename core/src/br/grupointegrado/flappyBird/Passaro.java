package br.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Rhuan Coltre on 05/10/2015.
 */
public class Passaro {

    private final World mundo;
    private final OrthographicCamera camera;
    private final Texture[] texturas;
    private Body corpo;
    private Sprite sprite;

    public Passaro(World mundo, OrthographicCamera camera, Texture[] texturas){


        this.mundo = mundo;
        this.camera = camera;
        this.texturas = texturas;
        this.sprite = new Sprite(texturas[0]);

        initCorpo();
    }

    private void initCorpo() {
        float x = (camera.viewportWidth / 2) / Util.PIXEL_METRO;
        float y = (camera.viewportHeight / 2) / Util.PIXEL_METRO;

        corpo = Util.criarCorpo(mundo, BodyDef.BodyType.DynamicBody, x, y);

        FixtureDef definicao = new FixtureDef();
        definicao.density = 1;
        definicao.friction = 0.4f;
        definicao.restitution = 0.3f;

        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("physics/bird.json"));

        loader.attachFixture(corpo, "bird", definicao, 1, "PASSARO");

    }

    public void atualiar(float delta, boolean movimentar){
        if (movimentar) {
            atualizarVelocidade();
            atualizarRotacao();
        }
    }

    private void atualizarRotacao() {
        float velocidadeY = corpo.getLinearVelocity().y;
        float rotacao = 0;
        if (velocidadeY < 0) {
            //caindo
            rotacao = -15;
        }else if (velocidadeY > 0) {
            //subindo
            rotacao = +10;
        }else {
            //reto
            rotacao = 0;
        }

        rotacao = (float) Math.toRadians(rotacao); // convertendo grau para radiano
        corpo.setTransform(corpo.getPosition(), rotacao );
    }

    private void atualizarVelocidade() {
        corpo.setLinearVelocity(2f, corpo.getLinearVelocity().y);
    }

    /**
     * Aplica uma força positiva no Y para similar o Pulo
     */
    public void pular(){
        corpo.setLinearVelocity(corpo.getLinearVelocity().x, 0);
        corpo.applyForceToCenter(0, 115, false);
    }

    public Body getCorpo(){
        return corpo;
    }


    public void renderizar(SpriteBatch pincel) {
        Vector2 posicao = corpo.getPosition();
        sprite.setTexture(texturas[0]);
        sprite.setPosition(posicao.x * Util.PIXEL_METRO, posicao.y * Util.PIXEL_METRO);
        sprite.setOrigin(0, 0);
        sprite.setRotation((float) Math.toDegrees(corpo.getAngle()));

        sprite.draw(pincel);



    }
}
