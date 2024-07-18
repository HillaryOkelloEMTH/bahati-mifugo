package com.emtech.dairyapp.Stock.Product.bulk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/bulk-products")
public class BulkProductController {
    @Autowired
    private BulkProductService bulkProductService;

    @PostMapping("add")
    public Mono<ResponseEntity<?>> uploadBulkDeliveries(ServerWebExchange exchange) {
        return exchange.getMultipartData()
                .flatMap(multipart -> {
                    FilePart filePart = (FilePart) multipart.getFirst("file");
                    if (filePart == null) {
                        Map<String, Object> errBody = new HashMap<>();
                        errBody.put("message", "File is empty");
                        errBody.put("status", "400");
                        return Mono.just(ResponseEntity.badRequest().body(errBody));
                    }
                    return bulkProductService.uploadBulkProducts(filePart)
                            .map(response -> ResponseEntity.status(response.getStatusCode()).body(response));
                });
    }
}
