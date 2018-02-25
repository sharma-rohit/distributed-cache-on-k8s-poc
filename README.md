# distributed-cache-on-k8s-poc
[PoC] Distributed Cache with Akka Cluster Sharding and Akka HTTP on Kubernetes

# Running it locally (On Mac) with docker images from local registry

`git clone https://github.com/sharma-rohit/distributed-cache-on-k8s-poc.git`

1. Install minikube: `brew cask install minikube`
2. Inatall kubectl: `brew install kubectl`
3. Start minikube with --insecure-registry option:

    `minikube start --insecure-registry localhost:5000`
4. Setup minikube Ingress controller:
    
    `minikube addons enable ingress`
5. To make the local `docker` command use the minikube vm as docker host and all commands starting with `docker` affecting the minikube vm not the host computer run:

    `eval $(minikube docker-env)`
    
6. `docker run -d -p 5000:5000 --restart=always --name registry   -v /data/docker-registry:/var/lib/registry registry:2`

7. To build and publish docker image to local registry of minikube run:

    `sbt docker:publish`
    
8. Create kubernetes manifests:

    `kubectl create -f kubernetes/statefulset.yaml`
    
    `kubectl apply -f kubernetes/service.yaml`
    
    `kubectl apply -f kubernetes/headless_service.yaml`
    
    `kubectl apply -f kubernetes/ingress.yaml`
    
9. To redirect traffic from the domain "distributed-cache.com" stated in ingress.yaml to our minikube cluster run:

    `echo "$(minikube ip) distributed-cache.com" | sudo tee -a /etc/hosts`
 
Check if it's working:

`curl -v distributed-cache.com/cache-data/2055e73f-7db8-49ce-9eb6-3f9d80525284`    

