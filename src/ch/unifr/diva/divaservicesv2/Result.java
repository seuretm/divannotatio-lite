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
import org.json.JSONException;

/**
 *
 * @author Mathias Seuret
 */
public abstract class Result<T> {
    protected String url;
    protected HttpResponse<JsonNode> response = null;
    
    public Result(String url) {
        this.url = url;
        System.out.println("Waiting for results on\n"+url);
    }
    
    public boolean isResultAvailable() throws UnirestException {
        try {
            response = Unirest.get(url).asJson();
        } catch (UnirestException ex) {
            System.out.println("Illegal JSON response - probably generating the output");
            return false;
        }
        
        if (response.getBody().getObject().getString("status").equals("planned")) {
            response = null;
            return false;
        }
        return true;
    }
    
    public void waitResult() throws UnirestException, InterruptedException {
        waitResult(Long.MAX_VALUE);
    }
    
    public void waitResult(long timeout) throws UnirestException, InterruptedException {
        long chrono = System.currentTimeMillis();
        if (response==null) {
            Thread.sleep(5000);
        }
        while (response==null && !isResultAvailable()) {
            if (System.currentTimeMillis()-chrono>timeout) {
                throw new InterruptedException("Result not arrived within delay");
            }
            Thread.sleep(5000);
        }
    }
    
    public T getResult() throws UnirestException, InterruptedException {
        waitResult();
        try {
            return createResult(response);
        } catch (JSONException ex) {
            System.out.println("Fail: "+ex.getMessage());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {}
            response = Unirest.get(url).asJson();
            return getResult();
        }
    }
    
    protected abstract T createResult(HttpResponse<JsonNode> response);
}
