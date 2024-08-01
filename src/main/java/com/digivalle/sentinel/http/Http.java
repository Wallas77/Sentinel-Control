/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digivalle.sentinel.http;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import com.digivalle.sentinel.utils.StringUtils;
import java.awt.Image;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;


/**
 *
 * @author Waldir.Valle
 */
public class Http {
   
   public String post(String urlParam, String user, String password, String body, String contentType, String tokenWithPrefix, List<NameValuePair> params, List<NameValuePair> headers) throws RuntimeException, MalformedURLException, IOException, URISyntaxException{
        String result=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
           
            URIBuilder builder = new URIBuilder(urlParam);
            if(params!=null){
                for(NameValuePair param: params){
                    builder.setParameter(param.getName(), param.getValue());
                }
            }
            System.out.println("POST - builder.build() = "+builder.build());
            System.out.println("body = "+body);
            HttpPost request = new HttpPost(builder.build());
            String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            if(user!=null && password!=null){
                request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            } else if(tokenWithPrefix!=null){
                request.setHeader(HttpHeaders.AUTHORIZATION, tokenWithPrefix);
            } 
            
            // add request headers
            //request.setHeader("Accept", "application/json");
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            if(headers!=null){
                for(NameValuePair header: headers){
                    request.setHeader(header.getName(), header.getValue());
                }
            }
            //StringEntity json = new StringEntity(StringUtils.convertToUTF8(body));
            StringEntity json = new StringEntity(body,"UTF-8");
            //StringEntity json = new StringEntity(body);
            System.out.println("json = "+json);
            request.setEntity(json);
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    //System.out.println(result);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        throw new RuntimeException(result);
                    }
                }
                
                

            } finally {
                response.close();
            }
        }
        return result;
    }
   
   public String postTOKEN(String urlParam, String body, String contentType, String token) throws RuntimeException, MalformedURLException, IOException{
        String result=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            System.out.println("POST - urlParam = "+urlParam);
            System.out.println("body = "+body);
            
            HttpPost request = new HttpPost(urlParam);
            /*String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            if(user!=null && password!=null){
                request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            } else if(tokenWithPrefix!=null){
                request.setHeader(HttpHeaders.AUTHORIZATION, tokenWithPrefix);
            } */
            
            request.setHeader("token", token);
            // add request headers
            request.setHeader("Accept", "application/json");
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            //StringEntity json = new StringEntity(StringUtils.convertToUTF8(body));
            StringEntity json = new StringEntity(body,"UTF-8");
            //StringEntity json = new StringEntity(body);
            System.out.println("json = "+json);
            request.setEntity(json);
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    //System.out.println(result);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        throw new RuntimeException(result);
                    }
                }
                
                

            } finally {
                response.close();
            }
        }
        return result;
    }
   
   
   public String get(String urlParam, String user, String password, String contentType, String token, List<NameValuePair> params, String accept, List<NameValuePair> headers, Integer timeout) throws RuntimeException, MalformedURLException, IOException, URISyntaxException{
        String result=null;
        /*final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);*/
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            
            URIBuilder builder = new URIBuilder(urlParam);
            if(params!=null){
                for(NameValuePair param: params){
                    builder.setParameter(param.getName(), param.getValue());
                }
            }
            System.out.println("GET - builder.build() = "+builder.build());
            HttpGet request = new HttpGet(builder.build());
            String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            // add request headers
            //request.setHeader("Accept", "application/json");
            request.setHeader("Accept", accept);
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            if(token!=null){
                request.addHeader("token", token);
            }
            if(headers!=null){
                for(NameValuePair header: headers){
                    request.setHeader(header.getName(), header.getValue());
                }
            }
            
            if(timeout!=null){
                RequestConfig.Builder requestConfig = RequestConfig.custom();
                requestConfig.setConnectTimeout(timeout * 1000);
                requestConfig.setConnectionRequestTimeout(timeout * 1000);
                requestConfig.setSocketTimeout(timeout * 1000);

                request.setConfig(requestConfig.build());
            }
            
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println("response=>"+response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    
                    result = EntityUtils.toString(entity,Charset.forName("UTF-8"));
                    System.out.println("result=>"+result);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        if(!result.trim().isEmpty()){
                            throw new RuntimeException(result);
                        } else {
                            throw new RuntimeException(response.getStatusLine().toString());
                        }
                    } 
                }
                
                

            } finally {
                response.close();
            }
        }
        return result;
    }
   
   public Image getImage(String urlParam, String user, String password, String contentType, String token, List<NameValuePair> params, String accept) throws RuntimeException, MalformedURLException, IOException, URISyntaxException{
        Image  image=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            System.out.println("GET - urlParam = "+urlParam);
            URIBuilder builder = new URIBuilder(urlParam);
            if(params!=null){
                for(NameValuePair param: params){
                    builder.setParameter(param.getName(), param.getValue());
                }
            }
            HttpGet request = new HttpGet(builder.build());
            String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            // add request headers
            //request.setHeader("Accept", "application/json");
            request.setHeader("Accept", accept);
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            if(token!=null){
                request.addHeader("token", token);
            }
            
            
            
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println("response=>"+response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    System.out.println("entity.isRepeatable=>"+entity.isRepeatable());
                     image = ImageIO.read(entity.getContent());
                    System.out.println("image=>"+image);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        if(!EntityUtils.toString(entity).trim().isEmpty()){
                            throw new RuntimeException(EntityUtils.toString(entity));
                        } else {
                            throw new RuntimeException(response.getStatusLine().toString());
                        }
                    } 
                }
                
                

            } finally {
                response.close();
            }
        }
        return image;
    }
   
   public String getParam(String urlParam, String user, String password, String contentType, List<NameValuePair> params) throws RuntimeException, MalformedURLException, IOException{
        String result=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            System.out.println("GET - urlParam = "+urlParam);
            HttpGet request = new HttpGet(urlParam);
            String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            // add request headers
            request.setHeader("Accept", "application/json");
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            
            if(params!=null && !params.isEmpty()){
                try {                
                    URI uri = new URIBuilder(request.getURI()).addParameters(params).build();
                    ((HttpRequestBase) request).setURI(uri);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
           
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    //System.out.println(result);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        if(!result.trim().isEmpty()){
                            throw new RuntimeException(result);
                        } else {
                            throw new RuntimeException(response.getStatusLine().toString());
                        }
                    } 
                }
                
                

            } finally {
                response.close();
            }
        }
        return result;
    }
   
    public String put(String urlParam, String user, String password, String body, String contentType) throws RuntimeException, MalformedURLException, IOException{
        String result=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            System.out.println("PUT - urlParam = "+urlParam);
            System.out.println("body = "+body);
            HttpPut request = new HttpPut(urlParam);
            String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            // add request headers
            request.setHeader("Accept", "application/json");
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            StringEntity json = new StringEntity(StringUtils.convertToUTF8(body));
            //StringEntity json = new StringEntity(body);
            System.out.println("json = "+json);
            request.setEntity(json);
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    //System.out.println(result);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        throw new RuntimeException(result);
                    }
                }
                
                

            } finally {
                response.close();
            }
        }
        return result;
    }
   
   public String patch(String urlParam, String user, String password, String body, String contentType) throws RuntimeException, MalformedURLException, IOException{
        String result=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            System.out.println("PATCH - urlParam = "+urlParam);
            System.out.println("body = "+body);
            HttpPatch request = new HttpPatch(urlParam);
            String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            // add request headers
            //request.setHeader("Accept", "application/json");
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            StringEntity json = new StringEntity(StringUtils.convertToUTF8(body));
            //StringEntity json = new StringEntity(body);
            System.out.println("json = "+json);
            request.setEntity(json);
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    //System.out.println(result);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        if(!result.trim().isEmpty()){
                            throw new RuntimeException(result);
                        } else {
                            throw new RuntimeException(response.getStatusLine().toString());
                        }
                    }
                }
                
                

            } finally {
                response.close();
            }
        }
        return result;
    }
   
   public String deleteHttp(String urlParam, String user, String password, String contentType) throws RuntimeException, MalformedURLException, IOException{
        String result=null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            System.out.println("GET - urlParam = "+urlParam);
            HttpDelete request = new HttpDelete(urlParam);
            String userPassword = user + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(userPassword.getBytes("utf-8"));
            String authHeader = "Basic " + encoding;
            request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            // add request headers
            request.setHeader("Accept", "application/json");
            request.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
            CloseableHttpResponse response = httpClient.execute(request);

            try {

                // Get HttpResponse Status
                /*System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK*/
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    //System.out.println(result);
                    if(response.getStatusLine().getStatusCode()!=200 && response.getStatusLine().getStatusCode()!=201){
                        if(!result.trim().isEmpty()){
                            throw new RuntimeException(result);
                        } else {
                            throw new RuntimeException(response.getStatusLine().toString());
                        }
                    } 
                }
                
                

            } finally {
                response.close();
            }
        }
        return result;
    }
}
