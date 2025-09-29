package com.picksave.product_service.Service;

import com.picksave.product_service.Responses.OpenFoodFactsProductResponse;
import com.picksave.product_service.Responses.OpenFoodFactsSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenFoodFactsClient {

    private final WebClient webClient;

    public OpenFoodFactsClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://world.openfoodfacts.org").build();
    }

    public Mono<OpenFoodFactsProductResponse> fetchProductByBarcode(String barcode) {
        return webClient.get()
                .uri("/api/v0/product/{barcode}.json", barcode)
                .retrieve()
                .bodyToMono(OpenFoodFactsProductResponse.class);
    }

    public Mono<OpenFoodFactsSearchResponse> fetchProductsByShop(String shop, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cgi/search.pl")
                        .queryParam("search_simple", "1")
                        .queryParam("action", "process")
                        .queryParam("tagtype_0", "stores")
                        .queryParam("tag_contains_0", "contains")
                        .queryParam("tag_0", shop)
                        .queryParam("page", page)
                        .queryParam("page_size", 20)
                        .queryParam("json", "1")
                        .build())
                .retrieve()
                .bodyToMono(OpenFoodFactsSearchResponse.class);
    }
}
