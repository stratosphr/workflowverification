mvn install:install-file -Dfile=libs/sicstus/jasper.jar -DgroupId=com.sicstus -DartifactId=jasper -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true &&
mvn install:install-file -Dfile=libs/z3/z3.jar -DgroupId=com.microsoft -DartifactId=z3 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true &&
mvn clean install
