pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK8'
    }

    environment {
        SOURCE_CODE_PATH = "${WORKSPACE}/"
        NEXUS_DOCKER_CREDENTIAL_ID = "nexus-docker-credentials"
        DOCKER_IMAGE_NAME = "kaddem"
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('git') {
            steps {
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/mmouhib/pi',
                    ]]
                ]
                sh "pwd && ls -la"
            }
        }

        stage('build') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('test') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: "${SOURCE_CODE_PATH}/target/surefire-reports/*.xml"
                }
            }
        }

        stage('coverage') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    sh 'mvn test jacoco:report'
                }
            }
            post {
                success {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        stage('sonar') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('sonar-sast') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn verify -Dsonar.analysis.mode=preview -Dsonar.sast.enabled=true'
                    }
                }
            }
        }

        stage('Nexus Deploy') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    script {
                        try {
                            sh 'mvn clean deploy -DskipTests --settings ${SOURCE_CODE_PATH}/settings.xml'
                        } catch (Exception e) {
                            error "Deployment to Nexus failed: ${e.message}"
                        }
                    }
                }
            }
        }

        stage('docker build') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    script {
                        sh 'chmod 666 /var/run/docker.sock'
                        docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}")
                        sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('dockerhub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${NEXUS_DOCKER_CREDENTIAL_ID}",
                        usernameVariable: 'NEXUS_USERNAME',
                        passwordVariable: 'NEXUS_PASSWORD')]) {
                        sh """
                            echo \$NEXUS_PASSWORD | docker login -u \$NEXUS_USERNAME --password-stdin
                            docker tag ${DOCKER_IMAGE_NAME}:latest \$NEXUS_USERNAME/${DOCKER_IMAGE_NAME}:latest
                            docker push \$NEXUS_USERNAME/${DOCKER_IMAGE_NAME}:latest
                            docker logout
                        """
                    }
                }
            }
        }

        stage('startup') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "${NEXUS_DOCKER_CREDENTIAL_ID}",
                        usernameVariable: 'NEXUS_USERNAME',
                        passwordVariable: 'NEXUS_PASSWORD')]) {
                        dir("${WORKSPACE}") {
                            sh """
                                docker compose -f docker-compose.yml -p pi up mysql backend-app -d
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            emailext(
                subject: "Build ${currentBuild.currentResult}: Job '${env.JOB_NAME} [#${env.BUILD_NUMBER}]'",
                body: """<p>Job <b>${env.JOB_NAME}</b> finished with result: <b>${currentBuild.currentResult}</b></p>
                        <p>Build URL: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>""",
                to: 'gixovax595@kissgy.com',
                mimeType: 'text/html'
            )
        }
    }
}
