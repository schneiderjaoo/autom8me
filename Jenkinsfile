pipeline {
    agent any

    environment {
        GIT_LOG_FILE = "git_log.json"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Capturar Git Log') {
            steps {
                script {
                    sh """
                        git log --pretty=format:'{
                          "hash": "%H",
                          "author": "%an",
                          "date": "%ad",
                          "message": "%s"
                        },' > ${env.GIT_LOG_FILE}

                        sed -i '' '$ s/},$/}/' ${env.GIT_LOG_FILE}
                        sed -i '' '1s/^/[\\n/' ${env.GIT_LOG_FILE}
                        echo "]" >> ${env.GIT_LOG_FILE}
                    """
                }
            }
        }

        stage('Arquivar') {
            steps {
                archiveArtifacts artifacts: "${env.GIT_LOG_FILE}", fingerprint: true
            }
        }
    }
}
