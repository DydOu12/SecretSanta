Pour lancer une analyse SONAR :

- Démarrer Docker Desktop et lancer le conteneur Docker Sonar :
    ``docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 8080:9000 sonarqube:latest``
- Lancer une analyse jacoco pour la partie coverage (uniquement si des TU ont été impactés) :
    ``./gradlew clean app:testDebugUnitTest app:jacocoTestReport``
- Lancer l'analyse avec sonarqube :
    ``./gradlew sonar \
  -Dsonar.projectKey=SecretFamilySanta \
  -Dsonar.projectName='Noel Secret en famille' \
  -Dsonar.sourceEncoding=UTF-8 \
  -Dsonar.host.url=http://localhost:8080 \
  -Dsonar.token=<TOKEN> \
  -Dsonar.tests=src/test/java \
  -Dsonar.java.coveragePlugin=jacoco \
  -Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/jacoco.xml \
  -Dsonar.test.exclusions=src/test/java/**/*``