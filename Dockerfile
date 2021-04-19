FROM quay.io/strimzi/kafka:0.22.1-kafka-2.7.0
USER root:root
RUN mkdir -p /opt/kafka/plugins/GoogleSheetConnector
COPY ./target/google-sheet-source-connector-1.0.jar /opt/kafka/plugins/GoogleSheetConnector/
USER 1001
