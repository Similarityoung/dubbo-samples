/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.rest.demo.service.HelloService;

import org.apache.dubbo.rpc.RpcContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClient;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableDubbo
@RunWith(SpringRunner.class)
public class ResourceServerTest {

    //    @LocalServerPort
    //    private int port;

    @DubboReference(url = "tri://localhost:50051")
    private HelloService helloService;

    private final String clientId = "49fd8518-12eb-422b-9264-2bae0ab89f66";
    private final String clientSecret = "H3DTtm2fR3GRAdr4ls1mcg";

    //    @Test
    //    public void testGetUserEndpoint() {
    //        String credentials = clientId + ":" + clientSecret;
    //        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
    //
    //        // build RestClient request
    //        RestClient restClient = RestClient.builder().build();
    //        String url = "http://localhost:" + 9000 + "/oauth2/token";
    //
    //        try {
    //            // make a post request
    //            String response = restClient.post()
    //                    .uri(url)
    //                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
    //                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    //                    .body("grant_type=client_credentials&scope=read")
    //                    .retrieve()
    //                    .body(String.class);
    //
    //            System.out.println("Access Token Response: " + response);
    //
    //            ObjectMapper objectMapper = new ObjectMapper();
    //            JsonNode jsonNode = objectMapper.readTree(response);
    //            String accessToken = jsonNode.get("access_token").asText();
    //
    //            System.out.println("accessToken: " + accessToken);
    //            // Use the access token to authenticate the request to the /user endpoint
    //            assert accessToken != null;
    //            String userUrl = "http://localhost:" + port + "/api/hello/World";
    //            try {
    //                String userResponse = restClient.post()
    //                        .uri(userUrl)
    //                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
    //                        .retrieve()
    //                        .body(String.class);
    //
    //                System.out.println("User Response: " + userResponse);
    //                assertEquals("Hello, World", userResponse, "The response should be 'Hello,user!'");
    //            } catch (RestClientResponseException e) {
    //                System.err.println("Error Response: " + e.getResponseBodyAsString());
    //            }
    //
    //        } catch (JsonProcessingException e) {
    //            throw new RuntimeException(e);
    //        }
    //    }

    @Test
    public void testService() {
        System.out.println("helloService: " + helloService.sayHello("World"));
    }

    @Test
    public void testHelloService() {

        System.out.println("helloService: " + helloService.sayHello("World"));

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // build RestClient request
        RestClient restClient = RestClient.builder().build();
        String url = "http://localhost:" + 9000 + "/oauth2/token";

        try {
            // make a post request
            String response = restClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body("grant_type=client_credentials&scope=read")
                    .retrieve()
                    .body(String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            String accessToken = jsonNode.get("access_token").asText();

            System.out.println("accessToken: " + accessToken);

            RpcContext.getContext().setAttachment("Authorization", "Bearer " + accessToken);

            assertEquals("Hello, World", helloService.sayHello("World"), "The response should be 'Hello, World'");
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
