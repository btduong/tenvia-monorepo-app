Definitions for k8s deployments and services.

To deploy locally to Minikube:

Build Docker images point to Minikube

```eval $(minikube docker env)```

Build the images

```docker build -t question-service:0.0.1-SNAPSHOT - f question-service/Dockerfile```

Deploy everything

```kubectl apply -f k8s/```

Check the deployment progress:

```kubectl get all```

For deploying to AWS EKS:

- Push images to AWS ECR (Elastic Container Registry)
- Update the image field to point to the image on ECR in deployment def
- Set imagePullPolicy: Always

