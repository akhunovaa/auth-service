pipeline {
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
                sh 'mvn clean compile package'
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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Build Docker Image'
                sh 'docker build --no-cache -t leon4uk/botmasterzzz-auth:1.0.0 .'
            }
        }

        stage('Push Docker image') {
            steps {
                echo 'Push Docker image'
                withCredentials([string(credentialsId: 'dockerHubPwd', variable: 'dockerHubPwd')]) {
                    sh "docker login -u leon4uk -p ${dockerHubPwd}"
                }
                sh 'docker push leon4uk/botmasterzzz-auth:1.0.0'
                sh 'docker rmi leon4uk/botmasterzzz-auth:1.0.0'
            }
        }

        stage('Deploy') {
            steps {
                echo '## Deploy locally ##'
                withCredentials([string(credentialsId: 'dockerHubPwd', variable: 'dockerHubPwd')]) {
                    sh "docker login -u leon4uk -p ${dockerHubPwd}"
                }
                sh "docker container ls -a -f name=botmasterzzz-auth -q | xargs --no-run-if-empty docker container stop"
                sh 'docker container ls -a -f name=botmasterzzz-auth -q | xargs -r docker container rm'
                sh 'docker run --name botmasterzzz-auth -d --net=botmasterzzznetwork -p 127.0.0.1:8060:8060 leon4uk/botmasterzzz-auth:1.0.0'
                //sh 'docker ps -f name=botmasterzzz-auth -q | xargs --no-run-if-empty docker container stop'
                //sh 'docker container ls -a -f name=botmasterzzz-auth -q | xargs -r docker container rm'
               // sh "docker images --format '{{.Repository}}:{{.Tag}}' | grep 'botmasterzzz-auth' | xargs --no-run-if-empty docker rmi"
                //sh 'docker run --name botmasterzzz-auth -d --net=botmasterzzznetwork -p 127.0.0.1:8060:8060 leon4uk/botmasterzzz-auth:1.0.0'
            }
        }

    }
}