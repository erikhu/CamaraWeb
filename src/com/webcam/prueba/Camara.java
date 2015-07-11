package com.webcam.prueba;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import com.github.sarxos.webcam.Webcam;

public class Camara extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	BufferedImage bfi = null;
	ImageView vista = null;
	ObjectProperty<Image> imagen = null;
	Webcam camara = null;

	@Override
	public void start(Stage estado) throws Exception {
		estado.setTitle("webcam");
		estado.setWidth(640);
		estado.setHeight(480);
		camara = Webcam.getDefault();
		if (camara != null) {
			camara.setViewSize(new Dimension(640, 480));
			
			System.out.println("Posibles dimensiones: ");
			for(Dimension d : camara.getViewSizes()){
				System.out.printf("dimension: en x : %d en y: %d ", d.width,d.height);
				System.out.println();
			}
		
			camara.open();
			imagen = new SimpleObjectProperty<Image>();

			vista = new ImageView();

			FlowPane root = new FlowPane();

			root.getChildren().add(vista);
			estado.setScene(new Scene(root));
			estado.show();

			boolean p = true;

			Task<Void> tarea = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					while (p) {
						try {
							bfi = camara.getImage();
							if (bfi != null) {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										imagen.set(SwingFXUtils.toFXImage(bfi,
												null));

									}

								});
								bfi.flush();
							}

						} catch (Exception e) {
							System.out.println("la excepcion : " + e);
						}
					}
					return null;
				}

			};

			Thread hilo = new Thread(tarea);
			hilo.setDaemon(true);
			hilo.start();

			vista.imageProperty().bind(imagen);

		}

	}

}
