package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;

public class App extends Application  {
    int N = 0, M = 0, P = 0, i;
    String s;
    ArrayList<boolean[][]> tetrominos = new ArrayList<boolean[][]>();
    ArrayList<TetrominoPage> tetrominoPages = new ArrayList<TetrominoPage>();
    boolean[] isSaved = new boolean[26];
    String ConfigPath;

    public String getGreeting() {
        return "Hello World!";
    }

    public void loadConfig(String path) {

    }

    public void writeConfig() {
        String configPath = Paths.get("input.txt").toAbsolutePath().toString();
        
        File configFile = new File(configPath);
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(this.N + " " + this.M + " " + this.P + "\n");
            writer.write("DEFAULT\n");

            for (int i = 0; i < this.P; i++) {
                for (int j = 0; j < tetrominos.get(i).length; j++) {
                    for (int k = 0; k < tetrominos.get(i)[j].length; k++) {
                        if (tetrominos.get(i)[j][k]) {
                            writer.write((char)('A' + i));
                        }
                        else {
                            writer.write(" ");
                        }
                    }
                    writer.write("\n");
                }
            }

            System.out.println("Config saved at: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class TetrominoPage {
        int n, m;
        boolean[][] arr;
        int id;
        Button openTetrominoConfig;
        Button submitTetromino;
        TextField nField;
        TextField mField;
        Stage tetrominoStage;
        GridPane tetrominoGrid;
        VBox tetrominoLayout;
        Scene tetrominoScene;
    

        //constructor
        public TetrominoPage(int id) {
            this.n = 5;
            this.m = 5;
            this.arr = new boolean[5][5];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    this.arr[i][j] = false;
                }
            }
            this.id = id;

            Label nLabel = new Label("N : ");
            nField = new TextField();
            nField.setText("" + this.n);
            Label mLabel = new Label("M : ");
            mField = new TextField();
            mField.setText("" + this.m);

            HBox n_HBox = new HBox(10, nLabel, nField);
            n_HBox.setAlignment(Pos.CENTER);
            HBox m_HBox = new HBox(10, mLabel, mField);
            m_HBox.setAlignment(Pos.CENTER);
            Button saveSize = new Button("Save Size");
            saveSize.setOnAction(e -> {
                try {
                    int befn = this.n;
                    int befm = this.m;

                    this.n = Integer.parseInt(nField.getText());
                    this.m = Integer.parseInt(mField.getText());

                    //change the array
                    boolean[][] temp_arr = new boolean[this.n][this.m];
                    for (int i = 0; i < this.n; i++) {
                        for (int j = 0; j < this.m; j++) {
                            if (i < befn && j < befm) {
                                temp_arr[i][j] = this.arr[i][j];
                            }
                            else {
                                temp_arr[i][j] = false;
                            }
                        }
                    }
                    this.arr = temp_arr;


                    //refresh the page
                    this.tetrominoStage.close();
                    this.tetrominoStage = null;

                    this.openTetrominoConfig.fire();
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid input format");
                }
            });

            VBox NMBox = new VBox(10, n_HBox, m_HBox, saveSize);
            NMBox.setStyle("-fx-padding: 20; -fx-alignment: center; -fy-alignment: center;");

            openTetrominoConfig = new Button("Block " + (id+1));
            openTetrominoConfig.setOnAction(event -> {
                this.tetrominoGrid = new GridPane();
                int tetrominoRows = this.n;
                int tetrominoCols = this.m;

                for (int j = 0; j < tetrominoRows; j++) {
                    for (int k = 0; k < tetrominoCols; k++) {
                        Button cellButton = new Button();
                        cellButton.setMinSize(30, 30);
                        updateCellAppearance(cellButton, this.arr[j][k]);
                        int row = j;
                        int col = k;
                        cellButton.setOnAction(cellButtonAction -> {
                            this.arr[row][col] = !this.arr[row][col];
                            updateCellAppearance(cellButton, this.arr[row][col]);
                        });
                        this.tetrominoGrid.add(cellButton, k, j);
                    }
                }

                this.submitTetromino = new Button("Save Tetromino");
                submitTetromino.setOnAction(submitEvent -> {
                    tetrominos.set(id, this.arr);
                    isSaved[id] = true;
                    this.tetrominoStage.close();
                });

                this.tetrominoLayout = new VBox(20, NMBox, this.tetrominoGrid, this.submitTetromino);
                this.tetrominoLayout.setStyle("-fx-padding: 20;");
                this.tetrominoLayout.setAlignment(Pos.CENTER);
                NMBox.setAlignment(Pos.CENTER);
                tetrominoGrid.setAlignment(Pos.CENTER);

                this.tetrominoScene = new Scene(this.tetrominoLayout, 400, 400);
                
                this.tetrominoStage = new Stage();
                this.tetrominoStage.setScene(tetrominoScene);
                this.tetrominoStage.setTitle("Tetromino " + (id+1));
                this.tetrominoStage.show();
            });
        }

