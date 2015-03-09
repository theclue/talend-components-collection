package org.gabrielebaldassarre.facebook.app;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder;
/*
public class FacebookTest {

	public static void main(String[] args) {
		
		ConfigurationBuilder config = new ConfigurationBuilder();
		
		config.setOAuthAppId("487581031383192");
		config.setOAuthAppSecret("3642fd52f362bc61d6ea10b1964ddc5c");
		
		config.setOAuthPermissions("user_status");
		
		Facebook facebook = new FacebookFactory(config.build()).getInstance();
		
		String authorizationUrl = facebook.getOAuthAuthorizationURL("https://www.facebook.com/connect/login_success.html");
		
		if (Desktop.isDesktopSupported()) {
            // Windows
            try {
				Desktop.getDesktop().browse(new URI(authorizationUrl));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			} 
        } else {
            // Ubuntu
            Runtime runtime = Runtime.getRuntime();
            try {
				runtime.exec("/usr/bin/firefox -new-window " + authorizationUrl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		
		try {
			System.out.println(facebook.getName());
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FacebookException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
	}
	
}
*/

 