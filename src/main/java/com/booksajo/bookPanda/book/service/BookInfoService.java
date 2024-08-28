package com.booksajo.bookPanda.book.service;

import com.booksajo.bookPanda.book.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import com.booksajo.bookPanda.cart.exception.errorCode.NaverAPIErrorCode;
import com.booksajo.bookPanda.cart.exception.exception.NaverAPIException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookInfoService {

    private final ObjectMapper objectMapper;

    private final BookSalesService bookSalesService;


    public void errorTask(NaverRequestVariableDto dto)
    {
        if(dto.getDisplay() > 101 || dto.getDisplay() < 1 )
            throw new NaverAPIException(NaverAPIErrorCode.INVALID_DISPLAY_VALUE_ERROR);
        if(dto.getStart() > 100 || dto.getStart() < 1 )
            throw new NaverAPIException(NaverAPIErrorCode.INVALID_START_VALUE_ERROR);
        if(!(dto.getSort().equals("sim") || dto.getSort().equals("date")))
            throw new NaverAPIException(NaverAPIErrorCode.INVALID_SORT_VALUE_ERROR);
    }

    public URI getURI(NaverRequestVariableDto dto)
    {
        errorTask(dto);
        return UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/book.json")
                .queryParam("query", dto.getQuery())
                .queryParam("display", dto.getDisplay())
                .queryParam("start", dto.getStart())
                .queryParam("sort", dto.getSort())
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
    }

    public RequestEntity<Void> request(NaverRequestVariableDto dto)
    {
        return RequestEntity
                .get(getURI(dto))
                .header("X-Naver-Client-Id", "5GuY5cszfH0ANUwzundv")
                .header("X-Naver-Client-Secret", "wgmhvh4EoN")
                .header("Content-Type", "application/json; charset=UTF-8")
                .build();
    }

    public List<BookInfo> searchBookWithRestTemplate(NaverRequestVariableDto dto) throws JsonProcessingException
    {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(request(dto), String.class);

        return mapping(responseEntity.getBody());
    }

    public List<BookInfo> searchBookWithWebClient(NaverRequestVariableDto dto) throws JsonProcessingException {

        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", "5GuY5cszfH0ANUwzundv")
                .defaultHeader("X-Naver-Client-Secret", "wgmhvh4EoN")
                .build();

        URI uri = getURI(dto);
        System.out.println(uri);

        String body = webClient.get()
                .uri(getURI(dto))
                .retrieve()
                .toEntity(String.class)
                .block().getBody();

        return mapping(body);
    }


    public List<BookInfo> mapping(String body) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(body);
        JsonNode itemsNode = root.path("items");
        return objectMapper.readValue(itemsNode.toString(), new TypeReference<List<BookInfo>>() {});
    }

}
