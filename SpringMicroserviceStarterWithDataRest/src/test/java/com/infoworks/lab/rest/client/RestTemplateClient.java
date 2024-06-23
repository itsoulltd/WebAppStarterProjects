package com.infoworks.lab.rest.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RestTemplateClient {

    @Test
    public void DataRestApis() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();

        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        //http://localhost:8080/api/data
        ResponseEntity<String> rs = template.exchange("/"
                , HttpMethod.GET
                , entity
                , String.class);
        String result = rs.getBody();
        System.out.println(result);
    }

    @Test
    public void DataRestApisForUsersType1() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();

        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        //http://localhost:8080/api/data/users
        ResponseEntity<String> rs = template.exchange("/users"
                , HttpMethod.GET
                , entity
                , String.class);
        String result = rs.getBody();
        System.out.println(result);
    }

    @Test
    public void DataRestApisForUsersType2() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();

        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        String rootURL = "http://localhost:8080/api/data/users";
        ResponseEntity<String> rs = template.exchange(rootURL
                , HttpMethod.GET
                , entity
                , String.class);
        String result = rs.getBody();
        System.out.println(result);
    }

    @Test
    public void DataRestApisForUsersType3() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data/users")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();

        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        //http://localhost:8080/api/data/users
        //FIX: Will throw exception if pass empty string in url:
        ResponseEntity<String> rs = template.exchange("/"
                , HttpMethod.GET
                , entity
                , String.class);
        String result = rs.getBody();
        System.out.println(result);
    }

    @Test
    public void DataRestForUsersByPaging() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();

        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        //http://localhost:8080/api/data/users?page=0&size=5
        String path = "/users?page={page}&size={size}";
        int page = 0, size = 5;
        ResponseEntity<String> rs = template.exchange(path
                , HttpMethod.GET
                , entity
                , String.class, page, size);
        String result = rs.getBody();
        System.out.println(result);
    }

    //@Test
    public void createUser() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        HttpHeaders headers = new HttpHeaders();

        //POST
        Map<String, Object> postBody = new HashMap();
        postBody.put("name", "Test");
        postBody.put("sex", "MALE");
        postBody.put("age", "25");
        postBody.put("active", "true");
        HttpEntity<Map> create = new HttpEntity<>(postBody, headers);
        ResponseEntity<String> createResponse = template.exchange("/users"
                , HttpMethod.POST
                , create
                , String.class);
        String result = createResponse.getBody();
        System.out.println(result);
    }

    //@Test
    public void updateUser() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        HttpHeaders headers = new HttpHeaders();

        //PUT
        Map<String, Object> putBody = new HashMap();
        putBody.put("name", "Test Update");
        putBody.put("sex", "MALE");
        putBody.put("age", "29");
        HttpEntity<Map> update = new HttpEntity<>(putBody, headers);
        ResponseEntity<String> updateResponse = template.exchange("/users/355"
                , HttpMethod.PUT
                , update
                , String.class);
        String updateResult = updateResponse.getBody();
        System.out.println(updateResult);
    }

    @Test
    public void getUser() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        HttpHeaders headers = new HttpHeaders();

        //GET
        Map<String, Object> body = new HashMap();
        HttpEntity<Map> get = new HttpEntity<>(body, headers);
        ResponseEntity<String> getResponse = template.exchange("/users/1"
                , HttpMethod.GET
                , get
                , String.class);
        String getResult = getResponse.getBody();
        System.out.println(getResult);
    }

    //@Test
    public void deleteUser() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        HttpHeaders headers = new HttpHeaders();

        //DELETE
        Map<String, Object> body = new HashMap();
        HttpEntity<Map> delete = new HttpEntity<>(body, headers);
        ResponseEntity<String> deleteResponse = template.exchange("/users/355"
                , HttpMethod.DELETE
                , delete
                , String.class);
        String deleteResult = "Response Code: " + deleteResponse.getStatusCode();
        System.out.println(deleteResult);
    }

    @Test
    public void CRUDUser() throws IOException {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        HttpHeaders headers = new HttpHeaders();

        //POST
        Map<String, Object> postBody = new HashMap();
        postBody.put("name", "Test");
        postBody.put("sex", "MALE");
        postBody.put("age", "25");
        postBody.put("active", "true");
        HttpEntity<Map> create = new HttpEntity<>(postBody, headers);
        ResponseEntity<String> createResponse = template.exchange("/users"
                , HttpMethod.POST
                , create
                , String.class);
        String result = createResponse.getBody();
        System.out.println("POST: \n" + result);

        //Parse:
        Map<String, Object> dataMap = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, result);
        System.out.println("");
        Map link = (Map) dataMap.get("_links");
        Map users = (Map) link.get("self");
        String href = users.get("href").toString();

        //PUT
        Map<String, Object> putBody = new HashMap();
        putBody.put("name", "Test Update");
        putBody.put("sex", "MALE");
        putBody.put("age", "29");
        HttpEntity<Map> update = new HttpEntity<>(putBody, headers);
        ResponseEntity<String> updateResponse = new RestTemplate().exchange(href
                , HttpMethod.PUT
                , update
                , String.class);
        String updateResult = updateResponse.getBody();
        System.out.println("PUT: \n" + updateResult);

        //DELETE
        Map<String, Object> body = new HashMap();
        HttpEntity<Map> delete = new HttpEntity<>(body, headers);
        ResponseEntity<String> deleteResponse = new RestTemplate().exchange(href
                , HttpMethod.DELETE
                , delete
                , String.class);
        String deleteResult = "Response Code: " + deleteResponse.getStatusCode();
        System.out.println("DELETE: \n" + deleteResult);
        System.out.println("Delete Successful: " + (deleteResponse.getStatusCodeValue() == 204));
    }

    @Test
    public void SearchApisInUser() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();

        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        //http://localhost:8080/api/data/users/search
        ResponseEntity<String> rs = template.exchange("/users/search"
                , HttpMethod.GET
                , entity
                , String.class);
        String result = rs.getBody();
        System.out.println(result);
    }

    @Test
    public void SearchUserByAgaLimit() {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();

        HttpHeaders headers = new HttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        //http://localhost:8080/api/data/users/search/findByAgeLimit?min=18&max=20
        String path = "/users/search/findByAgeLimit?min={min}&max={max}";
        int min = 18, max = 20;
        ResponseEntity<String> rs = template.exchange(path
                , HttpMethod.GET
                , entity
                , String.class, min, max);
        String result = rs.getBody();
        System.out.println(result);
    }

}
