FROM openjdk:11-jre-slim as builder
COPY maven/assembly/ /app
# extract
WORKDIR /app
RUN java -Djarmode=layertools -jar sk-gov-knowledgegraph-1.0-SNAPSHOT.jar extract

FROM openjdk:11-jre-slim
LABEL author="marek.surek@gmail.com"
EXPOSE 8080
# healthcheck
RUN apt update && apt install -y -o APT::Immediate-Configure=false curl && rm -rf /var/lib/apt/lists/*
HEALTHCHECK --start-period=2m --interval=30s --timeout=15s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
# copy layered
WORKDIR /app
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
# other externalized cfgs
#COPY --from=builder app/*.json ./
COPY --from=builder app/snapshot-dependencies/ ./
# app classes
COPY --from=builder app/application/ ./

ENTRYPOINT java -Dhttp.socket.timeout=30000 -Dhttp.conn-manager.max-total=600 -Dhttp.conn-manager.max-per-route=400 -Djava.security.egd=file:/dev/./urandom org.springframework.boot.loader.JarLauncher $(perl -e 'sub get_stat(){ open STAT, "</proc/stat"; while (<STAT>){ next unless /^cpu\s+[0-9]+/; my @cpu = split /\s+/, $_; shift @cpu; my $idle = $cpu[3]; my $total = 0; $total += $_ for @cpu; close STAT; return ($idle, $total); }} $| = 1; my ($prev_idle, $prev_total) = get_stat(); my $diff_idle_perc = 0; sleep 1; my $count = 60; while ( $count > 0){ $count -= 1; my ($idle, $total) = get_stat(); my $diff_idle = $idle - $prev_idle; my $diff_total = $total - $prev_total; $diff_idle_perc = 100 * $diff_idle / $diff_total; last if $diff_idle_perc > 20; $prev_idle = $idle; $prev_total = $total; sleep 10; }'; if [ -d /run/secrets ] && [ $(ls -Aq /run/secrets | wc -l) -gt 0 ]; then for fn in /run/secrets/*; do prop=${fn##*/}; echo -n " --${prop}=$(cat ${fn})"; done fi)