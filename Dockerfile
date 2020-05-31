FROM openjdk:8

WORKDIR /app
COPY target/universal/researchime-1.0-SNAPSHOT.zip /app
RUN unzip ./researchime-1.0-SNAPSHOT.zip
COPY conf/keystore.jks /app/researchime-1.0-SNAPSHOT/conf
RUN chmod +x researchime-1.0-SNAPSHOT/bin/researchime

EXPOSE 9000
EXPOSE 9001

CMD researchime-1.0-SNAPSHOT/bin/researchime -Dplay.crypto.secret="researchime" -Dhttps.keyStore="researchime-1.0-SNAPSHOT/conf/keystore.jks" -Dhttps.keyStorePassword="researchime" -Dplay.evolutions.db.default.autoApply=true -Dhttp.port=disabled
