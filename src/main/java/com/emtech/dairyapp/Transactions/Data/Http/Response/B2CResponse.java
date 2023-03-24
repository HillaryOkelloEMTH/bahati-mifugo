package com.emtech.dairyapp.Transactions.Data.Http.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class B2CResponse {
    private String ConversationID;
    private String ResponseCode;
    private String OriginatorConversationID;
    private String ResponseDescription;
}
