FROM java:openjdk-8-jdk
run git clone https://github.com/jareds/grails-taskd.git /opt/grailstaskd-src && \
cd /opt/grailstaskd-src && \
chmod +x grailsw && \
./grailsw prod war && \
mkdir /opt/grailstaskd && \
cp /opt/grailstaskd-src/build/libs/grails-taskd-0.1.war /opt/grailstaskd/grailstaskd.war && \
rm -rf /opt/grailstaskd-src && \
rm -rf /root/.gradle && \
rm -rf /root/.m2 && \
rm -rf /root/.grails
VOLUME /var/lib/grailstaskd
EXPOSE 443
EXPOSE 80
CMD java -jar -Dgrails.env=prod /opt/grailstaskd/grailstaskd.war
