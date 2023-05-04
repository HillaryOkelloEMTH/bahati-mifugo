package com.emtech.dairyapp.bulksms;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkRequest{
	private String templateName;
	private List<RecipientsItem> recipients;
	private String templateBody;
}