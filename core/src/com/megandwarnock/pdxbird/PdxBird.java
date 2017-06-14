package com.megandwarnock.pdxbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class PdxBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;

	int gameState = 0;
	float gravity = 2;

	Texture topPipe;
	Texture bottomPipe;
	float gap = 400;
	float maxPipeOffset;
	Random randomGenerator;
	float pipeVelocity = 4;
	int numberOfPipes = 4;
	float[] pipeX = new float[numberOfPipes];
	float[] pipeOffset = new float[numberOfPipes];
	float distanceBetweenPipes;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		birds = new Texture[3];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird.png");
		birds[2] = new Texture("bird2.png");
		birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() /2;

		topPipe = new Texture("toptube.png");
		bottomPipe = new Texture("bottomtube.png");
		maxPipeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 -100;
		randomGenerator = new Random();
		distanceBetweenPipes = Gdx.graphics.getWidth() * 3 / 4;

		for(int i = 0; i < numberOfPipes; i++) {
			pipeOffset[i] =  (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			pipeX[i] = Gdx.graphics.getWidth() / 2 - topPipe.getWidth() / 2 + i * distanceBetweenPipes;

		}

	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState != 0) {

			if(Gdx.input.justTouched()) {

				velocity = -30;

			}

			for(int i = 0; i < numberOfPipes; i++) {

				if (pipeX[i] < - topPipe.getWidth()) {

					pipeX[i] += numberOfPipes * distanceBetweenPipes;

				} else {

					pipeX[i] = pipeX[i] - pipeVelocity;

				}


				batch.draw(topPipe, pipeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + pipeOffset[i]);
				batch.draw(bottomPipe, pipeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomPipe.getHeight() + pipeOffset[i]);

			}

			if (birdY > 0 || velocity < 0) {
				velocity = velocity + gravity;
				birdY -= velocity;
			}
		} else {

			if(Gdx.input.justTouched()) {
				gameState = 1;

			}
		}

		if (flapState == 0) {
			flapState = 1;
		} else if (flapState == 1) {
			flapState = 2;
		} else if (flapState == 2) {
			flapState = 0;
		}


		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		batch.end();
	}
}
