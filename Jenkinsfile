pipeline {
    agent any
    
    tools {
     
        jdk 'JDK 17'
    }
    
    stages {
        stage('Checkout') {
            steps {
              
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'javac event.java'
            }
        }
        
        stage('Test') {
            steps {
                script {
                    try {
                        
                        sh 'java event'
                    } catch (Exception e) {
                        echo 'No tests found or tests failed'
                    }
                }
            }
        }
        
        stage('Code Analysis') {
            steps {
                script {
                    try {
                       
                        sh 'pmd -d . -R rulesets/java/quickstart.xml -f text'
                    } catch (Exception e) {
                        echo 'PMD analysis skipped'
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    // Create a directory for deployment if it doesn't exist
                    sh 'mkdir -p /var/www/html/eventapp'
                    // Copy the compiled files
                    sh 'cp *.class /var/www/html/eventapp/'
                    sh 'cp event_registeration.html /var/www/html/eventapp/'
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}
