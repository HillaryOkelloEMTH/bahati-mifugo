package com.emtech.dairyapp.Dairy.Supply;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class Codenerator {

    private final MilkCollectionRepo repo;

    public Codenerator(MilkCollectionRepo repo) {
        this.repo = repo;
    }

    public String codeGenerator(Integer farmerNo) {

        StringBuilder sb = new StringBuilder();
        LocalDate date = LocalDate.now();
//        Long max = repo.getMaxVaue();
        Random random = new Random();
        Integer val = random.nextInt(1000);
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonthValue());
        String day = String.valueOf(date.getDayOfMonth());
        String code = sb.append(farmerNo).append(month).append(day).append("-").append(val).toString();
        return code;


    }


}
