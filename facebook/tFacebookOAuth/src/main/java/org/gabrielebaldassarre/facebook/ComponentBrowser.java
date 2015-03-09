package org.gabrielebaldassarre.facebook;

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

