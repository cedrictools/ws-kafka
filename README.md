json web service for publishing on kafka
================================================

## Usage
[![Build Status](https://secure.travis-ci.org/cedrictools/wskaf.png)](http://travis-ci.org/cedrictools/wskaf)

```bash
### run kafka with docker
docker run -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=172.17.42.1 --env ADVERTISED_PORT=9092 spotify/kafka

### build and start the webservice
lein uberjar

java -jar target/wskaf-0.1.0-standalone.jar

### list existing topics
kafka-topics.sh --list --zookeeper localhost:2181

### create topic "topicA"
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topicA

### send on topic topicA return 200
curl  -v -H 'Content-Type: application/json' -X PUT -d '{"msg":"I love clojure and open source"}' http://localhost:8080/v1/produce/topicA

### send on random topic return 404
curl  -v -H 'Content-Type: application/json' -X PUT -d '{"msg":"I love clojure and open source"}' http://localhost:8080/v1/produce/topicB

### consume messages
kafka-console-consumer.sh --zookeeper localhost:2181 --topic topicA --from-beginning
```

## Running tests

`lein with-profile +test repl`


```clojure
(use 'midje.repl)
(autotest)
```
