####Jenkins Deployment Plugin with support for JBoss 6.x, 7.x and WildFly 8.x####

To use JBoss 7.x and WildFly 8.x support you have to manually upgrade the log4j library in your *jenkins.war*.
This is required because jboss-logging depends on a newer version of log4j.

Open your *jenkins.war* with 7z and replace the *log4j-1.2.9.jar* in *\WEB-INF\lib\* with the latest version *log4j-1.2.17.jar*.
