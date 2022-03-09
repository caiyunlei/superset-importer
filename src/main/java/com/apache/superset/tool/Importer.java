package com.apache.superset.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Importer {
  private String user;
  private String password;
  private String address;

  public Importer(String user, String password, String address) {
    this.user = user;
    this.password = password;
    this.address = address;
  }

  public void importDashboard(File file) throws IOException {
    OkHttpClient client = new OkHttpClient();

    LoginRequestDto loginRequestDto = new LoginRequestDto(user, password);
    ObjectMapper objectMapper = new ObjectMapper();
    RequestBody loginReqBody = RequestBody.create(objectMapper.writeValueAsString(loginRequestDto), MediaType.parse("application/json"));
    Request loginReq = new Request.Builder()
            .url( address + "/api/v1/security/login")
            .post(loginReqBody)
            .build();

    String bearAuthValue = null;
    try (Response loginResp = client.newCall(loginReq).execute()) {
      ResponseBody responseBody = loginResp.body();
      log.debug("login response:{}", responseBody);
      LoginResponseDto loginResponseDto = objectMapper.readValue(responseBody.bytes(), LoginResponseDto.class);
      bearAuthValue = loginResponseDto.getAccessToken();
    }

    Request getCsrfTokenReq = new Request.Builder()
            .url(address +  "/api/v1/security/csrf_token/")
            .addHeader("Authorization", "Bearer " + bearAuthValue)
            .get()
            .build();

    String sessionCookie = null;
    String csrfToken = null;
    try (Response csrfResp = client.newCall(getCsrfTokenReq).execute()) {
      String cookies = csrfResp.headers().get("Set-Cookie");
      log.debug("all cookies:{}", cookies);
      sessionCookie = subSessionCookieValue(cookies);
      String csrfRespBodyString = csrfResp.body().string();
      var csrfTokenDto = objectMapper.readValue(csrfRespBodyString, CsrfTokenDto.class);
      csrfToken = csrfTokenDto.getResult();
      log.debug("csrf token:{}", csrfToken);
    }

    RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("formData", file.getName(), RequestBody.create(MediaType.parse("form-data"), file))
            .build();

    Request request = new Request.Builder()
            .url( address + "/api/v1/dashboard/import/")
            .addHeader("X-CSRFToken", csrfToken)
            .addHeader("Authorization", "Bearer " + bearAuthValue)
            .addHeader("Cookie", sessionCookie)
            .post(requestBody)
            .build();

      try (Response response = client.newCall(request).execute()) {
        log.debug("import response:{}", response);
      }
  }

  private String subSessionCookieValue(String cookies) {
    int startIndex = cookies.indexOf("session=");
    int endIndex = cookies.indexOf(";", startIndex);
    String sessionCookie = cookies.substring(startIndex, endIndex);
    log.debug("session cookie:{}", sessionCookie);
    return sessionCookie;
  }
}
