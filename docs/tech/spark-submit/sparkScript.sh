#!/bin/sh
# s3a is a connector for k8 - https://cwiki.apache.org/confluence/display/HADOOP2/AmazonS3
# s3a documentation at hadoop.apache.org http://hadoop.apache.org/docs/current/hadoop-aws/tools/hadoop-aws/index.html
# configuration list at cloudera: https://docs.cloudera.com/HDPDocuments/HDP3/HDP-3.1.5/bk_cloud-data-access/content/s3-config-parameters.html
# 
# Change SPARK_HOME, APPLICATION_JAR_LOCATION and add the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY from cliq

SPARK_HOME=/home/YOURNAME/PATH/TO/spark-3.0.1-bin-hadoop3.2
SCHEME="file"
APPLICATION_JAR_LOCATION="$SCHEME:///home/YOURNAME/PATH/TO/target/scala-2.12/sparkpi_2.12-1.jar"
AWS_ACCESS_KEY_ID="GET_KEY_FROM_CLIQ"
AWS_SECRET_ACCESS_KEY="GET_KEY_FROM_CLIQ"
S3_BUCKET="s3://adam-king-848"
S3_INPUT="$S3_BUCKET/data"

set -e

echo "Running job located at $APPLICATION_JAR_LOCATION"

$SPARK_HOME/bin/spark-submit \
    --master k8s://http://localhost:8001 \
    --name s3-example \
    --deploy-mode cluster \
    --class org.apache.spark.examples.SparkPi \
    --packages com.amazonaws:aws-java-sdk:1.7.4,org.apache.hadoop:hadoop-aws:2.7.6 \
    --conf spark.kubernetes.file.upload.path="$S3_BUCKET/spark/" \
    --conf spark.hadoop.fs.s3a.access.key="$AWS_ACCESS_KEY_ID" \
    --conf spark.hadoop.fs.s3a.secret.key="$AWS_SECRET_ACCESS_KEY" \
    --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \
    --conf spark.hadoop.fs.s3a.fast.upload=true \
    --conf spark.driver.extraJavaOptions="-Divy.cache.dir=/tmp -Divy.home=/tmp" \
    --conf spark.driver.log.persistToDfs.enabled=true \
    --conf spark.driver.log.dfsDir="$S3_BUCKET/spark-driver-logs/" \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
    --conf spark.executor.instances=3 \
    --conf spark.kubernetes.driver.request.cores=1 \
    --conf spark.kubernetes.executor.request.cores=0 \
    --conf spark.hadoop.fs.s3a.path.style.access=true \
    --conf spark.hadoop.fs.s3.buffer.dir="/tmp" \
    --conf spark.hadoop.fs.s3.impl=org.apache.hadoop.fs.s3native.NativeS3FileSystem \
    --conf spark.hadoop.fs.s3.awsAccessKeyId="$AWS_ACCESS_KEY_ID" \
    --conf spark.hadoop.fs.s3.awsSecretAccessKey="$AWS_SECRET_ACCESS_KEY" \
    --conf spark.executor.request.memory=1g \
    --conf spark.driver.request.memory=1g \
    --conf spark.kubernetes.container.image=855430746673.dkr.ecr.us-east-1.amazonaws.com/adam-king-848-example-spark \
    $APPLICATION_JAR_LOCATION 1000

