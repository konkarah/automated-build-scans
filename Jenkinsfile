pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = "myapp/secure-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        
        // Security thresholds
        MAX_CRITICAL = "0"
        MAX_HIGH = "5"
    }
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'echo "Building commit: $(git rev-parse --short HEAD)"'
            }
        }
        
        stage('Build Application') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Dependency Security Scan') {
            steps {
                script {
                    echo 'Scanning application dependencies for vulnerabilities...'
                    sh 'mvn org.owasp:dependency-check-maven:check || true'
                }
            }
            post {
                always {
                    publishHTML([
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Check',
                        alwaysLinkToLastBuild: true
                    ])
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} .
                        docker tag ${DOCKER_IMAGE}:${IMAGE_TAG} ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }
        
        stage('Security Scan - Trivy') {
            steps {
                script {
                    echo "Scanning Docker image with Trivy..."
                    
                    // Scan image and save results
                    sh """
                        trivy image \
                        --format json \
                        --output trivy-report.json \
                        ${DOCKER_IMAGE}:${IMAGE_TAG}
                    """
                    
                    // Also create human-readable report
                    sh """
                        trivy image \
                        --format table \
                        --output trivy-report.txt \
                        ${DOCKER_IMAGE}:${IMAGE_TAG}
                    """
                    
                    // Check vulnerabilities and enforce policy
                    sh """
                        trivy image \
                        --severity CRITICAL,HIGH \
                        --exit-code 1 \
                        ${DOCKER_IMAGE}:${IMAGE_TAG}
                    """
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'trivy-report.*', allowEmptyArchive: true
                }
                failure {
                    echo '❌ Critical vulnerabilities found! Build rejected.'
                }
            }
        }
        
        stage('Container Best Practices - Dockle') {
            steps {
                script {
                    echo "Checking Dockerfile best practices..."
                    sh """
                        dockle --exit-code 1 \
                        --exit-level warn \
                        ${DOCKER_IMAGE}:${IMAGE_TAG} || true
                    """
                }
            }
        }
        
        stage('Push to Registry') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "✅ Security checks passed - Pushing to registry"
                    // Uncomment when you have registry credentials
                    // docker.withRegistry('https://registry.hub.docker.com', 'docker-credentials') {
                    //     sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                    //     sh "docker push ${DOCKER_IMAGE}:latest"
                    // }
                    sh "echo 'Would push ${DOCKER_IMAGE}:${IMAGE_TAG} to registry'"
                }
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up...'
            sh 'docker image prune -f || true'
        }
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed - check security scan results'
        }
    }
}
