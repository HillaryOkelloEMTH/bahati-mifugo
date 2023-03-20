package com.emtech.dairyapp.Dairy.Supply;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class Codenerator {

 private final MilkCollectionRepo repo;

    public Codenerator(MilkCollectionRepo repo) {
        this.repo = repo;
    }

    public  String codeGenerator(Long id){

        StringBuilder sb = new StringBuilder();
        LocalDate date = LocalDate.now();
        Long max = repo.getMaxVaue();

        System.out.println(date);
        System.out.println(date.getYear());
        System.out.println(date.getMonthValue());
        System.out.println(date.getDayOfMonth());
//        String year = String.valueOf(date.getYear()).substring(2,4);
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonthValue());
        String day = String.valueOf(date.getDayOfMonth());
      String code=  sb.append(month).append(day).append("-"+id).append(max+1).toString();
//        sb.append(year.trim()).append("-"+max).toString();
        System.out.println(sb);
//        Calendar calendar = Calendar.getInstance();

        return  code;




    }


}
