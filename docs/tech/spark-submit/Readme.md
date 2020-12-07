# Submit Spark Job to the K8 Cluster

## Intro and setup

Our k8 (kubernetes) cluster is running  on AWS EKS ([Elastic Kubernetes Service](https://aws.amazon.com/eks/)).  To send a job to the cluster, we use the `bin/spark-submit` command.  However,  we can need to install a couple of programs and setup keys first.

#### 1. AWS's CLI (Command Line Interface) -  `aws` - ([link](https://aws.amazon.com/cli/)). 
Allows  management of multiple AWS services from one tool.  The syntax is:
```
$ aws service-name command
````

To list all service-names, I use `aws list` (not valid command, but the error message lists the services).

#### 2. Kubernetes CLI tool, kube control - `kubectl` -  ([link](https://kubernetes.io/docs/tasks/tools/install-kubectl/)).  
This tool allows management of Kubernetes clusters, including deploying, deleting, inspecting logs/resources, and running the **proxy**. 

The **proxy** is the only way to access the cluster.  We start up the server with:
```
$ kubectl proxy
```
*What it does: The proxy server runs on localhost, connects to our cluster.  When we use  `curl`, `wget` and the browser on our localhost our requests redirected through the proxy to the Kubernetes API ([docs](https://kubernetes.io/docs/concepts/overview/kubernetes-api/), [examples](https://kubernetes.io/docs/tasks/extend-kubernetes/http-proxy-access-api/)).*

*kubectl tip*:  The optional kubectl configuration for shell auto-completion is useful.  Also, adding the line `alias k=kubectl` to your .bashrc will save keystrokes.

## Identity and Credentials

#### How it works
To access AWS services, including submitting Spark jobs and accessing K8, we have to identify ourselves.  Both *aws-cli* and *kubectl* need to know who we are.  

We do this through the text strings, AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY ([aws cli environment variables doc](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-envvars.html)).  

**KEY_ID is akin to a username (links to an [IAM user/role](https://docs.aws.amazon.com/iam)).  The access_key is a password. Protect both, and do not save in VCS.**

It appears an IAM user/role is set up on AWS and we can have more than on by using a ***profile***.  Our credentials are located in `~/.aws` and contain our AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY along with our default ***region*** ([list](https://docs.aws.amazon.com/general/latest/gr/rande.html)) and ***output*** ([options](https://docs.aws.amazon.com/cli/latest/userguide/cli-usage-output.html)) settings.  

#### Configure and update

In our case, Adam has setup a profile for us on AWS, and with provided key_id and access_key at hand, we can run the command:
```
$ aws configure --profile p3
```
To verify configuration succeeded, and that our id and key are valid, let's try and update **kubectl cli's configuration** by running:
```
aws eks --region us-east-1 --profile p3 update-kubeconfig --name adam-king-848
```
This command tells **aws-cli** to  update config files in the directory `~/.kube ` with security certificates ([docs](https://docs.aws.amazon.com/cli/latest/reference/eks/update-kubeconfig.html)).

#### Success
If this succeeds, then it will be possible to get a list of everything running in our cluster with:
```
$ k get all    // if you added alias to .bashrc
or 
$ kubectl get all
```

There's a seemingly superflous plethora of commands, some of which are available on the [kubernetes cheatsheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/).

#### On Failure - Troubleshooting

Issue: Configuration doesn't update.  
Possible cause:  A conflict with a previous AWS identity on your system.
Check your identity with :
```
aws sts get-caller-identity
```
See config files in ~/.aws dir:
```
cat ~/.aws/credentials
cat ~/.aws/config
```
Move ~/.aws to ~/.aws-old or add an environment variable using in your .bashrc:
```
export AWS_PROFILE=p3
```


## Running a Spark Job on K8

William provided example Scala app and shell script that runs `spark-submit` for us and we're use this sample: ([GitHub repository](https://github.com/RevatureGentry/spark-scala-k8s)).

**The shell script**

Let's start with the shell script.  
1. It collects:
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS S3 bucket URI (Uniform Resource Identifier)
- JAR path and filename.  
2. Sets master to the LOCALHOST:8001.  Remember this is where  `kubectl proxy` is hosting a [proxy server](https://en.wikipedia.org/wiki/Proxy_server) and redirecting traffic to the K8 API (*aside: [why is kubernetes abbreviated k8?](https://medium.com/@rothgar/why-kubernetes-is-abbreviated-k8s-905289405a3c)*).
```
$SPARK_HOME/bin/spark-submit \
    --master k8s://http://localhost:8001 \
```

The script goes on to set other `spark-submit` configurations ([ Cloudera list](https://docs.cloudera.com/runtime/7.2.2/running-spark-applications/topics/spark-submit-options.html)):
```
    --name s3-jobName \                     # cloudera descriptions:
    --deploy-mode cluster \                 #   "Deployment mode: cluster and client. In cluster mode, the driver runs on worker hosts. In client mode, the driver runs locally as an external client. Use cluster mode with production jobs; client mode is more appropriate for interactive and debugging uses, where you want to see your application output immediately"
    --class thePackageName.Runner \         #   "For Java and Scala applications, the fully qualified classname of the class containing the main method of the application. For example, org.apache.spark.examples.SparkPi."
    --packages com.amazonaws:aws-java-sdk:1.7.4,org.apache.hadoop:hadoop-aws:2.7.6 \    # "Comma-separated list of Maven coordinates of JARs to include on the driver and executor classpaths. "
```

**What class name to use?**

I have found that for the `--class` switch, using the package name and scala file name seems to work.  (ie. src/main/scala/myPackage/Scala.scala = myPackage.Scala).  

When I change folder/file names, I have sometimes found it necessary to use IntelliJ's "*Add Configuration*" (*top menu: Run menu / Edit Configuration*) and *add(+)* to create an *Application*.  The only settings I change are:
- *Main class*
- *Use classpath of module* 

**Assembly vs Package**

Assembly can produce some large (100mb+) uber-jars.  I have experimented with adding dependencies to the `--package` line using Maven directories/addresses and in that case, I am able to use sbt's `package` command, rather than `assembly`.

**Deduplication errors in Assembly**

If you opt for assembly, and run into **deduplication** errors, this bit of code in your `build.sbt` may help:
```
assemblyMergeStrategy in assembly := {  
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard  
  case x => MergeStrategy.first  
}
```
3. Moving on, these lines add configuration for hadoop/S3/k8 (https://spark.apache.org/docs/3.0.0-preview/running-on-kubernetes.html)):
```                                                                                
    --conf spark.kubernetes.file.upload.path="$S3_BUCKET/spark/" \              # "Path to store files at the spark submit side in cluster mode."
    --conf spark.hadoop.fs.s3a.access.key="$AWS_ACCESS_KEY_ID" \                # I may have mentioned S3A being a connector-translator between k8 and S3, but (next line)
    --conf spark.hadoop.fs.s3a.secret.key="$AWS_SECRET_ACCESS_KEY" \            # its actually an Apache Hadoop client that interfaces with S3 - docs at apache http://hadoop.apache.org/docs/current/hadoop-aws/tools/hadoop-aws/index.html#Introducing_the_Hadoop_S3A_client.
    --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \    # set The implementation class of the S3A Filesystem - ref: https://hadoop.apache.org/docs/current/hadoop-aws/tools/hadoop-aws/index.html#general_configuration
    --conf spark.hadoop.fs.s3a.fast.upload=true \                               # use S3A's S3AFastOutputStream - ref: https://docs.cloudera.com/HDPDocuments/HDP2/HDP-2.6.1/bk_cloud-data-access/content/s3a-fast-upload.html, https://hadoop.apache.org/docs/current/hadoop-aws/tools/hadoop-aws/index.html#upload
    --conf spark.driver.extraJavaOptions="-Divy.cache.dir=/tmp -Divy.home=/tmp" \   # JVM options to pass to the driver - ref: https://spark.apache.org/docs/latest/configuration.html#runtime-environment, JVM command-line options - https://docs.oracle.com/javase/7/docs/technotes/tools/windows/java.html
    --conf spark.driver.log.persistToDfs.enabled=true \                         #  If true, spark application running in client mode will write driver logs to a persistent storage, configured in spark.driver.log.dfsDir. (next line)
    --conf spark.driver.log.dfsDir="$S3_BUCKET/spark-driver-logs/" \            # If spark.driver.log.dfsDir is not configured, driver logs will not be persisted.  - https://spark.apache.org/docs/latest/configuration.html#runtime-environment
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \      # On systems with RBAC (role based access control) driver pod needs k8 service account to create executor nodes - ref: https://spark.apache.org/docs/latest/running-on-kubernetes.html#rbac
    --conf spark.kubernetes.driver.request.cores=1 \                            # driver cores (request, not limit) in CPU units, ex: .5 = 0.5 of 1 cpu = 500m (millicpu or millicores) ref: https://spark.apache.org/docs/latest/running-on-kubernetes.html#spark-properties
    --conf spark.kubernetes.executor.request.cores=1 \                          # executor core (request, not limit) as above.
    --conf spark.hadoop.fs.s3a.path.style.access=true \                         # Enable S3 path style access ie disabling the default virtual hosting behaviour. Useful for S3A-compliant storage providers as it removes the need to set up DNS for virtual hosting. ref: https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingBucket.html#path-style-url-ex
    --conf spark.hadoop.fs.s3.impl=org.apache.hadoop.fs.s3native.NativeS3FileSystem \   # implementation class of s3 file system
    --conf spark.hadoop.fs.s3.awsAccessKeyId="$AWS_ACCESS_KEY_ID" \             
    --conf spark.hadoop.fs.s3.awsSecretAccessKey="$AWS_SECRET_ACCESS_KEY" \
    --conf spark.hadoop.fs.s3.buffer.dir="/tmp" \                               # Determines where on the local filesystem the s3:/s3n: filesystem should store files before sending them to S3
    --conf spark.executor.request.memory=1g \                                   # def. 1g: 	Amount of memory to use per executor process, in the same format as JVM memory strings with a size unit suffix ("k", "m", "g" or "t") 
    --conf spark.driver.request.memory=1g \                                     # same as above for driver - ref: https://spark.apache.org/docs/latest/configuration.html
    --conf spark.kubernetes.container.image=855430746673.dkr.ecr.us-east-1.amazonaws.com/adam-king-848-example-spark \
    --conf spark.executor.instances=2 \                                         # number of executors for static allocation ref - https://spark.apache.org/docs/latest/running-on-yarn.html
 ```

***Aside: S3 (object store) vs Hadoop (filesystem) [What's the difference?](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/filesystem/introduction.html#Object_Stores_vs._Filesystems)***

4. Finally, we pass in our jar path, and script arguments (key_id, access_key and bucket name on S3)
```
    $APPLICATION_JAR_LOCATION "$AWS_ACCESS_KEY_ID" "$AWS_SECRET_ACCESS_KEY" "$S3_BUCKET"
```

#### The Scala driver / Runner.scala

Here we are reiterating many settings we set in our script for s3a, s3, and also adding our dependencies with `addJar`.  My theory 
is that when we set these configurations previously, they are being set in the **Spark driver**.  I'm guessing these in the Runner are providing them for the **Spark executors**.  But, I don't think that's correct.
```
package myPackage

import org.apache.spark.sql.SparkSession

object Runner {
  def main(args: Array[String]): Unit = {

    if (args.length <= 2) {
      System.err.println("EXPECTED 2 ARGUMENTS: AWS Access Key, then AWS Secret Key, then S3 Bucket")
      System.exit(1)
    }
    val accessKey = args(0)
    val secretKey = args(1)
    val filePath = args(2)

    val spark = SparkSession.builder().appName("sample").getOrCreate()

    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/2.7.4/hadoop-aws-2.7.4.jar")
    spark.sparkContext.addJar("https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk/1.7.4/aws-java-sdk-1.7.4.jar")
    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/apache/spark/spark-sql_2.12/3.0.0/spark-sql_2.12-3.0.0.jar")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", accessKey)
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", secretKey)
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.path.style.access", "true")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.fast.upload", "true")
    spark.sparkContext.hadoopConfiguration.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    spark.sparkContext.hadoopConfiguration.set("fs.s3.awsAccessKeyId", accessKey)
    spark.sparkContext.hadoopConfiguration.set("fs.s3.awsSecretAccessKey", secretKey)

    println("hello world")
    // your code here
  }
}
```

## Monitoring Spark Jobs in K8

After using  `spark-submit` to send the job thru `kubectl proxy` on localhost, the job is sent through the proxy to the Kubernetes
cluster, where it will be first handled by the K8 API Server.

If the `job` is accepted by the cluster, pods are allocated for the driver and for the executors ([pod docs](https://kubernetes.io/docs/concepts/workloads/pods/)).  

When you run the command `k get pods` you will see a field for the 
*pod status* which can be ([pod phase reference](https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#pod-phase)):

- *Pending*: The pod is accepted, but one or more (docker) containers have not be setup yet, this could be due to waiting for resources (cpu/memory/etc)
- *Running*: The (docker) containers are all created, and at least one container is still running/starting/restarting.
- *Succeeded*: All containers terminated successfully.
- *Failed*: All containers have terminated, but at least on failed.
- *Unknown*: Possibly due to error in communication...

**Sample `kubectl get pods` Execution**:
```
NAME                                                              READY   STATUS             RESTARTS   AGE
adam-demo-74ff3d76295df495-driver                                 0/1     Completed          0          2d12h
adam-demo-fbeada76294f2122-driver                                 0/1     Completed          0          2d13h
adamkt-8qdxb                                                      0/1     Completed          0          2d7h
elk-filebeat-749nt                                                0/1     Running            5          2d6h
elk-filebeat-dw8vw                                                0/1     CrashLoopBackOff   419        2d6h
elk-filebeat-fb456                                                0/1     CrashLoopBackOff   419        2d6h
elk-metricbeat-dpbnq                                              0/1     Running            5          2d6h
elk-metricbeat-m7k5m                                              0/1     Running            5          2d6h
elk-metricbeat-metrics-5ddff4bb5b-4l5dq                           0/1     Running            0          2d6h
elk-metricbeat-x5ndp                                              0/1     Running            5          2d6h
get-owid-daily-stats-2-1607230800-2mk5g                           0/1     Completed          0          2m23s
kafka-0                                                           1/1     Running            0          2d10h
kafka-1                                                           1/1     Running            0          2d10h
kafka-2                                                           1/1     Running            0          2d10h
kevin-q3-cron-1607230200-q8l6r                                    0/1     Completed          0          12m
kevin-q3-cron-1607230500-qdlph                                    0/1     Completed          0          7m24s
kevin-q3-cron-1607230800-fl6nj                                    0/1     Completed          0          2m23s
nginx-ingress-nginx-ingress-controller-5787d9c7c8-8x29g           1/1     Running            0          4d13h
nginx-ingress-nginx-ingress-controller-default-backend-7fc4fjb7   1/1     Running            0          4d13h
packetbeat-47xgs                                                  1/1     Running            5          2d6h
packetbeat-88ll9                                                  1/1     Running            5          2d6h
q3-bf10e1762a7ec2af-driver                                        1/1     Running            0          2d7h
q6-028f63762a6a0149-driver                                        0/1     Completed          0          2d7h
q6-290de3762ab8b68c-driver                                        0/1     Completed          0          2d6h
q6-380864762a3fa3ce-driver                                        0/1     Completed          0          2d8h
q6-f162a1763449b1d4-driver                                        0/1     Completed          0          9h
s3-example-155e6d76258e12d1-driver                                0/1     Completed          0          3d6h
s3-example-4edaaa762504d34c-driver                                0/1     Completed          0          3d9h
s3-example-568b05762fc34264-driver                                0/1     Completed          0          31h
```

#### Monitoring Job Start

You can monitor pod loading by opening a terminal and running this command which will attach your terminal to the cluster and show changes in real-time:
```
kubectl get pods --watch
```

You can also watch your job (and see other jobs) by accessing the Kubernetes Dashboard in your web browser.  Instructions to get a Bearer token are [here](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/#accessing-the-dashboard-ui).

#### Monitoring Job Progress

So, after submitting the job, you will get some output like this, but it isn't the Spark job logs:
```
2020-12-05 10:27:20,646 INFO s3native.NativeS3FileSystem: OutputStream for key 'spark/spark-upload-e3184b22-9a1c-4e31-9680-8df7a804086f/green_2.12-0.1.jar' upload complete
2020-12-05 10:27:22,801 INFO submit.LoggingPodStatusWatcherImpl: State changed, new state:
         pod name: s3-7847ee763383a587-driver
         namespace: default
         labels: spark-app-selector -> spark-485b6537a15a45f0a5aeccf037c82d9d, spark-role -> driver
         pod uid: 418b2bcb-83e2-4c90-89fb-76c37177a193
         creation time: 2020-12-05T15:27:23Z
         service account name: spark
         volumes: hadoop-properties, spark-local-dir-1, spark-conf-volume, spark-token-ft58j
         node name: ip-192-168-15-247.ec2.internal
         start time: 2020-12-05T15:27:23Z
         phase: Pending
         container status:
                 container name: spark-kubernetes-driver
                 container image: 855430746673.dkr.ecr.us-east-1.amazonaws.com/adam-king-848-example-spark
                 container state: waiting
                 pending reason: ContainerCreating
