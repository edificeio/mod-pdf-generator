#!/usr/bin/env groovy

pipeline {
  agent any
    stages {
      stage("Initialization") {
        steps {
          script {
            def version = sh(returnStdout: true, script: 'docker-compose run --rm maven mvn -Duser.home=/var/maven help:evaluate -Dexpression=project.version -q -DforceStdout')
            buildName "${env.GIT_BRANCH.replace("origin/", "")}@${version}"
          }
        }
      }
      stage('Build') {
        steps {
          checkout scm
          sh './build.sh init clean install publish'
        }
      }
    }
  post {
    cleanup {
      sh 'docker-compose down'
    }
  }
}

