package com.ece.ing4.bomberman.test;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;

public class LocalizationTest {
	
	@Test
	public void testLocalization() throws Exception{

	String language;
    String country;
    language = new String("en");
    country = new String("US");

    Locale currentLocale;
    ResourceBundle messages;

    currentLocale = new Locale(language, country);

    messages = ResourceBundle.getBundle("dictionary", currentLocale);
    System.out.println(messages.getString("welcome_text"));
	}    
}
