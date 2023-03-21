package com.emtech.dairyapp.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LinkedStringInteger {
    private LinkedList<String> names;
    private List<Date> dates;
    private LinkedList<Integer> count;
    private LinkedList<Double> amount;
    private LinkedList<Double> quantiy;
}
