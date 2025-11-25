package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.MusicPlayer;

/**
 * Main menu screen for the Yu-Gi-Oh! game application.
 * <p>
 * This class serves as the entry point for the JavaFX application, displaying
 * the main menu where players can start a duel, access options (future feature),
 * or quit the game. It extends {@link Application} to integrate with the JavaFX
 * application lifecycle.
 * </p>
 * <p>
 * The menu features:
 * <ul>
 * <li>A Yu-Gi-Oh! logo displayed at the top</li>
 * <li>A "Start Duel" button that launches the game board</li>
 * <li>A "Quit" button to exit the application</li>
 * <li>Background music that plays automatically when the menu opens</li>
 * <li>Styled UI elements loaded from an external CSS file</li>
 * </ul>
 * </p>
 * <p>
 * The menu uses a simple vertical layout (VBox) with consistent spacing and
 * centered alignment. All visual assets (logo, CSS) are loaded from the
 * application's resources directory.
 * </p>
 * <p>
 * <strong>Required Resources:</strong>
 * <ul>
 * <li>{@code /ui/logo.png} - The Yu-Gi-Oh! logo image</li>
 * <li>{@code /ui/style.css} - CSS stylesheet for menu styling</li>
 * <li>{@code /audio/Menu_1.mp3} - Background music for the menu</li>
 * </ul>
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class MainMenu extends Application {
    
    /**
     * Starts the JavaFX application and displays the main menu.
     * <p>
     * This method is called by the JavaFX runtime after the application has been
     * initialized. It sets up the menu UI components, configures their behavior,
     * and displays the stage (window).
     * </p>
     * <p>
     * The method performs the following setup:
     * <ol>
     * <li>Starts playing the menu background music</li>
     * <li>Loads and displays the Yu-Gi-Oh! logo</li>
     * <li>Creates menu buttons with event handlers</li>
     * <li>Arranges components in a vertical layout</li>
     * <li>Applies CSS styling from the external stylesheet</li>
     * <li>Sets the scene and shows the stage</li>
     * </ol>
     * </p>
     * <p>
     * <strong>Button Actions:</strong>
     * <ul>
     * <li><strong>Start Duel:</strong> Transitions to the {@link DuelBoard} to begin gameplay</li>
     * <li><strong>Quit:</strong> Closes the application window and exits</li>
     * </ul>
     * </p>
     *
     * @param stage the primary Stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        // Start menu background music
        MusicPlayer.playMusic("/audio/Menu_1.mp3");
        
        // Yu-Gi-Oh! logo (replace with your image in resources/ui/logo.png)
        ImageView logo = new ImageView(new Image(getClass().getResource("/ui/logo.png").toExternalForm()));
        logo.setFitWidth(200);
        logo.setPreserveRatio(true);

        // Menu buttons
        Button startBtn = new Button("Start Duel");
        // Button optionsBtn = new Button("Options"); // TODO: Implement options screen
        Button quitBtn = new Button("Quit");

        // Button actions
        startBtn.setOnAction(e -> {
            DuelBoard plateau = new DuelBoard();
            try {
                plateau.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        // optionsBtn.setOnAction(e -> System.out.println("Options opened...")); // TODO
        quitBtn.setOnAction(e -> stage.close());

        // Layout configuration
        VBox root = new VBox(20, logo, startBtn, quitBtn);
        root.setStyle("-fx-padding: 40; -fx-alignment: center;");

        // Create scene with specified dimensions
        Scene scene = new Scene(root, 1200, 950);

        // Load CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

        // Configure and display the stage
        stage.setScene(scene);
        stage.setTitle("Yu-Gi-Oh! Menu");
        stage.show();
    }

    /**
     * Application entry point.
     * <p>
     * This method is called when the application is launched from the command line
     * or IDE. It invokes the JavaFX {@link Application#launch(String...)} method
     * which initializes the JavaFX runtime and calls {@link #start(Stage)}.
     * </p>
     * <p>
     * <strong>Usage:</strong>
     * <pre>
     * java ui.MainMenu
     * </pre>
     * </p>
     *
     * @param args command-line arguments (not currently used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}