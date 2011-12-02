dataSource {
  pooled = true
  driverClassName = "org.h2.Driver"
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
      dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
      url = "jdbc:h2:prodDb"
    }
  }
  test {
    dataSource {
      dbCreate = "update"
      url = "jdbc:h2:mem:testDb"
    }
  }
  production {
    dataSource {
      dbCreate = "update"
      url = "jdbc:mysql://localhost/wenergy_test?useUnicode=true&characterEncoding=UTF-8"
      driverClassName = "com.mysql.jdbc.Driver"
      dialect = "org.hibernate.dialect.MySQL5InnoDBDialect"
      username = "wenergy"
      password = "Vs4QYp2uaF"
      pooled = true
      properties {
        maxActive = -1
        minEvictableIdleTimeMillis = 1800000
        timeBetweenEvictionRunsMillis = 1800000
        numTestsPerEvictionRun = 3
        testOnBorrow = true
        testWhileIdle = true
        testOnReturn = true
        validationQuery = "SELECT 1"
      }
    }
  }
}
