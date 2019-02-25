package com.server.search;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageRequest {
    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

    private final String CONTENT_TYPE = "application/json";

    private final String ENCODING = "UTF-8";

    // private static final String GET_URL = "http://localhost:9090/SpringMVCExample";

    public final String GET_URL;

    private final String INIT_GET_URL = "https://commons.wikimedia.org/w/api.php?action=query&titles=";

    private final String END_GET_URL = "&prop=imageinfo&&iiprop=url&iiurlwidth=200&format=json&origin=*";

    private final String POST_URL = "http://localhost:9090/SpringMVCExample/home";

    private final String POST_PARAMS = "userName=Pankaj";

    public ArrayList<String> thumbUrlList = new ArrayList<>();

    public ImageRequest() {
        this.GET_URL = "https://commons.wikimedia.org/w/api.php?action=query&titles=File:EPN._Inauguraci%C3%B3n_de_la_Planta_de_Honda_en_Celaya.jpg|File:EPN_Firma.png|File:EPN_Firma.png&prop=imageinfo&&iiprop=url&iiurlwidth=300&format=json&origin=*";
    }

    public ImageRequest(ArrayList<String> imageNames) {
        StringBuilder sb = new StringBuilder(INIT_GET_URL);
        if(imageNames.size() > 0) {
            sb.append("File:").append(imageNames.get(0));
            for (int i = 1; i < imageNames.size(); i++)
                sb.append("|File:").append(imageNames.get(i));
        }
        sb.append(END_GET_URL);
        this.GET_URL = sb.toString();
    }

    public static void main(String[] args) throws IOException {
        ImageRequest request = new ImageRequest();
        request.sendGET();
        for (String tUrl: request.thumbUrlList) {
            System.out.println(tUrl);
        }
        System.out.println("GET DONE");
        // sendPOST();
        // System.out.println("POST DONE");
    }

    private void appendContent(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println(response.toString());
    }

    private void getThumbUrlList(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Gson gson = new GsonBuilder().create();
        JsonObject json = gson.fromJson(response.toString(), JsonObject.class);
        //System.out.println(json.get("batchcomplete"));
        JsonObject jsonQuery = (JsonObject) json.get("query");
        //System.out.println(jsonQuery.get("normalized"));
        JsonObject jsonPages = (JsonObject) jsonQuery.get("pages");
        thumbUrlList = new ArrayList<>();
        //System.out.println(jsonPages.keySet());
        for (String key: jsonPages.keySet()) {
            JsonObject jsonPage = jsonPages.getAsJsonObject(key);
            //System.out.println(jsonPage);
            if(!jsonPage.has("missing")) {
                JsonArray jsonImageInfo = jsonPage.getAsJsonArray("imageinfo");
                // System.out.println(jsonImageInfo);
                for (JsonElement je : jsonImageInfo) {
                    JsonObject imageInfo = je.getAsJsonObject();
                    //System.out.println(imageInfo.get("thumburl").getAsString());
                    thumbUrlList.add(imageInfo.get("thumburl").getAsString());
                }
            } else thumbUrlList.add("images/missing-min.png");
        }
        // System.out.println(json.toString());
        // print result
        // System.out.println(response.toString());
        // System.out.println(thumbUrlList);
    }

    public void sendGET() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("content-type", CONTENT_TYPE);
        con.setRequestProperty("Accept-Charset", "UTF-8");

        // System.out.println("Content type: " + con.getContentType());
        // System.out.println("Encoding: " + con.getContentEncoding());

        int responseCode = con.getResponseCode();
        //System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) // success
            getThumbUrlList(con);
        else System.out.println("GET request not worked");

    }

    private void sendPOST() throws IOException {
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            appendContent(con);
        } else {
            System.out.println("POST request not worked");
        }
    }
}
