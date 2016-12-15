package org.neverNows;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!2" );
        Application.launch(args);
    }

	@Override
	public void start(Stage stage) throws Exception {
		
		Button bt = new Button("boton2");
        bt.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent arg0)
            {
                System.out.println("HelloWorld!2 :)");
            }
        });
        
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 150);
        root.getChildren().add(bt);

        stage.setTitle("HelloWorld in JavaFX 2.0");
        stage.setScene(scene);
        stage.show();
		
	}
}
