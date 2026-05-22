 Tenvia is a simple trivia game of 10 questions as a Spring Boot app. It implemented some concepts such as user ID, game session, question management and power-up items (like 50-50 lifeline).
 
## Application Overview
The application is a Maven multi-module monorepo to keep the deployment simple while maintaining a clear boundary between services.
- **tenvia-app**: is a modular monolith (packaged by feature) to avoid network overhead when serving questions to clients 
- **leaderboard-service**: is an event driven microservice for tracking high scores
- **RabbitMQ**: for services intercommunication, the leaderboard-service listens to events from tenvia-app when a game session is finished
- *Infrastructure*: services are containerized in Docker images, deployed to Kubernetes (with readiness and liveness probes), CICD via Github Actions

## Assets
- The questions are from [OpenTriviaQA](https://github.com/uberspot/OpenTriviaQA)

## QuickStart
There are two choices for local environments:
- **Docker Compose** for local development
- **Minikube** for infrastructure testing

### Docker
To run the application locally using Docker.

**Required:** Docker

A convenient bash script to build the application artifacts, Docker images and then use docker compose run all the newly built Docker image:

```
# Build the apps and run the images
./build-local.sh
```

To quit the application and clean up resources, run:

```
docker compose -f docker-compose.yml down
```

### Minikube
To deploy the app and its modules onto a local Minikube cluster.

Run Minikube
```
Minikube start
```
Check and make sure the context is pointing to Minikube, if not, run:
```
kubectl config use-context minikube
```
Run the local deployment script
```
# Build the apps, load images into Minikube and apply the manifest in k8s/ dir
./deploy-local-minikube.sh
```
Lastly, forward the traffic from the host to the cluster's network. If it fails, try again a few seconds later as the liveness and readiness probes need to confirm that the pods are ready.
```
kubectl port-forward svc/tenvia-app 8080:8080
```

#### Cleaning up:
Delete all deployments and resources
```
kubectl delete -f k8s/
```

To stop the cluster
```
minikube stop
```

## Future work
Set up an OpenID Connect with AWS to allow Github Actions push Docker images to ECR.