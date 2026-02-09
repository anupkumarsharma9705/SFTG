package com.securetransfer.SFTG.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendOtp(String toEmail, String otp) {

        OkHttpClient client = new OkHttpClient();

        String jsonBody = """
        {
          "sender": {
            "email": "yourbrevoemail@gmail.com",
            "name": "SFTG"
          },
          "to": [ { "email": "%s" } ],
          "subject": "SFTG Email Verification OTP",
          "textContent": "Your OTP is: %s\\nValid for 5 minutes.\\n\\nSFTG Team"
        }
        """.formatted(toEmail, otp);

        RequestBody body = RequestBody.create(
                jsonBody, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(BREVO_URL)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("api-key", brevoApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new RuntimeException("Brevo failed: " + response.body().string());
            }

        } catch (Exception e) {
            throw new RuntimeException("OTP email failed", e);
        }

        // DEV ONLY (remove later)
        System.out.println("OTP for " + toEmail + " is " + otp);
    }
}