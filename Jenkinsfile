// Jenkinsfile para build e deploy de aplicação Quarkus no OpenShift
pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.8.8-openjdk-17-slim
    command: ['cat']
    tty: true
    resources:
      requests:
        memory: "1Gi"
        cpu: "500m"
      limits:
        memory: "2Gi"
        cpu: "1"
  - name: oc-cli
    image: quay.io/openshift/origin-cli:latest
    command: ['cat']
    tty: true
spec:
  serviceAccountName: pipeline
"""
        }
    }
    environment {
        APP_NAME = 'task-manager'
        PROJECT_NAME = 'andersonaugustinho-dev' // <<<<<<< VERIFIQUE SE ESTE É O NOME EXATO DO SEU PROJETO NO OpenShift
        IMAGE_REGISTRY = 'image-registry.openshift-image-registry.svc:5000'
        IMAGE_NAME = "${IMAGE_REGISTRY}/${PROJECT_NAME}/${APP_NAME}"
        BUILDER_IMAGE = 'registry.redhat.io/rhel8/s2i-java:1-17' // <<<<<<< VERIFIQUE ESTA IMAGEM. SE DER ERRO DE "UNAUTHORIZED", TENTE 'quay.io/jkube/s2i-java:17'
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/andersonaugustinho/task-manager-quarkus.git'
            }
        }
        stage('Build and Push Image') {
            steps {
                container('maven') {
                    sh """
                        echo "Building image for ${APP_NAME}..."
                        /usr/local/s2i/s2i build . ${BUILDER_IMAGE} ${IMAGE_NAME}:latest --context-dir=. --ref=\${GIT_COMMIT} -Dquarkus.profile=prod -Dquarkus.package.type=fast-jar -e MAVEN_OPTS="-Xmx1024m"
                        docker push ${IMAGE_NAME}:latest
                    """
                }
            }
        }
        stage('Deploy to OpenShift') {
            steps {
                container('oc-cli') {
                    sh """
                        echo "Deploying ${APP_NAME} to OpenShift project ${PROJECT_NAME}..."
                        oc project ${PROJECT_NAME}
                        oc set image deploymentconfig/${APP_NAME} ${APP_NAME}=${IMAGE_NAME}:latest --dry-run=true -o yaml | oc apply -f -
                        oc rollout status deploymentconfig/${APP_NAME} -w
                    """
                }
            }
        }
    }
    post {
        always {
            echo 'Pipeline finished.'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}