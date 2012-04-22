grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.war.file = "target/${appName}.war" //-${appVersion}

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  checksums true // Whether to verify checksums on resolve

  repositories {
    inherits true // Whether to inherit repository definitions from plugins
    grailsPlugins()
    grailsHome()
    grailsCentral()
    mavenCentral()

    // uncomment these to enable remote dependency resolution from public Maven repositories
    //mavenCentral()
    //mavenLocal()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

    runtime("mysql:mysql-connector-java:5.1.18")
    compile("org.jadira.usertype:usertype.jodatime:1.9")
    compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
      excludes "commons-logging", "xml-apis", "xalan", "groovy"
    }
  }

  plugins {
    compile ":hibernate:$grailsVersion"
    compile ":database-migration:1.0"
    compile ":jquery:1.7.1"
    compile ":resources:1.1.6"
    compile ":cloud-foundry:1.2.1"
    compile ":spring-security-core:1.2.7.3"
    compile ":joda-time:1.4"
    compile ":quartz2:0.2.2"
    compile ":rocks:1.0.1"
    compile ":fields:1.1"

    // Uncomment these (or add new ones) to enable additional resources capabilities
    runtime ":zipped-resources:1.0"
    runtime ":cached-resources:1.0"
    compile ":cache-headers:1.1.5"
    runtime ":yui-minify-resources:0.1.5"

    build ":tomcat:$grailsVersion"
  }
}
