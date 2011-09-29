dataSource {
  pooled = true
  driverClassName = "org.hsqldb.jdbcDriver"
  username = "sa"
  password = ""
}
hibernate {
  cache.use_second_level_cache = true
  cache.use_query_cache = true
  cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
  development {
    dataSource {
      dbCreate = "create-drop" // one of 'create', 'create-drop','update'
      url = "jdbc:hsqldb:mem:devDB"
    }
  }
  test {
    dataSource {
      dbCreate = "update"
      url = "jdbc:hsqldb:mem:testDb"
    }
  }
//    production {
  //        dataSource {
  //            dbCreate = "update"
  //            url = "jdbc:hsqldb:file:prodDb;shutdown=true"
  //        }
  //    }
  production {
    dataSource {
      pooled = true
      driverClassName = "com.mysql.jdbc.Driver"
      dbCreate = "update" // one of 'create', 'create-drop','update'
      username = "wattsoever"
      password = "Vs4QYp2uaF"
      url = "jdbc:mysql://localhost/wattsoever_prod?useUnicode=true&characterEncoding=UTF-8"
      dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
      properties {
        validationQuery = "SELECT 1"
        testOnBorrow = true
        testOnReturn = true
        testWhileIdle = true
        timeBetweenEvictionRunsMillis = 1000 * 60 * 30
        numTestsPerEvictionRun = 3
        minEvictableIdleTimeMillis = 1000 * 60 * 30
      }
    }
  }

}
