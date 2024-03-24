package com.example.stock.util.uuid;

import java.util.UUID;

public class UuidProvider {

  public UuidProvider() {
  }

  public static String generateUuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
