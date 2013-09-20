VERSION="8.0.3"
set -e
if [ ! -e TeamCity-${VERSION}.tar.gz ]; then
	echo "Downloading TeamCity-${VERSION}.tar.gz"
    curl -O http://download.jetbrains.com/teamcity/TeamCity-${VERSION}.tar.gz
else
	echo "Using TeamCity-${VERSION}.tar.gz"
fi

tar zxf TeamCity-${VERSION}.tar.gz
mvn install:install-file -Dfile=TeamCity/devPackage/server-api.jar -DgroupId=com.jetbrains.teamcity -DartifactId=server-api -Dversion=${VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=TeamCity/devPackage/agent-api.jar -DgroupId=com.jetbrains.teamcity -DartifactId=agent-api -Dversion=${VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=TeamCity/devPackage/common-api.jar -DgroupId=com.jetbrains.teamcity -DartifactId=common-api -Dversion=${VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=TeamCity/webapps/ROOT/WEB-INF/lib/annotations.jar -DgroupId=com.jetbrains.teamcity -DartifactId=annotations -Dversion=${VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=TeamCity/webapps/ROOT/WEB-INF/lib/openapi.jar -DgroupId=com.intellij -DartifactId=openapi -Dversion=${VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=TeamCity/buildAgent/lib/agent.jar -DgroupId=com.jetbrains.teamcity -DartifactId=agent -Dversion${VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=TeamCity/buildAgent/lib/util.jar -DgroupId=com.intellij -DartifactId=util -Dversion=${VERSION} -Dpackaging=jar


