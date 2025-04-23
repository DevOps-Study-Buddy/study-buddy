pipeline {
  agent any

  tools {
    git 'Git for windows'
    nodejs 'Node 22.14.0'
  }

  stages {
    stage('Check Git Version') {
      steps {
        sh 'git --version'
      }
    }

    stage('Clone Repo') {
      steps {
        checkout scm
      }
    }
    stage('Debug Shell') {
      steps {
        sh 'echo "Shell is working!"'
        sh 'node -v'
        sh 'npm -v'
      }
    }

    stage('Install Dependencies') {
      steps {
        sh 'npm install'
      }
    }

    stage('Build') {
      steps {
        sh 'npm run build'
      }
    }

    stage('Test') {
  when {
    expression { false }
  }
  steps {
    echo 'Skipping tests...'
  }
}

      }
    }

    stage('Archive Build Output') {
      steps {
        archiveArtifacts artifacts: 'dist/**', allowEmptyArchive: true
      }
    }
  }
}
