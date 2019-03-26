package me.q9029.app.microsoft.translator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

public class MicrosoftTranslatorService {

	private String subscriptionKey;

	public MicrosoftTranslatorService(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	public Map<String, String> translate(String originalText, List<String> langList) {

		try {
			Gson gson = new Gson();

			String toLang = String.join(",", langList);
			URL url = new URL("https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=" + toLang);

			List<Text> textList = new ArrayList<>();
			Text text = new Text(originalText);
			textList.add(text);
			String reqBody = gson.toJson(textList);

			HttpsURLConnection conn = null;
			StringBuilder respBodyBuilder = new StringBuilder();
			try {
				conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Content-Length", "" + reqBody.length());
				conn.setRequestProperty("Connection", "close");
				conn.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
				conn.setRequestProperty("X-ClientTraceId", UUID.randomUUID().toString());
				conn.setDoOutput(true);

				try (DataOutputStream outStream = new DataOutputStream(conn.getOutputStream())) {
					byte[] byteArry = reqBody.getBytes("UTF-8");
					outStream.write(byteArry, 0, byteArry.length);
				}

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
					String line;
					while ((line = reader.readLine()) != null) {
						respBodyBuilder.append(line);
					}
				}
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			ResponseElement[] respElements = gson.fromJson(respBodyBuilder.toString(), ResponseElement[].class);

			Map<String, String> transResultMap = new HashMap<>();
			for (Translation trans : respElements[0].translations) {
				transResultMap.put(trans.to, trans.text);
			}

			return transResultMap;

		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

	public String translate(String originalText, String lang) {

		List<String> langList = new ArrayList<>();
		langList.add(lang);

		Map<String, String> result = translate(originalText, langList);
		return result.get(lang);
	}

	private class Text {

		@SuppressWarnings("unused")
		private String text;

		private Text(String text) {
			this.text = text;
		}
	}

	private class ResponseElement {

		@SuppressWarnings("unused")
		private DetectedLanguage detectedLanguage;

		private Translation[] translations;
	}

	private class DetectedLanguage {

		@SuppressWarnings("unused")
		private String language;

		@SuppressWarnings("unused")
		private double score;
	}

	private class Translation {

		private String text;

		private String to;
	}
}
