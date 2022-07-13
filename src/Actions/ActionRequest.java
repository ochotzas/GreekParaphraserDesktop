package Actions;

import Enums.ApiAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ActionRequest
{
    private static int statusCode;

    public ActionRequest()
    {
        statusCode = 0;
    }

    /**
     * Actions.Paraphrase the given text.
     *
     * @param paragraphToParaphrase The text to paraphrase.
     * @return The paraphrased text.
     * @throws IOException          If the request fails.
     * @throws InterruptedException If the request is interrupted.
     */
    public static String paraphrase(String paragraphToParaphrase) throws IOException, InterruptedException
    {
        String paraphrasedText;
        paragraphToParaphrase = translateFromGreekToEnglish(paragraphToParaphrase);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://paraphraser1.p.rapidapi.com/")).header("content-type", "application/json").header("X-RapidAPI-Key", getApiKey(ApiAction.PARAPHRASE)).header("X-RapidAPI-Host", "paraphraser1.p.rapidapi.com").method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"input\": \"" + paragraphToParaphrase + "\"\n}")).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        try
        {
            paraphrasedText = response.body().substring(response.body().indexOf("\"output\":") + 10, response.body().indexOf("\"output\":") + 10 + response.body().substring(response.body().indexOf("\"output\":") + 10).indexOf("\""));
        } catch (Exception e)
        {
            paraphrasedText = "Κατά την διάρκεια της παράφρασης, κάτι πήγε στραβά. Παρακαλώ δοκιμάστε αργότερα.";
        }

        return unicodeToGreek(translateFromEnglishToGreek(paraphrasedText));
    }

    /**
     * Converts a string from Greek to English and returns it (in this case, the string is not paraphrased but corrected).
     *
     * @param paragraphToParaphrase The string to be corrected.
     * @return The corrected string.
     * @throws IOException          If the connection to the API fails.
     * @throws InterruptedException If the connection to the API fails.
     */
    public static String correct(String paragraphToParaphrase) throws IOException, InterruptedException
    {
        return unicodeToGreek(translateFromEnglishToGreek(translateFromGreekToEnglish(paragraphToParaphrase)));
    }

    /**
     * Translates a string from Greek to English.
     *
     * @param paragraph The string to translate.
     * @return The translated string.
     * @throws IOException          If the translation fails.
     * @throws InterruptedException If the translation fails.
     */
    private static String translateFromGreekToEnglish(String paragraph) throws IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://deep-translate1.p.rapidapi.com/language/translate/v2")).header("content-type", "application/json").header("X-RapidAPI-Key", getApiKey(ApiAction.TRANSLATE)).header("X-RapidAPI-Host", "deep-translate1.p.rapidapi.com").method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"q\": \" " + paragraph + "\",\n    \"source\": \"el\",\n    \"target\": \"en\"\n}")).build();
        return getHttpTranslationResponse(request);
    }

    /**
     * Translates a string from English to Greek.
     *
     * @param paragraph The paragraph to translate.
     * @return The translated paragraph.
     * @throws IOException          If the translation fails.
     * @throws InterruptedException If the translation fails.
     */
    private static String translateFromEnglishToGreek(String paragraph) throws IOException, InterruptedException
    {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://deep-translate1.p.rapidapi.com/language/translate/v2")).header("content-type", "application/json").header("X-RapidAPI-Key", getApiKey(ApiAction.TRANSLATE)).header("X-RapidAPI-Host", "deep-translate1.p.rapidapi.com").method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"q\": \" " + paragraph + "\",\n    \"source\": \"en\",\n    \"target\": \"el\"\n}")).build();
        return getHttpTranslationResponse(request);
    }

    /**
     * Request the translation from the API and return the response.
     *
     * @param request The request to be sent to the API.
     * @return The response from the API.
     * @throws IOException          If the request fails.
     * @throws InterruptedException If the request is interrupted.
     */
    private static String getHttpTranslationResponse(HttpRequest request) throws IOException, InterruptedException
    {
        String toTranslate;
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        statusCode = response.statusCode();
        try
        {
            toTranslate = response.body().substring(response.body().indexOf("\"translatedText\":") + 18, response.body().indexOf("\"translatedText\":") + 18 + response.body().substring(response.body().indexOf("\"translatedText\":") + 18).indexOf("\""));
        } catch (Exception e)
        {
            toTranslate = "Κατά την διάρκεια της παράφρασης, κάτι πήγε στραβά.";

            if (getStatusCode() == 429) toTranslate += " Έχετε υπερβεί το μέγιστο αριθμό παραφράσεων.";
            else toTranslate += " Παρακαλώ δοκιμάστε αργότερα.";
        }

        return toTranslate;
    }

    /**
     * Returns the status code of the last request.
     *
     * @return The status code of the last request.
     */
    public static int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Convert unicode character to greek character.
     *
     * @param unicode The unicode character to convert.
     * @return The greek character.
     */
    private static String unicodeToGreekChar(String unicode)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unicode.length(); i += 6)
        {
            String hex = unicode.substring(i + 2, i + 6);
            int value = Integer.parseInt(hex, 16);
            sb.append((char) value);
        }
        return sb.toString();
    }

    /**
     * Converts a string of unicode characters to a string of Greek characters.
     *
     * @param paragraph The string of unicode characters to convert.
     * @return The string of Greek characters.
     */
    private static String unicodeToGreek(String paragraph)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paragraph.length(); i++)
        {
            if (paragraph.charAt(i) == '\\')
            {
                sb.append(unicodeToGreekChar(paragraph.substring(i, i + 6)));
                i += 5;
            } else sb.append(paragraph.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Gets the API key from the file of the action to be requested.
     *
     * @param action The action to be requested.
     * @return The API key.
     */
    private static String getApiKey(ApiAction action)
    {
        String apiKey = null;

        try
        {
            File file = action == ApiAction.PARAPHRASE ? new File(".env/.env.paraphrase") : new File(".env/.env.translate");
            apiKey = new Scanner(file).nextLine();
        } catch (FileNotFoundException e)
        {
            statusCode = 400;
        }

        return apiKey;
    }
}
