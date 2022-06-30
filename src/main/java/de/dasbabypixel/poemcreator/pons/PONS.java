package de.dasbabypixel.poemcreator.pons;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class PONS {

	public static void test() throws MalformedURLException, IOException {

		HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.pons.com/v1/dictionary?q=leben&l=deen")
				.openConnection();
		con.setRequestProperty("X-Secret", "c615473d80302bdcc468d0633bd2afdfbc1ba527e95016cbc37d1035423aed7a");
		con.connect();
		InputStream in = con.getInputStream();
		byte[] a = IOUtils.toByteArray(in);
		String s = new String(a, StandardCharsets.UTF_8);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(gson.fromJson(s, JsonElement.class)));
		con.disconnect();
		System.out.println(s);
	}
}
