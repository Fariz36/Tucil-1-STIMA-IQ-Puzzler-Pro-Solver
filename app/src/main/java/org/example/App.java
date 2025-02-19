package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import javafx.util.Pair;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    // @Override
    // public void start(Stage primaryStage) {
    //     Label label = new Label("Hello, JavaFX with Gradle!");
    //     StackPane root = new StackPane(label);
    //     Scene scene = new Scene(root, 400, 300);

    //     primaryStage.setTitle("JavaFX App");
    //     primaryStage.setScene(scene);
    //     primaryStage.show();
    // }

    public static boolean isCapitalLetter(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static boolean isSameTetromino(String a, String b) {
        char aChar = '$', bChar = '$';
        for (int i = 0; i < a.length(); i++) {
            if (isCapitalLetter(a.charAt(i))) {
                if (aChar != '$' && aChar != a.charAt(i)) {
                    throw new IllegalArgumentException("Invalid Tetromino : Exist different characters in the same Tetromino");
                }
                else aChar = a.charAt(i);
            }
        }
        for (int i = 0; i < b.length(); i++) {
            if (b.charAt(i) != ' ') {
                if (bChar != '$' && bChar != b.charAt(i)) {
                    throw new IllegalArgumentException("Invalid Tetromino : Exist different characters in the same Tetromino");
                }
                else bChar = b.charAt(i);
            }
        }
        return aChar == bChar;
    }

    public static char getCharID(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (isCapitalLetter(s.charAt(i))) {
                return s.charAt(i);
            }
        }
        throw new IllegalArgumentException("Invalid Tetromino : No capital letter found");
    }

    public static Tetromino processTetromino(List<String> tetromino) {
        int n = tetromino.size();
        int m = 0;
        for (int i = 0; i < n; i++) {
            m = Math.max(m, tetromino.get(i).length());
        }

        int rootn = -1, rootm = -1;

        boolean[][] arr = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (j < tetromino.get(i).length()) {
                    arr[i][j] = isCapitalLetter(tetromino.get(i).charAt(j));

                    if (isCapitalLetter(tetromino.get(i).charAt(j))) {
                        rootn = i;
                        rootm = j;
                    }
                }
                else {
                    arr[i][j] = false;
                }
            }
        }

        if (rootn == -1 || rootm == -1) {
            throw new IllegalArgumentException("Invalid Tetromino : Found empty tetromino");
        }

        //Check wether given tetromino is connected or not
        boolean[][] visited = new boolean[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                visited[i][j] = false;
            }
        }
        
        Queue<Pair<Integer, Integer>> q = new LinkedList<Pair<Integer, Integer>>();
        q.add(new Pair<Integer, Integer>(rootn, rootm));
        visited[rootn][rootm] = true;

        while (!q.isEmpty()) {
            Pair<Integer, Integer> p = q.poll();
            int x = p.getKey();
            int y = p.getValue();

            if (x > 0 && !visited[x-1][y] && arr[x-1][y]) {
                q.add(new Pair<Integer, Integer>(x-1, y));
                visited[x-1][y] = true;
            }
            if (x < n-1 && !visited[x+1][y] && arr[x+1][y]) {
                q.add(new Pair<Integer, Integer>(x+1, y));
                visited[x+1][y] = true;
            }
            if (y > 0 && !visited[x][y-1] && arr[x][y-1]) {
                q.add(new Pair<Integer, Integer>(x, y-1));
                visited[x][y-1] = true;
            }
            if (y < m-1 && !visited[x][y+1] && arr[x][y+1]) {
                q.add(new Pair<Integer, Integer>(x, y+1));
                visited[x][y+1] = true;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (arr[i][j] && !visited[i][j]) {
                    throw new IllegalArgumentException("Invalid Tetromino : Tetromino is not connected");
                }
            }
        }


        // System.out.println("n : " + n);
        // System.out.println("m : " + m);
        // System.out.println("arr : ");
        // for (int i = 0; i < n; i++) {
        //     for (int j = 0; j < m; j++) {
        //         System.out.print(arr[i][j] + " ");
        //     }
        //     System.out.println();
        // }

        return new Tetromino(n, m, arr);
    }

    public static Tetromino[] ProcessTetrominos(ArrayList<String> UnprocessedTetrominos, int tetrominoCount) {
        boolean[] isUsed = new boolean[26];
        for (int i = 0; i < 26; i++) {
            isUsed[i] = false;
        }
        Tetromino[] tetrominos = new Tetromino[tetrominoCount + 1];
        
        int count = 1;
        for (int i = 0; i < UnprocessedTetrominos.size(); i++) {
            int j = i;
            
            while (isSameTetromino(UnprocessedTetrominos.get(i), UnprocessedTetrominos.get(j))) {
                j++;
                if (j == UnprocessedTetrominos.size()) break;
            }

           // System.out.println(count + " : " + i + " " + j);

            char tetrominoID = getCharID(UnprocessedTetrominos.get(i));
            if (isUsed[tetrominoID - 'A']) {
                throw new IllegalArgumentException("Invalid Tetromino : Multiple tetromino with same alphabet code");
            }

            if (count > tetrominoCount) {
                throw new IllegalArgumentException("Invalid Tetromino : Too many tetrominos");
            }
            tetrominos[count++] = processTetromino(UnprocessedTetrominos.subList(i, j));
            isUsed[tetrominoID - 'A'] = true;

            i = j-1;
        }

        if (count - 1 != tetrominoCount) {
            throw new IllegalArgumentException("Invalid Tetromino : Number of tetrominos does not match with the given number");
        }

        return tetrominos;
    }

    public static void main(String[] args) {
        //launch(args);
        
        int n = -1, m = -1, p = -1;    
        String s = "";
        ArrayList<String> UnprocessedTetrominos = new ArrayList<String>();

        boolean first = true;
        boolean second = true;
        String filePath = "app\\src\\main\\java\\org\\example\\input.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    String[] tokens = line.trim().split("\\s+");
                    
                    n = Integer.parseInt(tokens[0]);
                    m = Integer.parseInt(tokens[1]);
                    p = Integer.parseInt(tokens[2]);
                    first = false;
                }
                else if (second) {
                    s = line;
                    second = false;
                }
                else {
                    UnprocessedTetrominos.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("n : " + n);
        System.out.println("m : " + m);
        System.out.println("p : " + p);
        System.out.println("s : " + s);
        System.out.println("UnprocessedTetrominos : ");
        for (String tetromino : UnprocessedTetrominos) {
            System.out.println(tetromino);
        }

        Tetromino[] tetrominos = ProcessTetrominos(UnprocessedTetrominos, p);
        System.out.println("ProcessedTetrominos : ");
        for (int i = 1; i <= p; i++) {
            System.out.println("Tetromino " + tetrominos[i].id + " : ");
            tetrominos[i].printTetromino();
        }

        int totalarea = 0;
        for (int i = 1; i <= p; i++) {
            for (int j = 1; j <= tetrominos[i].n; j++) {
                for (int k = 1; k <= tetrominos[i].m; k++) {
                    if (tetrominos[i].get(j, k)) {
                        totalarea++;
                    }
                }
            }
        }

        //validate total area
        // if (totalarea != n*m) {
        //     throw new IllegalArgumentException("Invalid Tetromino : Total area of tetrominos does not match with the given area, " + totalarea + " vs " + n*m);
        // }

        Block block = new Block(tetrominos, n, m, p);
        block.solve();
    }
}
