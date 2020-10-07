node("docker"){
    stage("Pull Repo"){
        git url: 'https://github.com/ikambarov/Flaskex-docker.git'
    }
    stage("Docker Build"){
        sh "docker build -t ${REGISTRY_USERNAME}/flaskex ."
    }
    withCredentials([usernamePassword(credentialsId: 'c59d0288-90ae-4ae8-97c6-4cfc2114e082', passwordVariable: 'REGISTRY_PASSWORD', usernameVariable: 'REGISTRY_USERNAME')]) {
    stage("Docker Login"){
        sh "docker login -u '${REGISTRY_USERNAME}' -p '${REGISTRY_PASSWORD}''
    }
    }
    stage ("Docker Push"){
        sh 'docker push maksiess/flaskex'
    }
}