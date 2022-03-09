package com.apache.superset.tool;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
  @JsonAlias("access_token")
  private String accessToken;
  @JsonAlias("refresh_token")
  private String refreshToken;
}
