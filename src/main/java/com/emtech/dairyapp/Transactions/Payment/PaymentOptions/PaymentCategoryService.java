package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import com.emtech.dairyapp.Response.EntityResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCategoryService {

    private final PaymentCategoryRepository categoryRepository;

    public EntityResponse<List<PaymentCategoryDTO>> getAllCategories() {
        EntityResponse<List<PaymentCategoryDTO>> res = new EntityResponse<>();

        try {
            List<PaymentCategoryDTO> categoryDTOS = categoryRepository.findByDeletedFalse().stream()
                    .map(category -> {
                        PaymentCategoryDTO dto = new PaymentCategoryDTO();
                        dto.setId(category.getId());
                        dto.setName(category.getName());
                        return dto;
                    }).toList();

            res.setMessage("Successful");
            res.setEntity(categoryDTOS);
            res.setStatusCode(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred {}", e.toString());
            res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            res.setMessage("A server error occurred");
        }
        return res;
    }

    public PaymentCategoryDTO createCategory(PaymentCategoryDTO dto) {
        PaymentCategory category = new PaymentCategory();
        category.setName(dto.getName());

        PaymentCategory saved = categoryRepository.save(category);
        dto.setId(saved.getId());
        return dto;
    }

    public Optional<PaymentCategoryDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id).map(category -> {
            PaymentCategoryDTO dto = new PaymentCategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            return dto;
        });
    }

    public PaymentCategoryDTO updateCategory(Long id, PaymentCategoryDTO dto) {
        PaymentCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(dto.getName());
        PaymentCategory updated = categoryRepository.save(category);
        dto.setId(updated.getId());
        return dto;
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public void softDeleteCategory(Long id) {
        Optional<PaymentCategory> optional = categoryRepository.findById(id);
        if (optional.isPresent()) {
            PaymentCategory category = optional.get();
            category.setDeleted(true);
            categoryRepository.save(category);
        } else {
            throw new EntityNotFoundException("PaymentCategory with id " + id + " not found");
        }
    }

}


