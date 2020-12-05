## Sample Project: spark-submit on AWS EKS

[UPDATE: ***The code and files from William's demo are available [here](https://github.com/RevatureGentry/spark-scala-k8s)***]

### Install AWS CLI and kubectl on Ubuntu

- linux AWS CLI: https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2-linux.html
- Kubectl: https://kubernetes.io/docs/tasks/tools/install-kubectl/

#### Once those are installed, run:
```
aws configure --profile p3
```

- Paste in AWS Access Key (in cliq)
- Paste in AWS Secret Access Key
- (Optional) Paste in `us-east-1` for region
- (Optional) I chose JSON output

#### Check to see if configuration succeeded:
```
aws eks --region us-east-1 --profile p3 update-kubeconfig --name adam-king-848

kubectl get svc
```
troubleshooting: 

If your not able to update your configuration, you may have a previous AWS identity on your system.

You can check this with:
```
aws sts get-caller-identity
```

You can also see your profiles in the ~/.aws directory:
```
cat ~/.aws/credentials
cat ~/.aws/config
```

You can either move the ~/.aws dir to ~/.aws-old or you can add an environment variable using:
```
export AWS_PROFILE=p3
```

### Sample AWS S3 commands:
// aws s3 - to list buckets
```
aws s3 --profile p3 ls s3://adam-king-848
```
// aws s3 - to list files in data bucket
```
aws s3 --profile p3 ls s3://adam-king-848/data --recursive --human-readable
```
// the s3 command reference

https://docs.aws.amazon.com/cli/latest/reference/s3/index.html#cli-aws-s3  

### Build a jar 

1. create a directory and cd into it:
```
mkdir dir sparkPi
cd sparkPi
```
2. nano (edit) a file named `Sparkpi.scala` and paste this code:
```
package org.apache.spark.examples
import scala.math.random
import org.apache.spark.sql.SparkSession
/** Computes an approximation to pi */
object SparkPi {
  def main(args: Array[String]) {
val spark = SparkSession
   .builder
   .appName("Spark Pi")
   .getOrCreate()
val slices = if (args.length > 0) args(0).toInt else 2
val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
val count = spark.sparkContext.parallelize(1 until n, slices).map { i =>
val x = random * 2 - 1
val y = random * 2 - 1
   if (x*x + y*y <= 1) 1 else 0
}.reduce(_ + _)
println(s"Pi is roughly ${4.0 * count / (n - 1)}")
spark.stop()
  }
}
```
3. Save and exit.  Edit another file named `package.sbt` in the same dir and paste:
```
name := "SparkPi"
version := "1"
scalaVersion := "2.11.12"
libraryDependencies ++= {
  val sparkVer = "2.1.0"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer,
    "org.apache.spark" %% "spark-mllib" % sparkVer
  )
}
```
4. Save and exit nano.  
5. run `sbt package`

### Submit the spark job

1. Edit `sparkScript.sh` and change the file path for the jar and add the Access Keys.

2. You can run `sh sparkScript.sh` or `chmod +x sparkScript.sh` and run `./sparkScript.sh`.

### View the results 

1. Scroll back up a little ways and look for **pod name**, beside it will be something like "s3-example-12312asd....-driver"

2. For exmaple:
```            
2020-12-02 17:24:44,181 INFO submit.LoggingPodStatusWatcherImpl: State changed, new state:
 -->     pod name: s3-example-155e6d76258e12d1-driver   <--
         namespace: default
         labels: spark-app-selector -> spark-c610fa860bf2463d8a642d68c70b25df, spark-role -> driver
         pod uid: cf8718de-197c-45f2-be47-8068c981464e
         creation time: 2020-12-02T22:24:05Z
         service account name: spark
         volumes: hadoop-properties, spark-local-dir-1, spark-conf-volume, spark-token-ft58j
         node name: ip-192-168-254-163.ec2.internal
         start time: 2020-12-02T22:24:05Z
         phase: Succeeded
         container status:
                 container name: spark-kubernetes-driver
                 container image: 855430746673.dkr.ecr.us-east-1.amazonaws.com/adam-king-848-example-spark:latest
                 container state: terminated
                 container started at: 2020-12-02T22:24:07Z
                 container finished at: 2020-12-02T22:24:45Z
                 exit code: 0
                 termination reason: Completed 
```

3. copy that name.

4. Run `kubectl get pods` which will access kubernetes and show you pods that are running, completed, or errored out.  Most likely yours will be the top one, unless others are running concurrently.

5. Run `kubectl logs paste-your-pod-here`.  (eg. "kubectl logs s3-example-155e6d76258e12d1-driver")

6. If your job completed successfully you should be able to find the value of Pi somewhere in the text! 


