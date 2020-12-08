# Running Apache Kafka in Docker Container

### intro
The goal of this guide is to stand up a Docker container with Kafka.  

The guide at https://kafka.apache.org/quickstart-docker is garbage.  A much better intro, which was used to compose this document is by Jacek Laskowski here - https://jaceklaskowski.gitbooks.io/apache-kafka/content/kafka-docker.html.

If you're still not sure how it all fits together, this was a good (55 minute) introduction to "Microservices, Docker, Kubernetes" by James Quigley [youTube](https://www.youtube.com/watch?v=1xo-0gCVhTU.)

### Set up Docker on local machine
1. Download and install Docker desktop [link](https://www.docker.com/products/docker-desktop).
2. Signup for a Docker Hub account. [link](https://hub.docker.com/)
3. Git clone the wurstmeister's kafka-docker repository to your local filesystem. [link](https://github.com/wurstmeister/kafka-docker)
4. Cd into the cloned directory.  
5. Run `ifconfig` and look for `eth0` and under that, look for `inet`.  Copy this IP address (eg. in this sample, the inet is 172.19.50.117).  Open `docker-compose.yml` in a text editor and change the `KAFKA_ADVERTISED_HOST_NAME` to your inet IP address.  Save and exit.
```
$ifconfig
eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 172.19.50.117  netmask 255.255.240.0  broadcast 172.19.63.255
        inet6 fe80::215:5dff:fe10:e5d4  prefixlen 64  scopeid 0x20<link>
        ether 00:15:5d:10:e5:d4  txqueuelen 1000  (Ethernet)
        RX packets 32860  bytes 45805976 (45.8 MB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 4800  bytes 508518 (508.5 KB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
        inet 127.0.0.1  netmask 255.0.0.0
        inet6 ::1  prefixlen 128  scopeid 0x10<host>
        loop  txqueuelen 1000  (Local Loopback)
        RX packets 29  bytes 2140 (2.1 KB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 29  bytes 2140 (2.1 KB)
```
6. Now, run `docker-compose up`.  This will pull the zookeeper, openjdk, alpine linux and kafka  and use `docker-compose.yml` to stand up a Docker container.  This default setup has just 1 broker and zookeeper.  Take a look at the output to see the exact order and from where these different files are being pulled from (Docker Hub and apache.claz.org).  Once Docker is finishing setting up it will remain in interactive mode, logging to the console.  So, lets get a new console open...
7. Open a new terminal window and let's get the container names.  (*aside: those double curly brackets are Go templates, more info [here](https://docs.docker.com/config/formatting/).*): 
```
$ docker ps --format "{{.Names}}"
kafka-docker_kafka_1
kafka-docker_zookeeper_1
```
8. Get the port number for the kafka broker.  This command is using the Go templates mentioned before, but in this case `docker inspect` normally returns a JSON object and the `format` switch is pulling out the `NetworkSettings` key and then returning `NetworkSettings.Ports` value.  To get a closer look, use `docker inspect kafka-docker_kafka_1`:   
```
$ docker inspect --format='{{range $k, $v := .NetworkSettings.Ports}}{{range $v}}{{$k}} -> {{.HostIp}} {{.HostPort}}{{end}}{{end}}' kafka-docker_kafka_1

9092/tcp -> 0.0.0.0 32770
```
9. Check connection to single kafka broker with `nc` the `-z` flag checks the port for listening daemons.
```
$ nc -vz 0.0.0.0 32770
Connection to 0.0.0.0 32770 port [tcp/*] succeeded!
```
10. Create a new topic on Broker 0 with 3 partitions and replication factor of 1.
```
$ docker exec -t kafka-docker_kafka_1 \
  kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create \
    --topic hot-topic \
    --partitions 3 \
    --replication-factor 1
```
11.  List topics and verify your topic was created.
```
$  docker exec -t kafka-docker_kafka_1 kafka-topics.sh --bootstrap-server localhost:9092 --list
```
12. Ask Kafka to show you the status of the topic.
```
$ docker exec -t kafka-docker_kafka_1 \
  kafka-topics.sh \
    --bootstrap-server :9092 \
    --describe \
    --topic hot-topic
Topic: hot-topic        PartitionCount: 3       ReplicationFactor: 1    Configs: segment.bytes=1073741824
        Topic: hot-topic        Partition: 0    Leader: 1001    Replicas: 1001  Isr: 1001
        Topic: hot-topic        Partition: 1    Leader: 1001    Replicas: 1001  Isr: 1001
        Topic: hot-topic        Partition: 2    Leader: 1001    Replicas: 1001  Isr: 1001    
```
13. Open a new terminal and startup a console **consumer** to make sure everything is connected:
```
$ docker exec -t kafka-docker_kafka_1 \
  kafka-console-consumer.sh \
    --bootstrap-server :9092 \
    --group big-data \
    --topic hot-topic
```

14. Open another terminal and startup a console **producer**.
```
$ docker exec -it kafka-docker_kafka_1 \
  kafka-console-producer.sh \
    --broker-list :9092 \
    --topic hot-topic
```

15. To shut down, use Ctrl-C on the producer, consumer, kafka then finally the terminal window running the Docker containers for kafka and zookeeper.  You also use `docker-compose down`.


