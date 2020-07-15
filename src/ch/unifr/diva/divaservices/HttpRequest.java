/*
 * Copyright (c) 2016 - UniFr.
 * DIVA group, University of Fribourg, Switzerland.
 */

package ch.unifr.diva.divaservices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Marcel Würsch
 *         marcel.wuersch@unifr.ch
 *         http://diuf.unifr.ch/main/diva/home/people/marcel-w%C3%BCrsch
 *         Created on: 08.10.2015.
 */
class HttpRequest {

    private static final Logger logger = Logger.getLogger(HttpRequest.class);

    /**
     * Executes a post request and returns the body json object
     *
     * @param url     URL for the HTTP Request
     * @param payload the JSON payload to send
     * @return the extracted JSON response
     */
    public static JSONObject executePost(String url, JSONObject payload) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        try {
            StringEntity se = new StringEntity(payload.toString());
            se.setContentType("application/json");
            post.setEntity(se);
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return parseEntity(entity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * executes a GET request
     *
     * @param url The url of for the request
     * @return the extracted JSON Object of the response
     */
    public static JSONObject executeGet(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // A Simple JSON Response Read
                return parseEntity(entity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    /**
     * Converts an input stream to a json string
     *
     * @param is the input stream to be converted
     * @return a JSON string
     */
    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static JSONObject parseEntity(HttpEntity entity) throws IOException {
        InputStream instream = entity.getContent();
        String str = convertStreamToString(instream);
        System.out.println("Received: "+str);
        JSONObject result = new JSONObject(str);
        instream.close();
        return result;
    }
}
