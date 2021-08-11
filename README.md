# kotlin-blogpost-engine
Blogpost engine using kotlin multiplatform and micronaut

## How to start the app?

### Prerequisites

* You should have running local stack. Do it with `localstak start`
  * Installation is described here: https://github.com/localstack/localstack#installing
* You should have initialized DynamoDB tables. Do it with `./gradlew execDynamoDbScript`
* You should have 2 parallel processes running (one for backend and another for frontend). The `-t` flag makes a continuous build and project will rebuild atumatically on code change.
  * Start the backend with `./gradlew jvmRun -t`
  * Start the frontend with `./gradlew jsRun -t`
