FROM openjdk:8

WORKDIR /app
COPY target/universal/languagelogger-1.0-SNAPSHOT.zip /app
RUN unzip ./languagelogger-1.0-SNAPSHOT.zip
COPY conf/keystore.jks /app/languagelogger-1.0-SNAPSHOT/conf
RUN chmod +x languagelogger-1.0-SNAPSHOT/bin/languagelogger

EXPOSE 9000
EXPOSE 9001

CMD languagelogger-1.0-SNAPSHOT/bin/languagelogger -Dplay.crypto.secret="researchime" -Dhttps.keyStore="languagelogger-1.0-SNAPSHOT/conf/keystore.jks" -Dhttps.keyStorePassword="researchime" -Dplay.evolutions.db.default.autoApply=true -Dhttp.port=disabled
