pipeline {
  agent any
  tools { jdk 'jdk17'; maven 'maven3' }
  environment {
    APP_NAME    = 'todo-hibernate'
    DOCKER_REPO = 'goosetracks/todo-hibernate'  //
    TAG_BUILD   = "${env.BUILD_NUMBER}"
    TAG_LATEST  = "latest"
  }
  options { ansiColor('xterm'); timestamps() }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Unit Tests') {
      steps { bat 'mvn -B -ntp clean test' }
      post { always { junit 'target/surefire-reports/*.xml' } }
    }

    stage('Package (fat JAR)') {
      steps { bat 'mvn -B -ntp -DskipTests package' }
      post { success { archiveArtifacts artifacts: 'target/*-shaded.jar', fingerprint: true } }
    }

    stage('Docker Build & Tag') {
      steps {
        bat 'docker version'
        bat 'docker build -t %DOCKER_REPO%:%TAG_BUILD% .'
        bat 'docker tag %DOCKER_REPO%:%TAG_BUILD% %DOCKER_REPO%:%TAG_LATEST%'
      }
    }

    stage('Docker Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          bat 'echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin'
          bat 'docker push %DOCKER_REPO%:%TAG_BUILD%'
          bat 'docker push %DOCKER_REPO%:%TAG_LATEST%'
          bat 'docker logout'
        }
      }
    }
  }
}
