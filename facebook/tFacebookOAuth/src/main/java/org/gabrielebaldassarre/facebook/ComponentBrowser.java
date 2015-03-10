package org.gabrielebaldassarre.facebook;
/*
import static javafx.concurrent.Worker.State.FAILED;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import javafx.concurrent.Worker;

import javax.swing.*;

public class ComponentBrowser extends JFrame {
	 
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JPanel panel = new JPanel(new BorderLayout());
 
    public ComponentBrowser() {
        super();
        initComponents();
    }

    
    private void initComponents() {
        createScene();
 
        panel.add(jfxPanel, BorderLayout.CENTER);
        
        getContentPane().add(panel);
        
        setPreferredSize(new Dimension(320, 200));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();

    }
 
    @SuppressWarnings("restriction")
	private void createScene() {
 
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
 
                WebView view = new WebView();
                engine = view.getEngine();
 
                // ComponentBrowser.this.setTitle
 
                engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(final ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                        SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								if(observableValue.getValue().intValue() == 100) System.out.println(engine.getLocation());
						

							}
                        });
                    }
                });
                
                engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                       System.out.println(engine.getDocument().getTextContent());
                    }
                });
                

                engine.getLoadWorker()
                        .exceptionProperty()
                        .addListener(new ChangeListener<Throwable>() {
 
                            public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                                if (engine.getLoadWorker().getState() == FAILED) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override public void run() {
                                            JOptionPane.showMessageDialog(
                                                    panel,
                                                    (value != null) ?
                                                    engine.getLocation() + "\n" + value.getMessage() :
                                                    engine.getLocation() + "\nUnexpected error.",
                                                    "Loading error...",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    });
                                }
                            }
                        });

                jfxPanel.setScene(new Scene(view));
            }
        });
    }
 
    @SuppressWarnings("restriction")
	public void loadURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
                String tmp = toURL(url);
 
                if (tmp == null) {
                    tmp = toURL("http://" + url);
                }
 
                engine.load(tmp);
            }
        });
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
                return null;
        }
    }

   

    public static void main(String[] args) {

    	SwingUtilities.invokeLater(new Runnable() {

            public void run() {
            	ComponentBrowser browser = new ComponentBrowser();
                browser.setVisible(true);
                browser.loadURL("https://www.facebook.com/connect/login_success.html");
           }    
            
            
       });
    }
}

*/
import java.awt.Window;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.w3c.dom.Document;


public class ComponentBrowser {
	
	String code;

    public ComponentBrowser() throws Exception {
        System.out.println("Showing window...");

        // Run initAndShowUI() on AWT event thread. Block until that is complete:
        SwingUtilities.invokeAndWait(new Runnable(){
        	
        	public void run(){
        		initAndShowUI();
        	}
        	
        });

        System.out.println("Now running application");
        for (int i=1; i <=10; i++) {
            System.out.println("Counting: "+i);
            Thread.sleep(500);
        }
    }

    public void initAndShowUI() {
        final JDialog dialog = new JDialog((Window)null);
        dialog.setModal(true);
        final JFXPanel jfxPanel = new JFXPanel();
        Platform.runLater(new Runnable() {
        	
        	public void run(){
        		initJFX(jfxPanel, dialog);
        	}
        	
        });
        dialog.add(jfxPanel);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(null);

        // Since the dialog is modal, this will block execution (of the AWT event thread)
        // until the dialog is closed:
        dialog.setVisible(true);
    }

    private void initJFX(JFXPanel jfxPanel, final Window dialog) {

        // Create a web view:
        WebView webView = new WebView();
        final WebEngine engine = webView.getEngine();
        
        engine.load("https://www.facebook.com/connect/login_success.html");

        // Check for a new document being loaded. If the document just contains the 
        // text "Success", then close the dialog (unblocking all threads waiting for it...)

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>(){

        	public void changed(final ObservableValue<? extends Worker.State> observableValue, final State oldState, final State newState)
            {
                if (newState == Worker.State.SUCCEEDED){
                    Document doc = engine.getDocument();
                    code = "scemo chi legge";
                    if ("Success".equals(doc.getDocumentElement().getTextContent())) {
                        // Close dialog: this must be done on the AWT event thread
                        SwingUtilities.invokeLater(new Runnable(){
                        	
                        	public void run(){
                        		dialog.dispose();
                        	}
                        	
                        });
                    }
                }
                
            }
        	
        });
        
 
        /*
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                Document doc = engine.getDocument();
                if ("Success".equals(doc.getDocumentElement().getTextContent())) {
                    // Close dialog: this must be done on the AWT event thread
                    SwingUtilities.invokeLater(new Runnable(){
                    	
                    	public void run(){
                    		dialog.dispose();
                    	}
                    	
                    });
                }
            }
        });
     */

        // Just for testing
        // simulate login with simple button:

        //Button button = new Button("Login");
        //button.setOnAction(event -> engine.loadContent("Success", "text/plain"));
        //HBox controls = new HBox(button);
        //controls.setAlignment(Pos.CENTER);
        //controls.setPadding(new Insets(10));

        jfxPanel.setScene(new Scene(new BorderPane(webView, null, null, null, null)));
    }

    public static void main(String[] args) throws Exception {
        ComponentBrowser loginView = new ComponentBrowser();
        System.out.println(loginView.code);

    }

}