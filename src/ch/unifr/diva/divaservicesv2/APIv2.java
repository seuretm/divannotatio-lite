/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.unifr.diva.divaservicesv2;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathias Seuret
 */
public class APIv2 {
    
    public static void main(String[] test) throws IOException, UnirestException, InterruptedException {
        //String cn = getImageName(ImageIO.read(new File("bar.png")));
        
        
        String cn = "firsthandsarcasticdragonfly/X.png";
        System.out.println(cn);
        PolyListResult res = extractPolygons(cn, 3, 0.0007, 0.12, 3, new Rectangle(1422, 764, 2430, 4230));
        res.waitResult();
        System.out.println("Result obtained");
        System.out.println(res.getResult().size()+" polygons received");
        
        //PolyListResult res = new PolyListResult("http://divaservices.unifr.ch/api/v2/results/worldlybitterlobster/data_0/data_0.json");
        //List<Polygon> poly = res.getResult();
    }
    
    public static String getImageName(BufferedImage bi) throws UnirestException {
        return getImageName(bi, "png");
    }
    
    public static String getImageName(BufferedImage bi, String format) throws UnirestException {
        List<Map<String, String>> fileValues = new ArrayList<>();
        Map<String, String> v = new HashMap<>();
        v.put("name", "X.png");
        v.put("extension", "png");
        v.put("type", "image");
        v.put("value", imgToBase64String(bi, format));
        fileValues.add(v);
        
        JSONObject reqBody = new JSONObject();
        JSONArray files = new JSONArray();
            for (Map<String, String> values : fileValues) {
                JSONObject object = new JSONObject();
                object.put("type", values.get("type"));
                object.put("value", values.get("value"));
                object.put("name", values.get("name"));
                object.put("extension", values.get("extension"));
                files.put(object);
            }
        reqBody.put("files", files);
        HttpResponse<String> response = Unirest.post("http://divaservices.unifr.ch/api/v2/collections")
                    .header("content-type", "application/json")
                    .body(reqBody.toString())
                    .asString();
        JSONObject jsonResponse = new JSONObject(response.getBody());
        return jsonResponse.getString("collection")+"/X.png";
    }
    
    public static PolyListResult extractPolygons(String imageName, int slices, double smoothing, double sigma, int slope, Rectangle rect) throws UnirestException {
        System.out.println("Extracting polygons for \""+imageName+"\"");
        HttpResponse<JsonNode> response = Unirest.post(
                "http://divaservices.unifr.ch/api/v2/segmentation/wavelengthseamcarving/1"
        )
                    .header("content-type", "application/json")
                    .body("{\"parameters\":{"
                            +"\"slices\": "+slices+","
                            +"\"smoothing\": "+smoothing+","
                            +"\"sigma\": "+sigma+","
                            +"\"slope\": "+slope+","
                            +"\"highlighter\":{\"type\":\"rectangle\",\"closed\":true,\"segments\":"+prepareRectangle(rect)+"}"
                            +"},\"data\":[{\"inputImage\": \""+imageName+"\"}]}")
                    .asJson();
        JSONArray results = response.getBody().getObject().getJSONArray("results");
        return new PolyListResult(results.getJSONObject(0).getString("resultLink"));
    }
    
    protected static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, formatName, os);
        return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
    
    protected static String prepareRectangle(Rectangle rectangle) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("[").append(String.valueOf(rectangle.x)).append(",").append(String.valueOf(rectangle.y)).append("]");
        sb.append(",[").append(String.valueOf(rectangle.x+rectangle.width)).append(",").append(String.valueOf(rectangle.y)).append("]");
        sb.append(",[").append(String.valueOf(rectangle.x+rectangle.width)).append(",").append(String.valueOf(rectangle.y+rectangle.height)).append("]");
        sb.append(",[").append(String.valueOf(rectangle.x)).append(",").append(String.valueOf(rectangle.y+rectangle.height)).append("]");
        sb.append("]");
        return sb.toString();
    }
}
