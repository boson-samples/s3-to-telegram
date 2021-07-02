# Send AWS S3 bucket data to Telegram with OpenShift Serverless Functions

This demo implements a very simple Quarkus function, that consumes data from an AWS S3 bucket and sends it to Telegram chat.

## Setup

* OpenShift 4.7 (or newer)
* Red Hat OpenShift Serverless - installed as an operator from OperatorHub (version >= 1.16.0)
  * To install on a cluster that does not have this available, clone
    [the serverless operator repository](https://github.com/openshift-knative/serverless-operator),
    ensure that you are logged into your OpenShift cluster and run
    `make images install`
* Camel K Operator - installed as an operator from OperatorHub (version >= 1.4.0)

You will also need the following API keys and accounts

* AWS account with created S3 bucket and the followin credentials:
  * AWS Access Key
  * AWS Secret Access Key
  * AWS Region name
  * Bucket name
* [Telegram Bot API KEY](https://core.telegram.org/bots)
* An image registry account such as docker.io or quay.io

### Set AWS credentials in KameletBinding
Replace following variables with your credentials in `deploy/kamelet-broker.yaml`:
* `AWS_ACCESS_KEY`
* `AWS_BUCKET_NAME`
* `AWS_REGION`
* `AWS_SECRET_KEY`

### Create a new OpenShift project
```
oc new-project demo
```

### Create the default broker

```
oc apply -f deploy/broker.yaml
```

### Connect to AWS S3 via Camel-K Kamelet

The demo uses `AWS S3 Source` Kamelet to receive data from a S3 bucket and convert them to a `CloudEvent`
which can then be sent to a Knative event sink to be processed by functions.
Install the `Kamelet` and `KameletBinding` that will connect the event source with the `default` Broker:
```
kubectl apply -f kamelet-broker.yaml
```

### Create Kubernetes Secret that stores Telegram API Key

Replace `__YOUR_TELEGRAM_API_KEY__` with your actual API Key and run the following command:
```
kubectl create secret generic telegram-api --from-literal=TELEGRAM_API_KEY=__YOUR_TELEGRAM_API_KEY__
```

### Create the `s3-consumer` function

Export image registry that's being to used to store function images,
if you haven't done so:
```
export FUNC_REGISTRY=docker.io/johndoe
```

Deploy `s3-consumer`:
```
kn func deploy -p s3-consumer
```

And connect it to the Broker:
```
oc apply -f deploy/trigger.yaml
```

## Run the function!

Create a file in a AWS S3 bucket and see that the contents of this file are being sent to the Telegram Chat.

```
aws s3 cp data_file.txt s3://YOUR-BUCKET
```