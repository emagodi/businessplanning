package zw.co.zetdc.businessplanning.payload.request;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text){

}
