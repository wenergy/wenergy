grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
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
  }

  plugins {
    compile ":hibernate:$grailsVersion"
    compile ":jquery:1.7"
    compile ":resources:1.0.2"

    compile ":spring-security-core:1.2.6"
    compile ":joda-time:1.3"

    compile ":quartz2:0.2.2"

    build ":tomcat:$grailsVersion"
  }
}
