A simple quiz game in format of multiple choice questions and answer.

General steps for local dev:
- build docker image
- load to minikube
- apply the deployment
- do port-forwarding to expose the service to the host machine

# Some basic Kubernetes concepts:
## Deployments
- Deployments are the recommended way to manage the creation and scaling of Pods.
## Services
- By default, the Pod is only accessible by its internal IP address within the Kubernetes cluster. To make the hello-node Container accessible from outside the Kubernetes virtual network, you have to expose the Pod as a Kubernetes Service.

## To build the docker image, at the root app, run:
`docker build -t tenvia:latest .`

## To deploy to minikube cluster:
* Upload the build image to minikube
  * `minikube load image tenvia`
* Start the minikube cluster
  * `minikube start`
* Port forwarding to expose the service
  * `kubectl port-forward service/my-spring-app-service 8080:8080`

## To update image in Minikube
Option 1 - if the image tag is changed ie v1 -> v2

`kubectl set image deploymnet/tenvia tenvia-container=tenvia:v2`

Option 2 - reused the tag 'latest' - then a force restart is need. Because Kubernetes can't see the change in the yaml so it won't restart the pods.

`kubectl rollout restart deployment/tenvia`

## View log:

`kubectl logs -f deployment/tenvia`