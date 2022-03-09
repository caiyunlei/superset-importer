package com.apache.superset.tool;

import lombok.Getter;

@Getter
public class LoginRequestDto {
  private String username;
  private String password;
  private String provider = "db";
  private boolean refresh = true;

  public LoginRequestDto(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
