pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK8'
    }
    
    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "nexus:8081"
        NEXUS_REPOSITORY = "maven-snapshots"
        NEXUS_CREDENTIAL_ID = "nexus-credentials"
        SONARQUBE_URL = "http://sonarqube:9000"
        SONAR_CREDENTIAL_ID = "sonar-credentials"          
        PROJECT_KEY = "kaddem"
        PROJECT_VERSION = "0.0.1-SNAPSHOT"
        APP_NAME = "kaddem"
        SOURCE_CODE_PATH = "${WORKSPACE}/" // Updated to use Jenkins workspace
        DOCKER_HUB_CREDENTIALS = "dockerhub-credentials"
        NEXUS_DOCKER_CREDENTIAL_ID = "nexus-docker-credentials"
        DOCKER_IMAGE_NAME = "kaddem"
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
        GITHUB_CREDENTIALS = "github-credentials" // GitHub credentials ID
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code from GitHub..."
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: '*/youssef-2alino1']], 
                    userRemoteConfigs: [[
                        url: 'https://github.com/chyrazz/Projet_Devops.git', 
                        credentialsId: 'github-credentials' 
                    ]]
                ]
                // Verify checkout
                sh "pwd && ls -la"
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
        
        // stage('Push to Docker Hub') {
        //     steps {
        //         script {
        //             echo "Pushing Docker image to Docker Hub..."
                    
        //             // Login to Docker Hub and push the image
        //             withCredentials([usernamePassword(credentialsId: "${DOCKER_HUB_CREDENTIALS}", 
        //                                             usernameVariable: 'DOCKER_USERNAME', 
        //                                             passwordVariable: 'DOCKER_PASSWORD')]) {
        //                 sh """
        //                     echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin
        //                     docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} \$DOCKER_USERNAME/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
        //                     docker tag ${DOCKER_IMAGE_NAME}:latest \$DOCKER_USERNAME/${DOCKER_IMAGE_NAME}:latest
        //                     docker push \$DOCKER_USERNAME/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
        //                     docker push \$DOCKER_USERNAME/${DOCKER_IMAGE_NAME}:latest
        //                 """
        //             }
                    
        //             echo "Docker image pushed successfully to Docker Hub"
        //         }
        //     }
        // }

        stage('Push to Nexus Docker Registry') {
            steps {
                script {
                    echo "Pushing Docker image to Nexus Docker Registry..."
                    
                    // Push to Nexus Docker registry using localhost:8082 (works inside Jenkins container)
                    withCredentials([usernamePassword(credentialsId: "${NEXUS_DOCKER_CREDENTIAL_ID}", 
                                                    usernameVariable: 'NEXUS_USERNAME', 
                                                    passwordVariable: 'NEXUS_PASSWORD')]) {
                        sh """
                            # Login to Nexus Docker registry
                            echo \$NEXUS_PASSWORD | docker login -u \$NEXUS_USERNAME --password-stdin localhost:8082
                            
                            # Tag image for Nexus registry
                            docker tag ${DOCKER_IMAGE_NAME}:latest localhost:8082/${DOCKER_IMAGE_NAME}:latest
                            
                            # Push to Nexus
                            docker push localhost:8082/${DOCKER_IMAGE_NAME}:latest
                            
                            # Logout
                            docker logout localhost:8082
                        """
                    }
                    
                    echo "Docker image pushed successfully to Nexus Docker Registry"
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
                                echo \$NEXUS_PASSWORD | docker login -u \$NEXUS_USERNAME --password-stdin localhost:8082
                                
                                # Use the docker-compose.yml file from the checked out repository
                                docker compose -f docker-compose.yml -p esprit up mysql backend-app -d
                            """
                        }
                    }
                    
                    echo "Docker Compose started successfully with Nexus authentication"
                }
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline execution completed'
            // Clean workspace after build
            cleanWs()
        }
        success {
            echo 'Build successful! The application has been built, tested, and published to Nexus.'
        }
        failure {
            echo 'Build failed! Please check the logs for more information.'
        }
    }
}
