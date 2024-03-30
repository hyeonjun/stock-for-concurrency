package com.example.stock.config.datasource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.orm.jpa.vendor.Database;

public class DatasourceProperties {

  @Getter
  @AllArgsConstructor
  public enum DatabaseType {
    MYSQL(Database.MYSQL),
    ;

    private final Database database;
  }

  @Getter
  @AllArgsConstructor
  public enum DatabasePlatform {
    MYSQL8("org.hibernate.dialect.MySQL8Dialect"),
    ;

    private final String platform;
  }

  @Getter
  @AllArgsConstructor
  public enum DdlOption {
    NONE("none"),
    CREATE("create"),
    CREATE_DROP("create-drop"),
    UPDATE("update"),
    VALIDATE("validate"),
    ;

    private final String option;
  }

}
