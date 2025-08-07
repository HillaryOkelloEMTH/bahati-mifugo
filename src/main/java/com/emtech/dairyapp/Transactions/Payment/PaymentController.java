package com.emtech.dairyapp.Transactions.Payment;

import com.emtech.dairyapp.Stock.Category.Category;
import com.emtech.dairyapp.Transactions.Data.Http.Request.CashPaymentRequest;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentEntityResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentResponse;
import com.emtech.dairyapp.Transactions.Data.Http.Response.PaymentsResponse;
import com.emtech.dairyapp.Transactions.Payment.PaymentOptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(
        path = "/api/v1/payments"
)
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentCategoryService paymentCategoryService;

    @Autowired
    private PaymentOptionService paymentOptionService;
    @Autowired
    private PaymentOptionRepository paymentOptionRepository;
    @Autowired
    private PaymentCategoryRepository paymentCategoryRepository;

    @RequestMapping(
            path = "/cash-payment",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentEntityResponse>> processCashPayment(@RequestBody CashPaymentRequest body) {
        return Mono.just(ResponseEntity.ok().body(this.paymentService.processCashPayment(body.getAmount(), body.getCollectorId(), body.getCollectionId())));
    }

    @RequestMapping(
            path = "/all-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllPayments() {
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPayments()));
    }

    @RequestMapping(
            path = "/pending-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllPendingPayments() {
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPaymentsByStatus("Pending")));
    }

    @RequestMapping(
            path = "/successful-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllSuccessfulPayments() {
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPaymentsByStatus("Success")));
    }

    @RequestMapping(
            path = "/failed-payments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentsResponse>> findAllFailedPayments() {
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findAllPaymentsByStatus("Failed")));
    }

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<PaymentResponse>> findPaymentDetails(@PathVariable Long id) {
        return Mono.just(ResponseEntity.ok().body(this.paymentService.findPaymentDetails(id)));
    }


    //payment options endpoints
    @GetMapping(path = "/mode", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PaymentCategoryDTO>>> getAllCategories() {
        List<PaymentCategoryDTO> categories = paymentCategoryService.getAllCategories();
        return Mono.just(ResponseEntity.ok(categories));
    }

    @PostMapping(path = "/mode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentCategoryDTO>> createCategory(@RequestBody PaymentCategoryDTO dto) {
        PaymentCategoryDTO created = paymentCategoryService.createCategory(dto);
        return Mono.just(ResponseEntity.ok(created));
    }

    @PutMapping(path = "/mode/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentCategoryDTO>> updateCategory(@PathVariable Long id, @RequestBody PaymentCategoryDTO dto) {
        PaymentCategoryDTO updated = paymentCategoryService.updateCategory(id, dto);
        return Mono.just(ResponseEntity.ok(updated));
    }

    @DeleteMapping(path = "/mode/{id}")
    public Mono<ResponseEntity<Void>> deleteCategory(@PathVariable Long id) {
        paymentCategoryService.deleteCategory(id);
        return Mono.just(ResponseEntity.noContent().build());
    }

    @DeleteMapping(path = "/mode/soft-delete/{id}")
    public ResponseEntity<String> softDeleteCategory(@PathVariable Long id) {
        paymentCategoryService.softDeleteCategory(id);
        return ResponseEntity.ok("Payment Category with ID " + id + " was successfully soft-deleted.");
    }
    // Payment Option APIs

    @GetMapping(path = "/options", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PaymentOptionDTO>>> getAllOptions() {
        List<PaymentOptionDTO> options = paymentOptionService.getAllOptions();
        return Mono.just(ResponseEntity.ok(options));
    }

    @PostMapping(path = "/options", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentOptionDTO>> createOption(@RequestBody PaymentOptionDTO dto) {
        PaymentOptionDTO created = paymentOptionService.createOption(dto);
        return Mono.just(ResponseEntity.ok(created));
    }

    @PutMapping(path = "/options/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentOptionDTO>> updateOption(@PathVariable Long id, @RequestBody PaymentOptionDTO dto) {
        PaymentOptionDTO updated = paymentOptionService.updateOption(id, dto);
        return Mono.just(ResponseEntity.ok(updated));
    }

    @DeleteMapping(path = "/options/{id}")
    public Mono<ResponseEntity<Void>> deleteOption(@PathVariable Long id) {
        paymentOptionService.deleteOption(id);
        return Mono.just(ResponseEntity.noContent().build());
    }

    @DeleteMapping(path = "/options/soft-delete/{id}")
    public ResponseEntity<String> softDeleteOptions(@PathVariable Long id) {
        paymentOptionService.softDeleteOptions(id);
        return ResponseEntity.ok("Payment Option with ID " + id + " was successfully soft-deleted.");
    }

    @GetMapping("/options/by-category/{categoryId}")
    public ResponseEntity<List<PaymentOptionDTO>> getOptionsByCategory(@PathVariable Long categoryId) {
        List<PaymentOptionDTO> options = paymentOptionService.getOptionByCategoryId(categoryId);
        return ResponseEntity.ok(options);

    }

    @PutMapping("/payment-mode/{id}/toggle-status")
    ResponseEntity<?>togglePaymentModeStatus(@PathVariable Long id) {
        Optional<PaymentCategory> PaymentModeOpt = paymentCategoryRepository.findById(id);
        if (PaymentModeOpt.isPresent()) {
            PaymentCategory mode = PaymentModeOpt.get();
            mode.setActive(!mode.isActive());
            paymentCategoryRepository.save(mode);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pyment mode not found");
        }
    }

@PutMapping("/payment-options/{id}/toggle-status")
        ResponseEntity<?>toggleStatusOption(@PathVariable Long id){
                Optional<PaymentOption>paymentOptionOpt=paymentOptionRepository.findById(id);
                if (paymentOptionOpt.isPresent()) {
                    PaymentOption option = paymentOptionOpt.get();
                    option.setActive(!option.isActive());
                    paymentOptionRepository.save(option);
                    return ResponseEntity.ok().build();
                }else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment Option not found");
                }
        }


}