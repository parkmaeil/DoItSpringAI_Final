package com.spring.ai.chapter08_1.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class WeatherTool {

    private final RestClient restClient;
    @Value("${app.weather.api-key}")
    private String weatherApiKey;

    public WeatherTool(RestClient restClient) {
        this.restClient = restClient;
    }

    @Tool(description = "Get weather information of given city.")
    public String getWeather(@ToolParam(description = "city name, preferably in English (e.g., 'Seoul')") String city) {
        //System.out.println("Executing tool: getWeather for " + city);
        // API 호출 및 결과 반환
        var response = restClient.get()
                .uri(builder -> builder.path("/current.json")
                        .queryParam("key", weatherApiKey)
                        .queryParam("q", city).build())
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        return response.toString();
    }

}
