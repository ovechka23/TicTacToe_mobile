package com.example.mobile6_1;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int[][] a = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    private int player = 1;
    private int run = 1;
    private int winningState = -1; // Для хранения состояния победы

    public void reset() {
        a = new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        player = 1;
        run = 1;
        winningState = -1;
    }

    public int getRun() {
        return run;
    }

    public void setGameOver() {
        run = 0;
    }

    public void set(int i, int j) {
        a[i][j] = player;
    }

    public int check() {
        for (int i = 0; i < 3; i++) {
            if (a[i][0] != 0 && a[i][0] == a[i][1] && a[i][1] == a[i][2]) {
                winningState = i + 1; // Хранение состояния победы (горизонтальная)
                return a[i][0];
            }
            if (a[0][i] != 0 && a[0][i] == a[1][i] && a[1][i] == a[2][i]) {
                winningState = i + 4; // Хранение состояния победы (вертикальная)
                return a[0][i];
            }
        }
        if (a[0][0] != 0 && a[0][0] == a[1][1] && a[1][1] == a[2][2]) {
            winningState = 7; // Диагональ
            return a[0][0];
        }
        if (a[0][2] != 0 && a[0][2] == a[1][1] && a[1][1] == a[2][0]) {
            winningState = 8; // Заднюю диагональ
            return a[0][2];
        }

        for (int[] row : a) {
            for (int cell : row) {
                if (cell == 0) {
                    winningState = -1; // Сброс состояния если игра продолжается
                    return 0; // Игра продолжается
                }
            }
        }
        return 9; // Ничья
    }

    public int getWinningState() {
        return winningState;
    }

    public int get(int i, int j) {
        return a[i][j];
    }

    public int getPlayer() {
        return player;
    }

    public void changePlayer() {
        player = (player == 1) ? 2 : 1;
    }

    public List<Integer> getWinners() {
        List<Integer> winners = new ArrayList<>();
        return winners;
    }
}
