package org.example;
import java.awt.Color;


public class Block {
  int n, m, tetrominoCount;
  int[][] arr;
  String[] color;
  Tetromino[] tetrominos;
  boolean foundSolution = false;
  int bruteForceCount = 0;

  public Block(Tetromino[] tetrominos, int n, int m, int tetrominoCount) {
    this.n = n;
    this.m = m;
    this.tetrominoCount = tetrominoCount;
    this.tetrominos = new Tetromino[tetrominoCount+2];

    this.arr = new int[n+2][m+2];
    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        arr[i][j] = -1;
      }
    }

    for (int i = 1; i <= tetrominoCount; i++) {
      this.tetrominos[i] = tetrominos[i];
    }

    this.color = new String[27];
    for (int i = 1; i <= 26; i++) {
      int j = (5*i % 26) + 1;
      float hue = (float) j / 26;
      Color color = Color.getHSBColor(hue, 1.0f, 1.0f);

      this.color[i] = "\u001B[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }
  }

  public void set(int x, int y, int value) {
    arr[x][y] = value;
  }

  public int get(int x, int y) {
    return arr[x][y];
  }

  public boolean isValidPosition(int x, int y) {
    return x >= 1 && x <= n && y >= 1 && y <= m && arr[x][y] == -1;
  }

  public boolean placeTetromino(Tetromino tetromino, int x, int y) {
    for (int i = 1; i <= tetromino.n; i++) {
      for (int j = 1; j <= tetromino.m; j++) {
        if (tetromino.get(i, j)) {
          if (!isValidPosition(x+i-1, y+j-1)) {
            return false;
          }
        }
      }
    }

    for (int i = 1; i <= tetromino.n; i++) {
      for (int j = 1; j <= tetromino.m; j++) {
        if (tetromino.get(i, j)) {
          set(x+i-1, y+j-1, tetromino.id);
        }
      }
    }

    return true;
  }

  public void removeTetromino(Tetromino tetromino, int x, int y) {
    for (int i = 1; i <= tetromino.n; i++) {
      for (int j = 1; j <= tetromino.m; j++) {
        if (tetromino.get(i, j)) {
          set(x+i-1, y+j-1, -1);
        }
      }
    }
  }

  public void printBlock() {
    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        if (arr[i][j] == -1) {
          System.out.print("  ");
        } else {
          System.out.print(color[arr[i][j]] + (char)('A' + arr[i][j] - 1) + "\u001B[0m");
        }
      }
      System.out.println();
    }
  }

  public void findSolution(int i, int j, boolean[] used) {
    this.bruteForceCount++;
    if (i == n+1) {
      printBlock();
      this.foundSolution = true;
    }
    else {
      for (int cur_tetromino = 1; cur_tetromino <= this.tetrominoCount; cur_tetromino++) {
        if (!used[cur_tetromino]) {
          used[cur_tetromino] = true;
          Tetromino tetromino = this.tetrominos[cur_tetromino];
          for (int rot = 0; rot < 4; rot++) {
            for (int flip = 0; flip < 2; flip++) {
              if (placeTetromino(tetromino, i, j)) {
                int nexti = i;
                int nextj = j+1;
                
                while (nexti <= this.n) {
                  while (nextj <= this.m && arr[nexti][nextj] != -1) {
                    nextj++;
                  }
                  if (nextj > this.m) {
                    nextj = 1;
                    nexti++;
                  } 
                  else {
                    break;
                  }
                }

                findSolution(nexti, nextj, used);
                if (this.foundSolution) return;
                removeTetromino(tetromino, i, j);
              }
              if (flip != 1) tetromino = tetromino.flip();
            }
            if (rot != 3) tetromino = tetromino.rotate();
          }
          used[cur_tetromino] = false;
        }
      }
    }
  }

  public void solve() {
    boolean[] used = new boolean[this.tetrominoCount+1];
    this.foundSolution = false;
    this.bruteForceCount = 0;
    long startTime = System.currentTimeMillis();

    findSolution(1, 1, used);

    long endTime = System.currentTimeMillis();
    System.out.println("Execution time : " + (endTime - startTime) + " ms");
    System.out.println("Case searched : " + this.bruteForceCount);
    if (!this.foundSolution) {
      System.out.println("No solution found");
    }
  }
}