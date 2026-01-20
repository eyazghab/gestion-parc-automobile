package com.example.demo.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final String SECRET_KEY = "6LdMg8krAAAAAOYuyRGeKzF_pXzbYMg257F2c2rF"; // ⚠ Mets ta clé secrète ici

    public boolean verify(String tokenRecuDuFrontend) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", SECRET_KEY);
        params.add("response", tokenRecuDuFrontend);

        ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, params, Map.class);

        return (Boolean) response.getBody().get("success");
    }
}
