import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.lang.Object;


/**
 * Created by kevin on 3/21/2016.
 */


public class TwitterRetrieval {


    public static void main(String args[]) throws IOException, InterruptedException {

/* REMOVED API KEY */
        String encodedKey = encodeKeys(REMOVED, REMOVED);
        String encodedKey2 = encodeKeys(REMOVED, REMOVED;


        ArrayList<String> bearerToken = new ArrayList<String>();

        String bearerTokenTemp = requestBearerToken("https://api.twitter.com/oauth2/token", encodedKey);
        bearerToken.add(0, parseBearerToken(bearerTokenTemp));

        bearerTokenTemp = requestBearerToken("https://api.twitter.com/oauth2/token", encodedKey2);
        bearerToken.add(1, parseBearerToken(bearerTokenTemp));



        long sysTime = 0;
        long currentTime = 0;
        long increment = 1000;//9000000;

        ArrayList<String> queries = new ArrayList<String>();
        queries.add(0,URLEncoder.encode("Princeton", "UTF-8"));
        queries.add(1, URLEncoder.encode("Basketball", "UTF-8"));
        queries.add(2, URLEncoder.encode("Hillary%20Clinton", "UTF-8"));
        queries.add(3, URLEncoder.encode("Bernie%20Sanders", "UTF-8"));
        queries.add(4, URLEncoder.encode("Donald%20Trump", "UTF-8"));
        queries.add(5, URLEncoder.encode("Harvard", "UTF-8"));
        queries.add(6, URLEncoder.encode("Yale", "UTF-8"));
        queries.add(7, URLEncoder.encode("Health", "UTF-8"));
        queries.add(8, URLEncoder.encode("Immigration", "UTF-8"));
        queries.add(9, URLEncoder.encode("Privacy", "UTF-8"));
        queries.add(10, URLEncoder.encode("Best", "UTF-8"));

        currentTime = 0;
        int i = 0;
        while (i < 100000000)
        {
            if ((currentTime + increment) <= System.currentTimeMillis())
            {
                bearerTokenTemp = bearerToken.get(i%2);
                String query = queries.get(i%11);


                String count = "&count=100";

                //  String recent = "&since_id=" + id;
                String endPoint = "https://api.twitter.com/1.1/search/tweets.json?q=" + query + count;

                String result = getTweet(endPoint, bearerTokenTemp);
                parseTweet(result, query);
                sysTime = System.currentTimeMillis();
                currentTime = sysTime;
                i++;
            }
            Thread.sleep(500);
        }
    }

    private static int parseTweet(String input, String query)
    {

        JsonParser parser = new JsonParser();
        JsonElement output = parser.parse(input);
        int id = -1;

        if (output == null)
            return 0;

        try {
            JsonObject object = output.getAsJsonObject();


            JsonArray array = object.getAsJsonArray("statuses");


             String fileName = query + ".txt";


            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));

        for (JsonElement s :array)
        {
            JsonObject perTweet = s.getAsJsonObject();

            System.out.println(perTweet.get("text"));
            out.print(perTweet.get("text") + ",");

            System.out.println(perTweet.get("created_at"));
            out.print(perTweet.get("created_at") + ",");




            System.out.println(perTweet.get("user").getAsJsonObject().get("screen_name"));
            out.println(perTweet.get("user").getAsJsonObject().get("screen_name") + ",");

            System.out.println(perTweet.get("id"));
            out.println(perTweet.get("id"));


            String idVal = String.valueOf(perTweet.get("id"));
            if (id == -1)
                id = Integer.parseInt(idVal.substring(6, idVal.length()-6));


        }
            out.close();
        }
        catch (IllegalStateException e)
        {
            System.out.println("JSON NULLLLLLLLLLLLLLLLLLLLL");
            return 0;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return id;
    }

    private static String getTweet(String endPointUrl, String bearerToken) throws IOException {
        HttpsURLConnection connection = null;

        try {
            URL url = new URL(endPointUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Host", "api.twitter.com");
            connection.setRequestProperty("User-Agent", "Your Program Name");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
            connection.setUseCaches(false);


            // Parse the JSON response into a JSON mapped object to fetch fields from.
            String obj = readResponse(connection, System.getProperty("line.separator"));

            if (obj != null) {
                System.out.println(obj);
                return obj;
            }
            return new String();
        }
        catch (MalformedURLException e) {
            throw new IOException("Invalid endpoint URL specified.", e);
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String parseBearerToken(String inToken)
    {


        JsonParser parser = new JsonParser();
        JsonElement output = parser.parse(inToken);
        String outToken = output.getAsJsonObject().get("access_token").getAsString();
        System.out.println(outToken);
      //  int pos = inToken.indexOf("en\":");
      //  int length = inToken.length();
      //  outToken = inToken.substring((pos + 5), length - 4);
       // System.out.println(outToken);

        return outToken;
    }

    private static String requestBearerToken(String endURL, String encodedCredentials) throws IOException {
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(endURL);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Host", "api.twitter.com");
            connection.setRequestProperty("User-Agent", "Your Program Name");
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("Content-Length", "29");
            connection.setUseCaches(false);

            writeRequest(connection, "grant_type=client_credentials");

            // Parse the JSON response into a JSON mapped object to fetch fields from.
        //    JSONObject obj = (JSONObject)JSONValue.parse(readResponse(connection));
            String obj = readResponse(connection, System.getProperty("line.separator"));
            System.out.println(obj);
            if (obj != null) {
                System.out.println(obj);

                return obj;
            }
            return new String();
        }
        catch (MalformedURLException e) {
            throw new IOException("Invalid endpoint URL specified.", e);
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String encodeKeys(String consumerKey, String consumerSecret)
    {
        String secret = null;

        try
        {
            String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

            String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;

            byte[] encodedBytes = Base64.getEncoder().encode(fullKey.getBytes());
            secret = new String(encodedBytes);
            System.out.println("Secret: " + secret);
            return secret;
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println("Unsupported Encoding!");
            return null;
        }

     //   return null;
    }


    // Writes a request to a connection
    private static boolean writeRequest(HttpsURLConnection connection, String textBody) {
        try {
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            wr.write(textBody);
            wr.flush();
            wr.close();

            return true;
        }
        catch (IOException e) { return false; }
    }


    // Reads a response for a given connection and returns it as a string.
    private static String readResponse(HttpsURLConnection connection, String seperator) {
        try {
            StringBuilder str = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while((line = br.readLine()) != null) {
                str.append(line + seperator);
            }
            return str.toString();
        }
        catch (IOException e) { return new String(); }
    }
}



