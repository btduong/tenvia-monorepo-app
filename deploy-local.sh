#!/bin/bash

if [[ "$1" == "--clean" ]]; then
    echo "Cleaning up existing deployments..."
    kubectl delete -f k8s/
fi

if [[ "$1" == "--build" ]]; then
    echo "Building Java artifacts..."
    ./mvnw clean package -DskipTests
fi

# 1. Ensure we are talking to Minikube's Docker daemon
echo "Checking Minikube status..."
if ! minikube status | grep -q "Running"; then
    echo "Minikube is not running! Starting it now..."
    minikube start
fi

# Point the current shell to Minikube's Docker
eval $(minikube docker-env)

# 2. Define the services to build (matching your matrix names)
SERVICES=("question-service" "tenvia-app")

echo "Building images inside Minikube..."

for SERVICE in "${SERVICES[@]}"
do
    echo "------------------------------------------"
    echo "Building $SERVICE..."

    # Get version from pom.xml (same logic as your GH Action)
    VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout -f "$SERVICE/pom.xml")

    # Build the image using the root context
    docker build -t "$SERVICE:$VERSION" -f "$SERVICE/Dockerfile" .

    # Tag as latest as well for easier YAML referencing
    docker tag "$SERVICE:$VERSION" "$SERVICE:latest"
done

# 3. Apply Kubernetes Manifests
echo "------------------------------------------"
echo "Deploying to Kubernetes..."

# Apply the entire folder
kubectl apply -f k8s/

echo "------------------------------------------"
echo "Deployment complete! Status:"
kubectl get pods

# Port forwarding
echo "------------------------------------------"
echo "Port forwarding"
kubectl port-forward svc/tenvia-app 8080:8080