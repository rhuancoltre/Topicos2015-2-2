package br.grupointegrado.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import br.grupointegrado.flappyBird.MainGame;
import br.grupointegrado.flappyBird.TelaBase;

/**
 * Created by Rhuan Coltre on 28/09/2015.
 */
public class TelaJogo extends TelaBase {



    private OrthographicCamera camera;     // Camera do Jogo
    private World mundo;    // Representa o mundo do Box2D
    private Body chao; // Corpo do chão
    private Passaro passaro;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private int pontuacao = 0;
    private BitmapFont fontePontuacao;
    private Stage palcoInformacoes;
    private Label lbPontuacao;
    private ImageButton btnPlay;
    private ImageButton btnGameOver;
    private OrthographicCamera cameraInfo;

    private Box2DDebugRenderer debug; // Desenha o mundo na tela para ajudar no desenvolvimento.

    public TelaJogo(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / Util.ESCALA, Gdx.graphics.getHeight() / Util.ESCALA);
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug = new Box2DDebugRenderer();
        mundo = new World(new Vector2(0, -9.8f), false);
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                detectarColisao(contact.getFixtureA(), contact.getFixtureB());
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        initChao();
        initPassaro();
        initFontes();
        initInformacoes();
    }

    private boolean gameOver = false;

    /**
     * Verifica se o passaro está envolvido na colisãos
     * @param fixtureA
     * @param fixtureB
     */
    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
        if ("PASSARO".equals(fixtureA.getUserData()) ||
                "PASSARO".equals(fixtureB.getUserData())) {

            gameOver = true;
        }
    }


    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter fonteParam =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        fonteParam.size = 56;
        fonteParam.color = Color.WHITE;
        fonteParam.shadowColor = Color.BLACK;
        fonteParam.shadowOffsetX = 4;
        fonteParam.shadowOffsetY = 4;

        FreeTypeFontGenerator gerador = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

                fontePontuacao = gerador.generateFont(fonteParam);
        gerador.dispose();
    }

    private void initInformacoes() {
        palcoInformacoes = new Stage (new FillViewport(cameraInfo.viewportWidth,
                cameraInfo.viewportHeight, cameraInfo));
    Gdx.input.setInputProcessor(palcoInformacoes);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fontePontuacao;

        lbPontuacao = new Label("0", estilo);
        palcoInformacoes.addActor(lbPontuacao);

    }

    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0, 0);
    }

    private void initPassaro() {
        passaro = new Passaro(mundo, camera, null);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1); // Limpa a tela e pinta a cor de fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Mantem o buffer de cores

        capturaTeclas();


        atualizar(delta);
        renderizar(delta);



        debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO));
    }
    private boolean pulando = false;

    private void capturaTeclas() {

        pulando = false;
        if (Gdx.input.justTouched()){
            pulando = true;
        }
    }


    /**
     * Renderizar/desenhar as imagens
     * @param delta
     */
    private void renderizar(float delta) {
        palcoInformacoes.draw();
    }

    /**
     * Atualização e cáculos dos corpos
     * @param delta
     */
    private void atualizar(float delta) {
        palcoInformacoes.act(delta);
        passaro.atualiar(delta);
        mundo.step(1f / 60f, 6, 2);

        atualizaInformacoes();
        atualizarObstaculos();
        atualizarCamera();
        atualizarChao();

        if (pulando) {
            passaro.pular();
        }
    }

    private void atualizaInformacoes() {
        lbPontuacao.setText(pontuacao + "");
        lbPontuacao.setPosition(
                cameraInfo.viewportWidth / 2 - lbPontuacao.getPrefWidth() / 2,
                cameraInfo.viewportHeight - lbPontuacao.getPrefHeight());
    }

    private void atualizarObstaculos() {
        //Enquanto a lista tiver menos do que 4, crie obstáculos
        while(obstaculos.size < 4) {
            Obstaculo ultimo = null;
            if (obstaculos.size > 0)
                ultimo = obstaculos.peek(); // Recupera ultimo item da lista

            Obstaculo o = new Obstaculo(mundo, camera, ultimo);

            obstaculos.add(o);
        }
        //Verifica se os obstáculos sairem da tela
        for (Obstaculo o : obstaculos){
            float inicioCamera = passaro.getCorpo().getPosition().x -
                    (camera.viewportWidth / 2 / Util.PIXEL_METRO) - o.getLargura();
            //Verifica se o obstaculo saiu da tela
            if (inicioCamera > o.getPosX()) {
                o.remover();
                obstaculos.removeValue(o, true);
                //Verifca se já passou para contar pontuação
            } else if (!o.isPassou() && o.getPosX() < passaro.getCorpo().getPosition().x) {
                o.setPassou(true); //
                //Calcular pontuação
                pontuacao++;
                //Reproduzir Som
            }
        }
    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x + 34 / Util.PIXEL_METRO) * Util.PIXEL_METRO;
        camera.update();
    }

    private void atualizarChao() {
        Vector2 posicao = passaro.getCorpo().getPosition();

        chao.setTransform(posicao.x, 0, 0);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();
        redimensionaChao();
        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();
    }

    /**
     * Configura o tamanho do chão de acordo com o tamanho da tela
     */
    private void redimensionaChao() {
        chao.getFixtureList().clear();

        float largura = camera.viewportWidth / Util.PIXEL_METRO;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, Util.ALTURA_CHAO / 2);
        Fixture forma = Util.criarForma(chao, shape, "CHAO");

        shape.dispose();
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
    public void dispose() {
        debug.dispose();
        mundo.dispose();
        palcoInformacoes.dispose();
        fontePontuacao.dispose();

    }
}