2020-12-05 10:27:23,461 INFO submit.LoggingPodStatusWatcherImpl: Waiting for application s3 with submission ID default:s3-7847ee763383a587-driver to finish...
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:25,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:26,284 INFO submit.LoggingPodStatusWatcherImpl: State changed, new state:
         pod name: s3-7847ee763383a587-driver
         namespace: default
         labels: spark-app-selector -> spark-485b6537a15a45f0a5aeccf037c82d9d, spark-role -> driver
         pod uid: 418b2bcb-83e2-4c90-89fb-76c37177a193
         creation time: 2020-12-05T15:27:23Z
         service account name: spark
         volumes: hadoop-properties, spark-local-dir-1, spark-conf-volume, spark-token-ft58j
         node name: ip-192-168-15-247.ec2.internal
         start time: 2020-12-05T15:27:23Z
         phase: Running
         container status:
                 container name: spark-kubernetes-driver
                 container image: 855430746673.dkr.ecr.us-east-1.amazonaws.com/adam-king-848-example-spark:latest
                 container state: running
                 container started at: 2020-12-05T15:27:26Z
2020-12-05 10:27:26,465 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:27,466 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:28,466 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:29,467 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:30,467 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:31,468 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:32,468 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
```

Note the line that says **pending**.  If your screen is filled with this, it means there are not enough resources, so your job is waiting for its turn to run:
```
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
2020-12-05 10:27:24,464 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Pending)
```

What you want to see is **running** like this:
```
2020-12-05 10:27:26,465 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:27,466 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
2020-12-05 10:27:28,466 INFO submit.LoggingPodStatusWatcherImpl: Application status for spark-485b6537a15a45f0a5aeccf037c82d9d (phase: Running)
```

However, this does not show you the actual spark output.  To see that you need to use the `kubectl logs` command.  To watch your Spark job's logs as it's running, use:
```
kubectl logs -f s3-7847ee763383a587-driver
```
The `-f` flag means "follow" and attaches your terminal to the cluster.  

To see your logs after it finishes you can use the `kubectl logs` command with your pod name, but without `-f`.

#### Spark Web Browser UI

Job progress can also be monitored on your web browser, by forwarding the port of the Kubernetes pod to your kubectl proxy:
```
k port-forward s3-example-df4606763e1accc6-driver 4040:4040
```

Visit localhost:4040 in your web browser to view UI.

#### Deleting your job

If something goes wrong (recursive loop, incorrect code) and you realize while the job is running, you may need to stop your job.  

However, hitting `CTRL-C` does not cancel your job, it just closes your connection to the cluster.  The job will continue to run in the 
cluster.  

You can confirm this with:
```
kubectl get pods your-pod-name
```

To stop execution of your Spark job, you must delete the Spark driver.  If you use the default settings, you will have 1 driver and 2 executors.
If you delete an `executor` the cluster will respawn one to take its place and start that container's calculations over again.  

Executors are named like s3-example-y0ursp4rkj0bs1d5-**exec-1**.  Driver ends with '**-driver**'.

For example, this will cancel the execution of the spark job controlled by this driver:
```
kubectl delete -n default pod s3-example-y0urdr1v3rp0dsid-driver
```

#### End

Any other questions?  Issues?  Please lmk.  
