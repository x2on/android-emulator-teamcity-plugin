mvn install:install-file -Dfile=server-api.jar -DgroupId=com.jetbrains.teamcity -DartifactId=server-api -Dversion=6.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=agent-api.jar -DgroupId=com.jetbrains.teamcity -DartifactId=agent-api -Dversion=6.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=common-api.jar -DgroupId=com.jetbrains.teamcity -DartifactId=common-api -Dversion=6.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=../webapps/ROOT/WEB-INF/lib/annotations.jar -DgroupId=com.jetbrains.teamcity -DartifactId=annotations -Dversion=6.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=../webapps/ROOT/WEB-INF/lib/openapi.jar -DgroupId=com.intellij -DartifactId=openapi -Dversion=6.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=../buildAgent/lib/agent.jar -DgroupId=com.jetbrains.teamcity -DartifactId=agent -Dversion=6.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=../buildAgent/lib/util.jar -DgroupId=com.intellij -DartifactId=util -Dversion=6.0.2 -Dpackaging=jar