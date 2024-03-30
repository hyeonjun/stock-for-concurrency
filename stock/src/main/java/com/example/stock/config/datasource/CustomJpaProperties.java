package com.example.stock.config.datasource;

import com.example.stock.config.datasource.DatasourceProperties.DatabasePlatform;
import com.example.stock.config.datasource.DatasourceProperties.DdlOption;
import java.util.Properties;
import lombok.Getter;

public class CustomJpaProperties {

  @Getter
  private final Properties jpaProperties = new Properties();

  private static final String DDL_AUTO = "hibernate.hbm2ddl.auto";
  private static final String DATABASE_PLATFORM = "hibernate.dialect";
  private static final String SHOW_SQL = "hibernate.show_sql";
  private static final String FORMAT_SQL = "hibernate.format_sql";
  private static final String ID_NEW_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";

  public void setDdlAuto(DdlOption ddlAuto) {
    jpaProperties.setProperty(DDL_AUTO, ddlAuto.getOption());
  }

  public void setDatabasePlatform(DatabasePlatform databasePlatform) {
    jpaProperties.setProperty(DATABASE_PLATFORM, databasePlatform.getPlatform());
  }

  public void setShowSql(boolean isShowSql) {
    jpaProperties.setProperty(SHOW_SQL, String.valueOf(isShowSql));
  }

  public void setFormatSql(boolean isFormatSql) {
    jpaProperties.setProperty(FORMAT_SQL, String.valueOf(isFormatSql));
  }

  public void setIdNewGeneratorMappings(boolean isIdNewGeneratorMappings) {
    jpaProperties.setProperty(ID_NEW_GENERATOR_MAPPINGS, String.valueOf(isIdNewGeneratorMappings));
  }
}
