package com.megandwarnock.pdxbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Random;

public class PdxBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;

	Texture gameOver;
	Texture splash;
	Texture tapToPlay;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	int score = 0;
	int scoringPipe = 0;
	BitmapFont font;

	int gameState = 0;
	float gravity = 2;

	Texture topPipe;
	Texture bottomPipe;
	float gap = 600;
	float maxPipeOffset;
	Random randomGenerator;
	float pipeVelocity = 8;
	int numberOfPipes = 4;
	float[] pipeX = new float[numberOfPipes];
	float[] pipeOffset = new float[numberOfPipes];
	float distanceBetweenPipes;
	Rectangle[] topPipeShape;
	Rectangle[] bottomPipeShape;

	float delay = 2;

	Stage stage;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.jpg");
		gameOver = new Texture("go.png");
		splash = new Texture("splash.jpg");
		tapToPlay = new Texture("tap_to_play.png");
		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		birds = new Texture[4];
		birds[0] = new Texture("pdxb1.png");
		birds[1] = new Texture("pdxb2.png");
		birds[2] = new Texture("pdxb3.png");
		birds[3] = new Texture("pdxb4.png");


		topPipe = new Texture("toptube.png");
		bottomPipe = new Texture("bottomtube.png");
		maxPipeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 -100;
		randomGenerator = new Random();
		distanceBetweenPipes = Gdx.graphics.getWidth() * 3 / 4;
		topPipeShape = new Rectangle[numberOfPipes];
		bottomPipeShape = new Rectangle[numberOfPipes];

		startGame();

	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() /2;

		for(int i = 0; i < numberOfPipes; i++) {
			pipeOffset[i] =  (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			pipeX[i] = Gdx.graphics.getWidth() / 2 - topPipe.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenPipes;
			topPipeShape[i] = new Rectangle();
			bottomPipeShape[i] = new Rectangle();
		}
	}

	public void birdAndScore() {
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
	}

	public void collision() {
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

		for(int i = 0; i < numberOfPipes; i++) {
			if (Intersector.overlaps(birdCircle, topPipeShape[i]) || Intersector.overlaps(birdCircle, bottomPipeShape[i])) {
				Gdx.app.log("collision", "yes");
				gameState = 3;
			}
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		 //GAME STATE - splash page going to instructions stage
		 if(gameState == 0) {
			batch.draw(splash, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			 Timer.schedule(new Timer.Task() {
				 @Override
				 public void run() {
					 if(gameState == 0) {
						 gameState = 1;
					 }
				 }
			 }, delay);

		 //GAME STATE - playing the game
		} else if (gameState == 2) {
			 collision();
			 birdAndScore();

			if (pipeX[scoringPipe] < Gdx.graphics.getWidth() / 2) {
				score++;
				if (scoringPipe < numberOfPipes - 1) {
					scoringPipe++;
				} else {
					scoringPipe = 0;
				}
			}

			if(Gdx.input.justTouched()) {
				velocity = -30;
			}

			//randomizing the pipes and generating placement
			for(int i = 0; i < numberOfPipes; i++) {
				if (pipeX[i] < - topPipe.getWidth()) {
					pipeX[i] += numberOfPipes * distanceBetweenPipes;
					pipeOffset[i] =  (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					pipeX[i] = pipeX[i] - pipeVelocity;
				}

				batch.draw(topPipe, pipeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i]);
				batch.draw(bottomPipe, pipeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomPipe.getHeight() + pipeOffset[i]);

				topPipeShape[i] = new Rectangle(pipeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i], topPipe.getWidth				(), topPipe.getHeight());

				bottomPipeShape[i] = new Rectangle(pipeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomPipe.getHeight() + 				pipeOffset[i], bottomPipe.getWidth(), bottomPipe.getHeight());
			}

			if (birdY > 0 ) {
				velocity = velocity + gravity;
				birdY -= velocity;

			} else	{
				gameState = 3;
			}

		//GAME STATE - instruction state before game play state
		} else if (gameState == 1) {
			birdAndScore();
			batch.draw(tapToPlay, Gdx.graphics.getWidth() /2 - tapToPlay.getWidth() / 2, Gdx.graphics.getHeight() / 2);

			if(Gdx.input.justTouched()) {
				gameState = 2;
			}

		//GAME STATE - game over
		} else if (gameState == 3) {
			birdAndScore();
			batch.draw(gameOver, Gdx.graphics.getWidth() /2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2);

			if(Gdx.input.justTouched()) {
				//resetting the game sore and state
				gameState = 1;
				startGame();
				score = 0;
				scoringPipe = 0;
				velocity = 0;
			}
		}
		//animation of the flapping bird
		if (flapState == 0) {
			flapState = 1;
		} else if (flapState == 1) {
			flapState = 2;
		} else if (flapState == 2) {
			flapState = 3;
		} else {
			flapState = 0;
		}

		batch.end();
		shapeRenderer.end();
	}
}
