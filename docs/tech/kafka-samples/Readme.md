### Kafka Samples

- `/kafka-consumer-scala-sample`: Subscribes to a topic and outputs events to console as they happen

- `/kafka-producer-scala-sample`: Endless loop (10 sec b/w iteration) that reads a text file from URL, splits it into lines and sends each line as an event to a channel.  

- `/kafka-redirector-spark-scala-sample`: Endless loop that reads in from one topic, and outputs to another topic as they happen.

### Instructions for use

1. **Open 4 terminal windows:**

Window 1 (topic 1):

```
linux: 
./bin/kafka-console-consumer.sh --bootstrap-server url:port --topic topic1-name --from-beginning

win: 
.\bin\windows\kafka-console-consumer.bat --bootstrap-server url:port --topic topic1-name --from-beginning
```

Window 2 (topic 2):

```
linux: 
./bin/kafka-console-consumer.sh --bootstrap-server url:port --topic topic2-name --from-beginning

win: 
.\bin\windows\kafka-console-consumer.bat --bootstrap-server url:port --topic topic2-name --from-beginning
```

Window 3 (topic 3):

```
linux: 
./bin/kafka-console-consumer.sh --bootstrap-server url:port --topic topic3-name --from-beginning

win: 
.\bin\windows\kafka-console-consumer.bat --bootstrap-server url:port --topic topic3-name --from-beginning
```

window 4 (topic list):
```
linux: 
./bin/kafka-topics.sh --bootstrap-server url:port --list

win: 
.\bin\windows\kafka-topics.sh --bootstrap-server url:port --list
```

2. **Open directory in Intellij**

Don't open from the top-level 201005-reston-bigdata repo, open from the directory you want to run (eg. /kafka-consumer-scala-sample). 

If you have the memory, open all 3 examples in 3 separate intellij windows.

3. **Edit config files**

Each directory has a configuration sample file in the `/src/main/resources` directory.  

Copy or rename the file to `aws-settings.conf`. 

Paste in your urls forservers 1..3 and topic names.  

Using config/configFactory, **along** with a `.gitignore` hiding your secrets, is a good way to handle API keys.

I included .gitignore files in each directory to specifically ignore `aws-settings.conf`.  

The local directory's .gitignore will ignore the top-level 201005-reston-bigdata/ directories .gitignore, and any global .gitignore you might have (if you setup one in your .gitconfig).

4. **Run**

You should be able to hit run in IntelliJ and it should work.  Lmk if there's anything that doesn't work, or could be improved.  Feel free to make additions, corrections,
clone, reuse the code and instructions. gl hf.


