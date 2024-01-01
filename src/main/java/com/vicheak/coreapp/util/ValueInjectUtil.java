package com.vicheak.coreapp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ValueInjectUtil {

    @Value("${file.base-uri}")
    private String baseUri;

    //check if the image is already set and then pass the baseUri + name
    public String getImageUri(String name) {
        return Objects.nonNull(name) ? baseUri + name : null;
    }

}
