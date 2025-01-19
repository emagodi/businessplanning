package zw.co.zetdc.businessplanning.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JsonException extends RuntimeException{
    private final String msg;
}
