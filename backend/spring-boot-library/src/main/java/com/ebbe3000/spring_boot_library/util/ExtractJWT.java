package com.ebbe3000.spring_boot_library.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExtractJWT {

    private final ObjectMapper mapper;

    public Optional<String> payloadJWTExtraction(String token, String extraction) throws JsonProcessingException {
        String[] chunks = token.replace("Bearer", "").split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JsonNode rootNode = mapper.readTree(payload);

        return Optional.of(rootNode.get(extraction).asText());
    }


    @Data
    private static class User {
        String sub;
    }
}


