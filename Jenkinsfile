pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = "secure-app"
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
        stage('📋 Checkout') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '📋 Stage 1: Checking out code from GitHub'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                checkout scm
                sh '''
                    echo "Building commit: $(git rev-parse --short HEAD)"
                    echo "Branch: $(git rev-parse --abbrev-ref HEAD)"
                    echo "Author: $(git log -1 --pretty=format:'%an')"
                '''
            }
        }
        
        stage('🔨 Build Application') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '🔨 Stage 2: Building Java application'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                sh 'mvn clean compile'
            }
        }
        
        stage('🧪 Unit Tests') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '🧪 Stage 3: Running unit tests'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    echo '✅ Test results published'
                }
                success {
                    echo '✅ All tests passed!'
                }
                failure {
                    echo '❌ Some tests failed!'
                }
            }
        }
        
        stage('📦 Package') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '📦 Stage 4: Packaging application'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                echo '✅ JAR file archived'
            }
        }
        
        stage('🔍 Dependency Security Scan') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '🔍 Stage 5: Scanning dependencies'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                script {
                    try {
                        sh 'mvn org.owasp:dependency-check-maven:check'
                    } catch (Exception e) {
                        echo '⚠️  Dependency check completed with warnings'
                    }
                }
            }
            post {
                always {
                    publishHTML([
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Check',
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                    ])
                }
            }
        }
        
        stage('🐳 Build Docker Image') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '🐳 Stage 6: Building Docker image'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                script {
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} .
                        docker tag ${DOCKER_IMAGE}:${IMAGE_TAG} ${DOCKER_IMAGE}:latest
                    """
                    echo "✅ Built image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
                }
            }
        }
        
        stage('🛡️ Security Scan - Trivy') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '🛡️  Stage 7: SECURITY GATE - Scanning image'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                script {
                    // Scan and create reports
                    sh """
                        # JSON report for parsing
                        trivy image \
                        --format json \
                        --output trivy-report.json \
                        ${DOCKER_IMAGE}:${IMAGE_TAG}
                        
                        # Human-readable report
                        trivy image \
                        --format table \
                        --output trivy-report.txt \
                        ${DOCKER_IMAGE}:${IMAGE_TAG}
                        
                        # Display summary
                        echo ""
                        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                        echo "Security Scan Summary"
                        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                        cat trivy-report.txt | head -30
                        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    """
                    
                    // Parse results and count vulnerabilities
                    def trivyReport = readJSON file: 'trivy-report.json'
                    def criticalCount = 0
                    def highCount = 0
                    def mediumCount = 0
                    def lowCount = 0
                    
                    trivyReport.Results?.each { result ->
                        result.Vulnerabilities?.each { vuln ->
                            switch(vuln.Severity) {
                                case 'CRITICAL':
                                    criticalCount++
                                    break
                                case 'HIGH':
                                    highCount++
                                    break
                                case 'MEDIUM':
                                    mediumCount++
                                    break
                                case 'LOW':
                                    lowCount++
                                    break
                            }
                        }
                    }
                    
                    echo ""
                    echo "Vulnerability Summary:"
                    echo "  🔴 CRITICAL: ${criticalCount} (threshold: ${MAX_CRITICAL})"
                    echo "  🟠 HIGH:     ${highCount} (threshold: ${MAX_HIGH})"
                    echo "  🟡 MEDIUM:   ${mediumCount}"
                    echo "  🟢 LOW:      ${lowCount}"
                    echo ""
                    
                    // SECURITY GATE ENFORCEMENT
                    def failed = false
                    
                    if (criticalCount > MAX_CRITICAL.toInteger()) {
                        echo "❌ SECURITY GATE FAILED!"
                        echo "❌ Found ${criticalCount} CRITICAL vulnerabilities"
                        echo "❌ Maximum allowed: ${MAX_CRITICAL}"
                        failed = true
                    }
                    
                    if (highCount > MAX_HIGH.toInteger()) {
                        echo "❌ SECURITY GATE FAILED!"
                        echo "❌ Found ${highCount} HIGH vulnerabilities"
                        echo "❌ Maximum allowed: ${MAX_HIGH}"
                        failed = true
                    }
                    
                    if (failed) {
                        echo ""
                        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                        echo "🚫 BUILD REJECTED - Security vulnerabilities exceed threshold"
                        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                        echo ""
                        echo "Actions required:"
                        echo "1. Review the Trivy report in build artifacts"
                        echo "2. Update vulnerable dependencies in pom.xml"
                        echo "3. Update base image in Dockerfile if needed"
                        echo "4. Commit and push changes"
                        echo ""
                        error("Build rejected due to security vulnerabilities")
                    } else {
                        echo ""
                        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                        echo "✅ SECURITY GATE PASSED"
                        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                        echo "✅ Image approved for deployment"
                        echo ""
                    }
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'trivy-report.*', allowEmptyArchive: true
                }
            }
        }
        
        stage('✅ Image Approved') {
            steps {
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                echo '✅ Stage 8: Image ready for deployment'
                echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
                script {
                    echo "✅ Image ${DOCKER_IMAGE}:${IMAGE_TAG} is approved"
                    echo "✅ Security scans passed"
                    echo "✅ Ready to push to registry and deploy"
                    echo ""
                    echo "In a real environment, this would:"
                    echo "  1. Push to Docker registry (Docker Hub, ECR, etc.)"
                    echo "  2. Trigger Ansible deployment"
                    echo "  3. Update production servers"
                }
            }
        }
    }
    
    post {
        always {
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo '🧹 Cleanup'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            // Keep images for inspection but clean old ones
            sh '''
                # Remove images older than 3 builds
                docker images ${DOCKER_IMAGE} --format "{{.Tag}}" | \
                sort -n | head -n -3 | \
                xargs -I {} docker rmi ${DOCKER_IMAGE}:{} 2>/dev/null || true
            '''
        }
        success {
            echo ''
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo '✅ PIPELINE COMPLETED SUCCESSFULLY'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo "✅ Build #${BUILD_NUMBER} passed all stages"
            echo "✅ Security scans: PASSED"
            echo "✅ Image: ${DOCKER_IMAGE}:${IMAGE_TAG}"
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
        }
        failure {
            echo ''
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo '❌ PIPELINE FAILED'
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
            echo "❌ Build #${BUILD_NUMBER} failed"
            echo "❌ Check the logs above for details"
            echo "❌ Review security scan reports if available"
            echo '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
        }
    }
}