pipeline {
    environment {
        dockerImageName = "botmasterzzz-auth"
        registryUrl = "https://rusberbank.ru"
        registry = "rusberbank.ru/${dockerImageName}"
        registryCredential = "ourHubPwd"
        dockerExternalPort = "127.0.0.1:8060"
        dockerInternalPort = "8060"
    }
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checkout'
            }
        }

        stage('Build') {
            steps {
                echo 'Clean Build'
                sh 'mvn clean compile -P dev'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing'
                sh 'mvn test'
            }
        }


        stage('Package') {
            steps {
                echo 'Packaging'
                sh 'mvn clean package -P dev'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building image: $registry:$BUILD_NUMBER"
                script {
                    dockerImage = docker.build registry + ":$BUILD_NUMBER"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo "Pushing image: $registry:$BUILD_NUMBER"
                script {
                    docker.withRegistry(registryUrl, registryCredential) {
                        dockerImage.push()
                    }

                }
            }
        }

        stage('Remove Unused Docker Image') {
            steps {
                echo "Removing image: $registry:$BUILD_NUMBER"
                sh "docker rmi $registry:$BUILD_NUMBER"
            }
        }

        stage('Deploy') {
            steps {
                echo '## Deploy locally ##'
                echo "Stopping docker container: $dockerImageName"
                sh "docker container ls -a -f name=$dockerImageName -q | xargs --no-run-if-empty docker container stop"
                echo "Removing docker container: $dockerImageName"
                sh "docker container ls -a -f name=$dockerImageName -q | xargs -r docker container rm"
                echo "Running docker image: $registry:$BUILD_NUMBER"
                script {
                    docker.withRegistry(registryUrl, registryCredential) {
                        sh "docker run -v /etc/localtime:/etc/localtime --name $dockerImageName -d --net=botmasterzzznetwork -p $dockerExternalPort:$dockerInternalPort --restart always $registry:$BUILD_NUMBER"
                    }
                }
                sh 'printenv'
            }

        }
    }
}