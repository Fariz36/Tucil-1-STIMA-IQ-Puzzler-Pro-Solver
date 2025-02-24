package org.example;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class Block {
  int n, m, tetrominoCount;
  int[][] arr;
  String[] color;
  int[] RGBinteger;
  Tetromino[] tetrominos;
  boolean foundSolution = false;
  int bruteForceCount = 0;
  long startTime;
  long endTime;
  int apalah = 0;

  public Block(Tetromino[] tetrominos, int n, int m, int tetrominoCount, boolean arr[][]) {
    this.n = n;
    this.m = m;
    this.tetrominoCount = tetrominoCount;
    this.tetrominos = new Tetromino[tetrominoCount+2];

    this.arr = new int[n+2][m+2];
    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        if (!arr[i-1][j-1]) {
          this.arr[i][j] = -1;
        } else {
          this.arr[i][j] = -2;
        }
      }
    }

    for (int i = 1; i <= tetrominoCount; i++) {
      this.tetrominos[i] = tetrominos[i];
    }

    this.color = new String[27];
    this.RGBinteger = new int[27];
    for (int i = 1; i <= 26; i++) {
      int j = (5*i % 26) + 1;
      float hue = (float) j / 26;
      Color tempcolor = Color.getHSBColor(hue, 1.0f, 1.0f);

      this.color[i] = "\u001B[38;2;" + tempcolor.getRed() + ";" + tempcolor.getGreen() + ";" + tempcolor.getBlue() + "m";
      this.RGBinteger[i] = tempcolor.getRGB();
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

  public boolean placeTetromino(Tetromino tetromino, int a, int b, int x, int y) {
    boolean isPlaced = false;
    for (int i = 1; i <= tetromino.n; i++) {
      for (int j = 1; j <= tetromino.m; j++) {
        if (tetromino.get(i, j)) {
          if (!isValidPosition(x+i-1, y+j-1)) {
            return false;
          }
        }

        if (x+i-1 == a && y+j-1 == b) {
          if (tetromino.get(i, j)) {
            isPlaced = true;
          }
        }
      }
    }

    if (!isPlaced) {
      return false;
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
        if (arr[i][j] < 0) {
          System.out.print("#");
        } else {
          System.out.print(color[arr[i][j]] + (char)('A' + arr[i][j] - 1) + "\u001B[0m");
        }
      }
      System.out.println();
    }


    //make a new stage
    GridPane grid = new GridPane();
    grid.setHgap(5.0);
    grid.setVgap(5.0);

    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        if (arr[i][j] < 0) {
          Rectangle rect = new Rectangle(50, 50);
          rect.setFill(javafx.scene.paint.Color.BLACK);
          rect.setStroke(javafx.scene.paint.Color.BLACK);
          rect.setStrokeWidth(1.0);
          grid.add(rect, j-1, i-1);
          continue;
        }
        Rectangle rect = new Rectangle(50, 50);
        Color tempcolor = new Color(RGBinteger[arr[i][j]]);

        javafx.scene.paint.Color FXcolor = javafx.scene.paint.Color.rgb(tempcolor.getRed(), tempcolor.getGreen(), tempcolor.getBlue());
        rect.setFill(FXcolor);

        rect.setStroke(javafx.scene.paint.Color.BLACK);
        rect.setStrokeWidth(1.0);
        
        Text text = new Text(25, 25, (char)('A' + arr[i][j] - 1) + "");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        StackPane Spane = new StackPane(rect, text);
        Spane.setStyle("-fx-alignment: center; -fy-alignment: center;");

        grid.add(Spane, j-1, i-1);
      }
    }

    this.endTime = System.currentTimeMillis();

    Button SaveResult = new Button("Save as Image");
    SaveResult.setOnAction(e -> {
      WritableImage image = new WritableImage((int) grid.getWidth(), (int) grid.getHeight());
      grid.snapshot(new SnapshotParameters(), image);

      PixelReader pixelReader = image.getPixelReader();

      int width = (int) image.getWidth();
      int height = (int) image.getHeight();

      BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      
      int[] pixels = new int[width * height];
      pixelReader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

      // Set pixel data
      bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

      File file = new File("result.png");
      try {
        ImageIO.write(bufferedImage, "png", file);
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      String ResultPath = file.getAbsolutePath();

      //print path
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Information Dialog");
      alert.setHeaderText(null);
      alert.setContentText("Image saved as result.png at : " + ResultPath);
      alert.showAndWait();
    });
      
    HBox GridHBox = new HBox(20, grid);
    GridHBox.setStyle("-fy-padding:20px; -fx-padding:20px; -fx-alignment: center; -fy-alignment: center;");

    Label resultLabel = new Label("Execution time : " + (this.endTime - this.startTime) + " ms\nCase searched : " + this.bruteForceCount);
    resultLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

    VBox ResultLayout = new VBox(20, resultLabel, GridHBox, SaveResult);
    ResultLayout.setStyle("-fx-background-color:hsl(0, 0.00%, 100.00%); -fy-padding:20px; -fx-padding:20px; -fx-alignment: center; -fy-alignment: center;");

    Scene resultScene = new Scene(ResultLayout, 600, 600);
    Stage resultStage = new Stage();
    resultStage.setScene(resultScene);
    resultStage.show();
  }

  public void findSolution(int i, int j, boolean[] used) {
    int count = 0;
    for (int x = 1; x <= this.tetrominoCount; x++) {
      if (!used[x]) {
        count++;
      }
    }
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
              for (int shiftx = -tetromino.n+1; shiftx <= tetromino.n-1; shiftx++) {
                for (int shifty = -tetromino.m+1; shifty <= tetromino.m-1; shifty++) {
                  this.bruteForceCount++;
                  if (placeTetromino(tetromino, i, j, i+shiftx, j+shifty)) {
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
                    removeTetromino(tetromino, i+shiftx, j+shifty);
                  }
                }
              }
              tetromino = tetromino.flip();
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
    this.startTime = System.currentTimeMillis();

    // for (int i = 1; i <= this.tetrominoCount; i++) {
    //   Tetromino cur = this.tetrominos[i];
    //   System.err.println("1 : ");
    //   cur.printTetromino();
    //   cur = cur.rotate();
    //   System.err.println("1 : ");
    //   cur.printTetromino();
    //   cur = cur.rotate();
    //   System.err.println("1 : ");
    //   cur.printTetromino();
    //   cur = cur.rotate();
    //   System.err.println("1 : ");
    //   cur.printTetromino();
    //   System.err.println("----------------------------------");
    // }

    int posi = 1, posj = 1;
    while (posi <= this.n) {
      while (posj <= this.m && arr[posi][posj] != -1) {
        posj++;
      }
      if (posj > this.m) {
        posj = 1;
        posi++;
      } 
      else {
        break;
      }
    }
    System.out.println("Starting from " + posi + " " + posj);
    findSolution(posi, posj, used);

    System.out.println("Execution time : " + (this.endTime - this.startTime) + " ms");
    System.out.println("Case searched : " + this.bruteForceCount);
    if (!this.foundSolution) {
      System.out.println("No solution found");

      this.endTime = System.currentTimeMillis();

      Label resultLabel = new Label("No Solution Found\nExecution time : " + (this.endTime - this.startTime) + " ms\nCase searched : " + this.bruteForceCount);
      resultLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

      VBox ResultLayout = new VBox(20, resultLabel);
      ResultLayout.setStyle("-fx-background-color:hsl(0, 0.00%, 100.00%); -fy-padding:20px; -fx-padding:20px; -fx-alignment: center; -fy-alignment: center;");

      Scene resultScene = new Scene(ResultLayout, 400, 400);
      Stage resultStage = new Stage();
      resultStage.setScene(resultScene);
      resultStage.show();
    }
  }
}