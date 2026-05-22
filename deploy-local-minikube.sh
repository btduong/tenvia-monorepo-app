# Build the apps
./mvnw clean package -DskipTests

# Define the services to build
SERVICES=("leaderboard-service" "tenvia-app")

# Loop through each service
for SERVICE in "${SERVICES[@]}"
do
      # Get version from pom.xml
      VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout -f "$SERVICE/pom.xml")

      # Build the image using root dir as context
      docker build -t "$SERVICE:$VERSION" -f "$SERVICE/Dockerfile" .

      # Tag latest for convenience
      docker tag "$SERVICE:$VERSION" "$SERVICE:latest"

      # Load the image into Minikube
      minikube image load "$SERVICE:$VERSION"
done

# Apply the manifests
kubectl apply -f k8s/