        //destructor
        void destroy() {
            System.out.println("Destroying TetrominoPage: " + id);
    
            // Close the stage (if open)
            if (tetrominoStage != null) {
                tetrominoStage.close();
            }
    
            // Nullify references to help garbage collection
            openTetrominoConfig = null;
            nField = null;
            mField = null;
            arr = null;
        }
    }

    private void updateCellAppearance(Button cellButton, boolean state) {
        if (state) {
            cellButton.setStyle("-fx-background-color: #0000FF; -fx-border-color: black;");
        } else {
            cellButton.setStyle("-fx-background-color: #ADD8E6; -fx-border-color: black;");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        //startBruteforce();

        for (int now = 0; now < 26; now++) {
            isSaved[now] = false;
        }

        Button uploadConfig = new Button ("Upload Config");
        Label fileLabel = new Label("No file selected");
        uploadConfig.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                fileLabel.setText("Selected File: " + file.getName());
                System.out.println("File Path: " + file.getAbsolutePath());

                ConfigPath = file.getAbsolutePath();
            }
            else {
                fileLabel.setText("No file selected");
            }
        });
        
        Button load = new Button("Load Config");
        load.setOnAction(e -> {
            if (ConfigPath == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Invalid input file");
                alert.setContentText("Please upload the config file first");
                alert.showAndWait();
                throw new IllegalArgumentException("You haven't upload the config file, please upload the config file first");
            }

            this.loadConfig(ConfigPath);
        });

        Button startButton = new Button("Start Finding Solution!");
        startButton.setOnAction(e -> {
            this.writeConfig();

            //alert if not all tetromino is saved
            for (int now = 0; now < P; now++) {
                if (!isSaved[now]) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Invalid input P");
                    alert.setContentText("Please save all tetrominos");
                    alert.showAndWait();
                    throw new IllegalArgumentException("You haven't save all tetromino, please save all tetrominos");
                }
            }

            //start the bruteforce algorithm
            startBruteforce();
        });

        // HBox gridWrapper = new HBox(gridPane);
        // gridWrapper.setAlignment(javafx.geometry.Pos.CENTER);

        TextField nField = new TextField("" + N);
        Label nLabel = new Label("N : ");
        TextField mField = new TextField("" + M);
        Label mLabel = new Label("M : ");
        TextField pField = new TextField("" + P);
        Label pLabel = new Label("P : ");
        Button SubmitNMP = new Button("Submit");

        HBox TetrominoHBox = new HBox(10);

        SubmitNMP.setOnAction(e -> {
            try {
                int befP = P;
                N = Integer.parseInt(nField.getText());
                M = Integer.parseInt(mField.getText());
                P = Integer.parseInt(pField.getText());

                if (P > 26) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Invalid input P");
                    alert.setContentText("P must be less than or equal to 26");
                    alert.showAndWait();
                    throw new IllegalArgumentException("Invalid input P : P must be less than or equal to 26");
                }

                if (befP < P) {
                    for (i = befP; i < P; i++) {
                        tetrominos.add(new boolean[5][5]);
                        tetrominoPages.add(new TetrominoPage(i));
                        TetrominoHBox.getChildren().add(tetrominoPages.get(i).openTetrominoConfig);
                    }
                }
                else {
                    System.out.println("P : " + P);
                    System.out.println("befP : " + befP);
                    for (int now = befP-1; now >= P; now--) {
                        System.out.println("now : " + now);
                        TetrominoHBox.getChildren().remove(TetrominoHBox.getChildren().size()-1);
                        tetrominos.remove(tetrominos.size()-1);
                        tetrominoPages.get(now).destroy();
                        tetrominoPages.remove(now);
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input format");
            }
        });

        HBox n_HBox = new HBox(10, nLabel, nField);
        HBox m_HBox = new HBox(10, mLabel, mField);
        HBox p_HBox = new HBox(10, pLabel, pField);

        VBox NMPField = new VBox(10, n_HBox, m_HBox, p_HBox);
        HBox NPMWrapper = new HBox(40, NMPField, SubmitNMP);
        NPMWrapper.setStyle("-fx-padding: 20; -fx-alignment: center; -fy-alignment: center;");

        VBox layout = new VBox(20, uploadConfig, load, fileLabel, NPMWrapper, TetrominoHBox, startButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        TetrominoHBox.setAlignment(Pos.CENTER);

        // Set up the stage
        Scene scene = new Scene(layout, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("File Upload Example");
        primaryStage.show();
    }

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
                    else if (tetromino.get(i).charAt(j) != ' ') {
                        throw new IllegalArgumentException("Invalid Tetromino : Found invalid character");
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
            boolean isBlank = true;
            for (int j = 0; j < UnprocessedTetrominos.get(i).length(); j++) {
                if (UnprocessedTetrominos.get(i).charAt(j) != ' ') {
                    isBlank = false;
                    break;
                }
            }
            if (isBlank) continue;
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

    public static void startBruteforce() {
        int n = -1, m = -1, p = -1;    
        String s = "";
        ArrayList<String> UnprocessedTetrominos = new ArrayList<String>();

        boolean first = true;
        boolean second = true;
        String filePath = "input.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    String[] tokens = line.trim().split("\\s+");
                    
                    try {
                        if (tokens.length != 3) {
                            throw new IllegalArgumentException("Invalid input format");
                        }
                        n = Integer.parseInt(tokens[0]);
                        m = Integer.parseInt(tokens[1]);
                        p = Integer.parseInt(tokens[2]);
                        first = false;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid input format");
                    }
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

        int max_length = 0;
        for (int i = 1; i <= p; i++) {
            max_length = Math.max(max_length, Math.max(tetrominos[i].n, tetrominos[i].m));
        }
        if (max_length > Math.max(n, m)) {
            throw new IllegalArgumentException("Invalid Tetromino : Tetromino is too big");
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
        if (totalarea != n*m) {
            throw new IllegalArgumentException("Invalid Tetromino : Total area of tetrominos does not match with the given area, " + totalarea + " vs " + n*m);
        }

        Block block = new Block(tetrominos, n, m, p);
        block.solve();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
