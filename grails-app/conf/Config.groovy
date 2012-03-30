// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

import grails.plugins.springsecurity.SecurityConfigType

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
    xml: ['text/xml', 'application/xml'],
    text: 'text/plain',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    all: '*/*',
    json: ['application/json', 'text/json'],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

environments {
  development {
    grails.logging.jul.usebridge = true
    grails.relativeServerURL = "/wenergy/"

    // log4j configuration
    log4j = {
      // Example of changing the log pattern for the default console
      // appender:
      //
      appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c{4} %x - %m%n')
      }

      error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
          'org.codehaus.groovy.grails.web.pages', //  GSP
          'org.codehaus.groovy.grails.web.sitemesh', //  layouts
          'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
          'org.codehaus.groovy.grails.web.mapping', // URL mapping
          'org.codehaus.groovy.grails.commons', // core / classloading
          'org.codehaus.groovy.grails.plugins', // plugins
          'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
    }
  }
  production {
    grails.logging.jul.usebridge = false
    // set per-environment serverURL stem for creating absolute links
    grails.serverURL = "http://www.wenergy-project.de"
    grails.relativeServerURL = "/"

    // log4j configuration
    log4j = {
      // Example of changing the log pattern for the default console
      // appender:
      //
      appenders {
        file name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c{4} %x - %m%n'), file: (System.getProperty('catalina.base') ?: 'target') + '/logs/wenergy.log'
        file name: 'stacktrace', layout: pattern(conversionPattern: '%d [%t] %-5p %c{4} %x - %m%n'), file:  (System.getProperty('catalina.base') ?: 'target') + '/logs/wenergy_stack.log'
      }

      error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
          'org.codehaus.groovy.grails.web.pages', //  GSP
          'org.codehaus.groovy.grails.web.sitemesh', //  layouts
          'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
          'org.codehaus.groovy.grails.web.mapping', // URL mapping
          'org.codehaus.groovy.grails.commons', // core / classloading
          'org.codehaus.groovy.grails.plugins', // plugins
          'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
    }
  }
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'edu.kit.im.Household'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'edu.kit.im.HouseholdRole'
grails.plugins.springsecurity.authority.className = 'edu.kit.im.Role'

//grails.plugins.springsecurity.auth.loginFormUrl = '/'
//grails.plugins.springsecurity.failureHandler.defaultFailureUrl = '/home/index'

grails.plugins.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap
grails.plugins.springsecurity.interceptUrlMap = [
    // Allow unrestricted access to API
    '/api/**': ["permitAll"],
    '/*': ["permitAll"],
    '/home/welcome': ["permitAll"],
    '/registration/**': ["permitAll"],
    // Block controllers
    '/admin/**': ["hasRole('ROLE_ADMIN')"],
    '/aggregatedConsumption/**': ["hasRole('ROLE_ADMIN')"],
    '/applicane/**': ["hasRole('ROLE_ADMIN')"],
    '/clearDatabase/**': ["hasRole('ROLE_ADMIN')"],
    '/consumption/**': ["hasRole('ROLE_ADMIN')"],
    '/data/**': ["hasRole('ROLE_USER')"],
    '/home/**': ["hasRole('ROLE_USER')"],
    '/household/**': ["hasRole('ROLE_ADMIN')"],
    '/peergroup/**': ["hasRole('ROLE_ADMIN')"]
]

// Always use latest jQuery version
jquery {
    sources = "jquery"
    version = "1.7.2"
}

// Added by the Joda-Time plugin:
grails.gorm.default.mapping = {
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentDateMidnight, class: org.joda.time.DateMidnight
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentDateTime, class: org.joda.time.DateTime
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentDateTimeZoneAsString, class: org.joda.time.DateTimeZone
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentDurationAsString, class: org.joda.time.Duration
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentInstantAsMillisLong, class: org.joda.time.Instant
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentInterval, class: org.joda.time.Interval
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentLocalDate, class: org.joda.time.LocalDate
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime, class: org.joda.time.LocalDateTime
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentLocalTime, class: org.joda.time.LocalTime
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentPeriodAsString, class: org.joda.time.Period
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentTimeOfDay, class: org.joda.time.TimeOfDay
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentYearMonthDay, class: org.joda.time.YearMonthDay
	"user-type" type: org.jadira.usertype.dateandtime.joda.PersistentYears, class: org.joda.time.Years
}

// Twitter Bootstrap CSS
grails.plugins.twitterbootstrap.fixtaglib = true

// Deployment
grails.plugin.cloudfoundry.target = "api.wenergy-project.de"
grails.plugin.cloudfoundry.appname = "www"
