# Running Kafka locally
Note: commands are being run from the Kafka installation directory.
If you have the KAFKA_HOME environment variable you can prepend the commands with $KAFKA_HOME
and run the commands from any directory.  This is useful if you want to read from a data directory.

1. start zookeeper
        default options:
        - dataDir=/tmp/zookeeper
        - clientPort=2181
        - maxClientCnxns=0
       
```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

2. start kafka server.  
        default options: 
        - broker.id=0
        - num.network.threads=3
        - num.io.threads=3
        - num.parititions=1
        - log.dirs=/tmp/kafka-logs
        - log.retention.hours=168
        - zookeeper.connect=localhost:2181

```
bin/kafka-server-start.sh config/server.properties
```

3. create a topic named "testTopic"
```
bin/kafka-topics.sh --create --topic testTopic --bootstrap-server :9092
```

4. describe the topic to see the PartitionCount, ReplicationFactor, partition/leader/replica/isr broker ids.
```
$ bin/kafka-topics.sh --bootstrap-server :9092 --topic testTopic --describe
Topic: testTopic        PartitionCount: 1       ReplicationFactor: 1    Configs: segment.bytes=1073741824
        Topic: testTopic        Partition: 0    Leader: 0       Replicas: 0     Isr: 0
```

5. startup a console-producer on the topic:
```
bin/kafka-console-producer.sh --bootstrap-server :9092 --topic testTopic
```

6. startup a console-consumer on the topic:
```
bin/kafka-console-consumer.sh --bootstrap-server :9092 --topic testTopic
```

7. Test connection using the console-producer.  It is working, try to `cat` a file into the producer.  The file LICENSE should be available in the base kafka directory.  You can also `cat` any other file.  :
```
cat LICENSE | bin/kafka-console-producer.sh --bootstrap-server :9092 --topic testTopic
```

# Multiple broker example

To run multiple brokers, you must create 2 new server.properties files in `config/` and
run them in separate terminal windows:

1a. Copy config/server.properties to config/server.properties-1 and change the following settings
- broker.id=1
- listeners=PLAINTEXT://:9093 (also, uncomment this line)
- log.dirs=/tmp/kafka-logs-1

1b. To start broker 1, run:
```
bin/kafka-server-start.sh config/server.properties-1 
```

2a. Copy config/server.properties to config/server.properties-2 and change the following settings
- broker.id=2
- listeners=PLAINTEXT://:9094 (also, uncomment this line)
- log.dirs=/tmp/kafka-logs-2

2b. To start broker 2, run:
```
bin/kafka-server-start.sh config/server.properties-2
```

3. Create a replicated topic:
```
bin/kaftopics.sh --create --topic testTopicReplicated --replication-factor 3 --partitions 1 --bootstrap-server :9092
```

4. Describe a replicated topic:
```
$ bin/kafka-topics.sh --bootstrap-server :9092 --topic testTopicReplicated --describe
Topic: testTopicReplicated      PartitionCount: 1       ReplicationFactor: 3    Configs: segment.bytes=1073741824
        Topic: testTopicReplicated      Partition: 0    Leader: 2       Replicas: 2,1,0 Isr: 2,1,0
```


