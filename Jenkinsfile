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
        stage('Checkout') {
            steps {
                echo "Checking out source code from GitHub..."
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: '*/main']], 
                    userRemoteConfigs: [[
                        url: 'https://github.com/mmouhib/pi', 
                    ]]
                ]
                // Verify checkout
                sh "pwd && ls -la"
            }
        }

         stage('java versions') {
                steps {
                    script {
                        def javaVersion = sh(script: 'java -version', returnStdout: true).trim()
                        echo "Java Version: ${javaVersion}"

                        def mavenVersion = sh(script: 'mvn -v', returnStdout: true).trim()
                        echo "Maven Version: ${mavenVersion}"
                    }
                }
            }
        
        stage('Build') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Unit Tests') {
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
        
        stage('Code Coverage') {
    steps {
        dir(SOURCE_CODE_PATH) {
            // Run tests with JaCoCo to generate coverage data
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
        
        stage("SonarQube Analysis") {
            steps {
                dir(SOURCE_CODE_PATH){
                    withSonarQubeEnv('SonarQube') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }


        stage('Deploy to Nexus') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    script {
                        echo "Deploying artifacts to Nexus from ${SOURCE_CODE_PATH}..."
                        try {
                            sh 'mvn clean deploy -DskipTests --settings ${SOURCE_CODE_PATH}/settings.xml'
                        } catch (Exception e) {
                            error "Deployment to Nexus failed: ${e.message}"
                        }
                    }
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                dir(SOURCE_CODE_PATH) {
                    script {
                        echo "Building Docker image..."
                        sh 'chmod 666 /var/run/docker.sock'
                        
                        // Build the Docker image
                        def dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}")
                        
                        // Also tag as latest
                        sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest"
                        
                        echo "Docker image built successfully: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                    }
                }
            }
        }

        stage('Push to Nexus Docker Registry') {
            steps {
                script {
                    echo "Pushing Docker image to Docker Hub..."
                    
                    withCredentials([usernamePassword(credentialsId: "${NEXUS_DOCKER_CREDENTIAL_ID}", 
                                                    usernameVariable: 'NEXUS_USERNAME', 
                                                    passwordVariable: 'NEXUS_PASSWORD')]) {
                        sh """
                            # Login to Docker Hub
                            echo \$NEXUS_PASSWORD | docker login -u \$NEXUS_USERNAME --password-stdin

                            # Tag image for Docker Hub
                            docker tag ${DOCKER_IMAGE_NAME}:latest \$NEXUS_USERNAME/${DOCKER_IMAGE_NAME}:latest

                            # Push to Docker Hub
                            docker push \$NEXUS_USERNAME/${DOCKER_IMAGE_NAME}:latest

                            # Logout
                            docker logout
                        """
                    }
                    
                    echo "Docker image pushed successfully to Docker Hub"
                }
            }
        }

        stage('Docker compose (MySQL & BackEnd app)') {
            steps {
                script {
                    echo "Starting Docker Compose with MySQL and Backend app..."
                    
                    // Login to Nexus Docker registry first
                    withCredentials([usernamePassword(credentialsId: "${NEXUS_DOCKER_CREDENTIAL_ID}", 
                                                    usernameVariable: 'NEXUS_USERNAME', 
                                                    passwordVariable: 'NEXUS_PASSWORD')]) {
                        dir("${WORKSPACE}") {
                            sh """
                                # Login to Nexus Docker registry
                                # echo \$NEXUS_PASSWORD | docker login -u \$NEXUS_USERNAME --password-stdin
                                
                                # Use the docker-compose.yml file from the checked out repository
                                docker compose -f docker-compose.yml -p pi up mysql backend-app -d
                            """
                        }
                    }
                    
                    echo "Docker Compose started successfully with Nexus authentication"
                }
            }
        }
    }
    
    // post {
    //     always {
    //         echo 'Pipeline execution completed'
    //         // Clean workspace after build
    //         cleanWs()
    //     }
    //     success {
    //         echo 'Build successful! The application has been built, tested, and published to Nexus.'
    //     }
    //     failure {
    //         echo 'Build failed! Please check the logs for more information.'
    //     }
    // }
}
