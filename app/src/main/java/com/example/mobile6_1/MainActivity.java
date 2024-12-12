package com.example.mobile6_1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private MediaPlayer backgroundMusic;
    private MediaPlayer moveSound;
    private MediaPlayer winSound;
    private MediaPlayer drawSound;
    private boolean isMusicEnabled;
    private boolean isSoundEffectsEnabled;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        loadSettings();

        game = new Game();
        DrawView drawView = new DrawView(this, game);
        setContentView(drawView);

        backgroundMusic = MediaPlayer.create(this, R.raw.background);
        moveSound = MediaPlayer.create(this, R.raw.move);
        winSound = MediaPlayer.create(this, R.raw.win);
        drawSound = MediaPlayer.create(this, R.raw.draw);

        if (isMusicEnabled) {
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }

        // Кнопка для меню настроек
        Button settingsButton = new Button(this);
        settingsButton.setText("Настройки");
        settingsButton.setOnClickListener(v -> showSettingsMenu());
        addContentView(settingsButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Кнопка "Начать игру заново"
        Button restartButton = new Button(this);
        restartButton.setText("Начать игру заново");
        restartButton.setOnClickListener(v -> restartGame(drawView)); // Вызываем метод для перезапуска игры
        addContentView(restartButton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Задаем расположение кнопок

        restartButton.setX(10);
        restartButton.setY(500);

        settingsButton.setX(10);
        settingsButton.setY(600);
    }

    private void restartGame(DrawView drawView) {
        game.reset(); // Создаем новое состояние игры
        drawView.invalidate(); // Перерисовываем поле
        Log.d("MainActivity", "Game restarted.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundMusic != null) {
            backgroundMusic.release();
        }
        if (moveSound != null) {
            moveSound.release();
        }
        if (winSound != null) {
            winSound.release();
        }
        if (drawSound != null) {
            drawSound.release();
        }
    }

    private void showSettingsMenu() {
        final String[] options = {"Включить музыку", "Включить звуковые эффекты"};
        final boolean[] checkedItems = {isMusicEnabled, isSoundEffectsEnabled};

        new AlertDialog.Builder(this)
                .setTitle("Настройки")
                .setMultiChoiceItems(options, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isMusicEnabled = checkedItems[0];
                        isSoundEffectsEnabled = checkedItems[1];
                        saveSettings();
                        if (isMusicEnabled) {
                            backgroundMusic.start();
                        } else {
                            backgroundMusic.pause();
                        }
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isMusicEnabled", isMusicEnabled);
        editor.putBoolean("isSoundEffectsEnabled", isSoundEffectsEnabled);
        editor.apply();
    }

    private void loadSettings() {
        isMusicEnabled = sharedPreferences.getBoolean("isMusicEnabled", true);
        isSoundEffectsEnabled = sharedPreferences.getBoolean("isSoundEffectsEnabled", true);
    }

    class DrawView extends View {
        Paint paint = new Paint();
        Game game;
        Bitmap bitmapX;
        Bitmap bitmapO;
        int bitmapWidth;
        int bitmapHeight;

        public DrawView(Context context, Game game) {
            super(context);
            this.game = game;

            bitmapX = BitmapFactory.decodeResource(getResources(), R.drawable.x);
            bitmapO = BitmapFactory.decodeResource(getResources(), R.drawable.o);

            bitmapWidth = 150;
            bitmapHeight = 150;

            bitmapX = Bitmap.createScaledBitmap(bitmapX, bitmapWidth, bitmapHeight, true);
            bitmapO = Bitmap.createScaledBitmap(bitmapO, bitmapWidth, bitmapHeight, true);
        }

        private void drawPic1(Canvas canvas, int x, int y) {
            canvas.drawBitmap(bitmapO, x, y, paint);
        }

        private void drawPic2(Canvas canvas, int x, int y) {
            canvas.drawBitmap(bitmapX, x, y, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (game.getRun() != 0) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int w = metrics.widthPixels;

                    int i = (int) (x / (w / 3));
                    int j = (int) (y / (w / 3));

                    if (game.get(i, j) == 0) {
                        game.set(i, j);
                        if (isSoundEffectsEnabled) {
                            moveSound.start();
                        }
                        if (game.check() != 0) {
                            game.setGameOver();
                            if (game.check() == 9 && isSoundEffectsEnabled) {
                                drawSound.start();
                            } else if (isSoundEffectsEnabled) {
                                winSound.start();
                            }
                        } else {
                            game.changePlayer();
                        }
                        invalidate();
                    }
                }
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int w = metrics.widthPixels;

            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5);
            for (int i = 1; i < 3; i++) {
                canvas.drawLine(i * w / 3, 0, i * w / 3, w, paint);
                canvas.drawLine(0, i * w / 3, w, i * w / 3, paint);
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int x = (i * w / 3) + (w / 3 - bitmapWidth) / 2;
                    int y = (j * w / 3) + (w / 3 - bitmapHeight) / 2;
                    if (game.get(i, j) == 1) {
                        drawPic1(canvas, x, y);
                    } else if (game.get(i, j) == 2) {
                        drawPic2(canvas, x, y);
                    }
                }
            }

            drawWinningLine(canvas);

            String message = "";
            int state = game.check();
            if (state == 0) {
                message = "Ход игрока " + game.getPlayer();
            } else if (state != 9) {
                message = "Победил игрок " + ((state - 1) % 2 + 1);
            } else {
                message = "Ничья";
            }

            paint.setTextSize(50);
            paint.setColor(Color.BLACK);
            canvas.drawText(message, 10, w + 50, paint);
        }

        private void drawWinningLine(Canvas canvas) {
            int state = game.getWinningState();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);

            float cellSize = canvas.getWidth() / 3; // Размер одной клетки

            if (state >= 1 && state <= 3) { // Вертикальная линия
                float x = (state - 1) * cellSize + cellSize / 2; // Центр клетки
                canvas.drawLine(x, 0, x, canvas.getHeight()/2, paint);
            } else if (state >= 4 && state <= 6) { // Горизонтальная линия
                float y = (state - 4) * cellSize + cellSize / 2; // Центр клетки
                canvas.drawLine(0, y, canvas.getWidth(), y, paint);
            } else if (state == 7) { // Диагональ /
                canvas.drawLine(0, 0, canvas.getWidth(), canvas.getHeight()/2, paint);
            } else if (state == 8) { // Диагональ \
                canvas.drawLine(0, canvas.getHeight()/2, canvas.getWidth(), 0, paint);
            }
        }
    }
}

