package br.grupointegrado.flappyBird.desktop;

import com.badlogic.gdx.Screen;

import br.grupointegrado.flappyBird.MainGame;

/**
 * Created by Rhuan Coltre on 28/09/2015.
 */
public abstract class TelaBase implements Screen {

    protected MainGame game;

    public TelaBase (MainGame game) {
        this.game = game;
    }


    @Override
    public void hide() {


    }
}
