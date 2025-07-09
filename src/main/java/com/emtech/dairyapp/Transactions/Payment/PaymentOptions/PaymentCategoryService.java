package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCategoryService {

    private final PaymentCategoryRepository categoryRepository;

    public List<PaymentCategoryDTO> getAllCategories() {
        return categoryRepository.findByDeletedFalse().stream()
                .map(category -> {
                    PaymentCategoryDTO dto = new PaymentCategoryDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    return dto;
                }).collect(Collectors.toList());
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


