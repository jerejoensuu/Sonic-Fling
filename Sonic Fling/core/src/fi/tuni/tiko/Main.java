package fi.tuni.tiko;

import fi.tuni.tiko.util.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;

import java.util.concurrent.TimeUnit;

public class Main extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
	private Texture gameOverText;
	private Sonic player;
	private Eggman enemy;
	private Explosion explosion;

	private Music backgroundMusic;
	private Music gameOverMusic;
	private Sound jump;

	private int gameStart = 154;
	private boolean accelerometer;
	private int stage = 1;
	private int stageEndCountdown = 1;
	private boolean gameOver = false;
	private float fallingSpeed = 0.3f;

	private OrthographicCamera camera;

	@Override
	public void create () {
		batch = new SpriteBatch();
		accelerometer = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
		background = new Texture("Background.jpg");
		gameOverText = new Texture("Game over.png");

		player = new Sonic();
		enemy = new Eggman(0);
		explosion = new Explosion(0, 0, 0, 0);


		camera = new OrthographicCamera();
		camera.setToOrtho(false, 16, 9);

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Green Hill Zone.mp3"));
		gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("Game over.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
		backgroundMusic.setVolume(0.3f);
		gameOverMusic.setVolume(0.3f);
		jump = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));

		Gdx.input.setInputProcessor(new GestureDetector(new GestureDetector.GestureAdapter() {
			@Override
			public boolean fling(float velocityX, float velocityY, int button) {
			if (!player.inAir && gameStart <= 0) {
				player.setVelocityX(velocityX / 4500f);
				player.setVelocityY(velocityY / 4500f * -1);
				player.inAir = true;
				jump.play(0.2f);
			}
			return super.fling(velocityX, velocityY, button);
			}
		}));
	}

	@Override
	public void render () {
		batch.setProjectionMatrix(camera.combined);

		if (gameOver) {
			gameOver();
		} else {


			// stages
			int enemyNumber = 1;
			if (enemy.getList().size() == 0) {
				stageEndCountdown--;
				while (enemy.getList().size() < stage && stageEndCountdown <= 0) {
					enemy.newEnemy(((16f / (stage + 1)) * enemyNumber) - enemy.radius);
					enemyNumber++;
				}
				if (stageEndCountdown <= 0) {
					stage++;
					stageEndCountdown = 120;
				}
			}

			// game start
			if (gameStart >= 0) {
				player.setScale(1.25f);
				player.setY(1f);
				if (gameStart > 14) {
					player.stretchAnimation();
				} else {
					player.crouchAnimation();
				}
				if (gameStart == 0) {
					player.setScale(1f);
					player.setY(0.8f);
				}
				gameStart--;
			} else {
				playerInput();
			}
			enemy.move();

			for (int i = 0; i < enemy.getList().size(); i++) {
				if (player.getHitbox().overlaps(enemy.getList().get(i).getHitbox()) && !player.hitEnemy) {
					if (enemy.getList().get(i).calculateCollision(player.getX(), player.getY(), player.width / 2, player.velocityX, player.velocityY, i)) {
						enemy.hitUnit = i;
					} else {
						explosion.newExplosion(enemy.getList().get(i).getX(),
								enemy.getList().get(i).getY(),
								enemy.getList().get(i).radius,
								enemy.getList().get(i).pulseValue);
					}
					player.calculateCollision(enemy.getX(), enemy.getY(), enemy.radius);
					Gdx.app.log("Collision", "Collision detected");
				}
				if (enemy.getList().get(i).explosionCountdown <= 30) {
					enemy.getList().get(i).explosionCountdown--;
					if (enemy.getList().get(i).explosionCountdown == 0) {
						enemy.getList().remove(i);
					}
				}
			}
			for (int i = 0; i < enemy.getBulletList().size(); i++) {
				if (enemy.getBulletList().get(i).getHitbox().overlaps(player.getHitbox())) {
					if (player.hitBullet(enemy.getBulletList().get(i).getHitbox().getX())) {
						gameOver = true;
						player.deathAnimation(1);
						backgroundMusic.stop();
						break;
					}
					enemy.getBulletList().remove(i);
				}
			}
		}

		batch.begin();

		batch.draw(background, 0, 0, 16, 9);
		enemy.draw(batch);
		explosion.draw(batch);
		player.draw(batch);
		if (gameOver && TimeUnit.MILLISECONDS.convert(System.nanoTime() - player.lastHit, TimeUnit.NANOSECONDS) > 700) {
			batch.draw(gameOverText,
						8 - gameOverText.getWidth() / 100f / 2,
						4.5f - gameOverText.getHeight() / 100f / 2,
						gameOverText.getWidth() / 100f,
						gameOverText.getHeight() / 100f);
		}

		batch.end();
	}

	public void playerInput() {
		if (!accelerometer) {
			if (player.inAir) {
				player.fling();
			} else if((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))) {
				player.changeDirection(player.LEFT);
				player.move();
			} else if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
				player.changeDirection(player.RIGHT);
				player.move();
			} else {
				player.crouch();
				player.setRotation(0);
			}

			/*
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				player.setY(player.getY() + 0.1f);
			} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				player.setY(player.getY() - 0.1f);
			}

			 */
		}

		if (accelerometer) {
			if (player.inAir) {
				player.fling();
			}else if (Gdx.input.getAccelerometerY() > 0.5) {
				player.changeDirection(player.RIGHT);
				player.moveAndroid(Gdx.input.getAccelerometerY());
			} else if (Gdx.input.getAccelerometerY() < -0.5) {
				player.changeDirection(player.LEFT);
				player.moveAndroid(Gdx.input.getAccelerometerY());
			} else {
				player.crouch();
				player.setRotation(0);
			}
		}
	}

	public void gameOver() {
		if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - player.lastHit, TimeUnit.NANOSECONDS) > 700) {
			gameOverMusic.play();
			player.getHitbox().setY(player.getHitbox().getY() + fallingSpeed);
			player.deathAnimation(2);
			fallingSpeed -= 0.0075;
		}
		if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - player.lastHit, TimeUnit.NANOSECONDS) > 9600+700) {
			gameOver = false;
			player.inAir = false;
			player.setScale(1f);
			player.getHitbox().setX(8 - player.getHitbox().getWidth()/2);
			player.getHitbox().setY(0.8f);
			player.health = 5;
			stage = 1;
			stageEndCountdown = 0;
			backgroundMusic.play();
			fallingSpeed = 0.3f;
			enemy.getBulletList().removeAll(enemy.getBulletList());
			enemy.getList().removeAll(enemy.getList());
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		player.dispose();
		enemy.dispose();
	}
}
