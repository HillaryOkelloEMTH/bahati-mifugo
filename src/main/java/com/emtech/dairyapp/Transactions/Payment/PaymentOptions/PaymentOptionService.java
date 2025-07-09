package com.emtech.dairyapp.Transactions.Payment.PaymentOptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentOptionService {

    private final PaymentOptionRepository optionRepository;
    private final PaymentCategoryRepository categoryRepository;

//    public List<PaymentOptionDTO> getAllOptions() {
//        return optionRepository.findByDeletedFalse().stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
public List<PaymentOptionDTO> getAllOptions() {
    return optionRepository.findByDeletedFalse().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
}



    public PaymentOptionDTO createOption(PaymentOptionDTO dto) {
        if (optionRepository.existsByName(dto.getName())) {
            throw new RuntimeException("A payment option with this name already exists.");
        }

        PaymentCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        PaymentOption option = new PaymentOption();
        option.setName(dto.getName());
        option.setCode(generateUniqueCode());
        option.setDescription(dto.getDescription());
        option.setActive(dto.isActive());
        option.setCategory(category);

        PaymentOption saved = optionRepository.save(option);
        return toDTO(saved);
    }

    private String generateUniqueCode() {
        return String.valueOf((int)(Math.random() * 9000) + 1000); // e.g., "8423"
    }


    public Optional<PaymentOptionDTO> getOptionById(Long id) {
        return optionRepository.findById(id).map(this::toDTO);
    }

    public PaymentOptionDTO updateOption(Long id, PaymentOptionDTO dto) {
        PaymentOption option = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment option not found"));

        PaymentCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        option.setName(dto.getName());
        option.setCode(generateUniqueCode());
        option.setDescription(dto.getDescription());
        option.setActive(dto.isActive());
        option.setCategory(category);

        return toDTO(optionRepository.save(option));
    }

    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }

    private PaymentOptionDTO toDTO(PaymentOption option) {
        PaymentOptionDTO dto = new PaymentOptionDTO();
        dto.setId(option.getId());
        dto.setName(option.getName());
        dto.setCode(option.getCode());
        dto.setDescription(option.getDescription());
        dto.setActive(option.isActive());
        dto.setCategoryId(option.getCategory().getId());
        dto.setCategoryName(option.getCategory().getName());
        return dto;
    }

    public void softDeleteOptions(Long id) {
        Optional<PaymentOption> optional = optionRepository.findById(id);
        if (optional.isPresent()) {
            PaymentOption option = optional.get();
            option.setDeleted(true);
            optionRepository.save(option);
        } else {
            throw new EntityNotFoundException("Payment Options with id " + id + " not found");
        }
    }

    public List<PaymentOptionDTO>getOptionByCategoryId(Long categoryId){
    return optionRepository.findByCategoryIdAndDeletedFalse(categoryId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());



        }
    }


