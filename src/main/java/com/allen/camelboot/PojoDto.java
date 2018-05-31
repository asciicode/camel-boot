package com.allen.camelboot;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class PojoDto {
    private @Getter @Setter String firstnames;
    private @Getter @Setter String surname;

}
