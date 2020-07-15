/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.unifr.diva.divaservicesv2;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import java.awt.Polygon;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Mathias Seuret
 */
public class PolyListResult extends Result<List<Polygon>> {

    public PolyListResult(String url) {
        super(url);
        System.out.println("PolyListResult(" + url + ")");
    }

    @Override
    protected List<Polygon> createResult(HttpResponse<JsonNode> response) {
        List<Polygon> res = new LinkedList<>();
        JSONArray out = response.getBody().getObject().getJSONArray("output");
        HashSet<String> hack = new HashSet<>();
        for (int o = 0; o < out.length(); o++) {
            if (!out.getJSONObject(o).has("file") && !out.getJSONObject(o).getJSONObject("file").getString("name").equals("textlines")) {
                continue;
            }
            try {
                JSONObject tmpRes = readJsonFromUrl(out.getJSONObject(0).getJSONObject("file").getString("url"));
                JSONArray item = tmpRes.getJSONArray("output");
                for (int p = 0; p < item.length(); p++) {
                    JSONArray values = item.getJSONObject(p).getJSONObject("array").getJSONArray("values");
                    Polygon poly = new Polygon(new int[values.length()], new int[values.length()], 0);
                    for (int l = 0; l < values.length(); l++) {
                        JSONArray pt = values.getJSONArray(l);
                        poly.addPoint(pt.getInt(0), pt.getInt(1));
                    }
                    String chk = Arrays.toString(poly.xpoints)+Arrays.toString(poly.ypoints);
                    if (!hack.contains(chk)) {
                        res.add(poly);
                        hack.add(chk);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return res;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
