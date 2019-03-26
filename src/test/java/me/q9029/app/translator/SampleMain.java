package me.q9029.app.translator;

public class SampleMain {

	public static void main(String[] args) {

		String subscriptionKey = "******************************";

		MicrosoftTranslatorService service = new MicrosoftTranslatorService(subscriptionKey);

		String translatedText = service.translate("こんにちは。", "en");

		// write your program
	}
}
