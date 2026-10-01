package com.emtech.dairyapp.bulksms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipientsItem{
	private String idNumber;
	private String memberNumber;
	private String phoneNumber;
	private String name;
}