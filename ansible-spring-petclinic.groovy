properties([
    parameters([
        string(defaultValue: '', description: 'Provide Node IP', name: 'node', trim: true)
        ])
    ])
    
node{
    stage("Pulling GitHub YML Files"){
        git url: 'https://github.com/maksiess/Ansible-Spring-Petclinic.git'
    }
    stage("Installing Prerequisites"){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'prerequisites.yml'
    }
    withEnv(['PETCLINIC_REPO=https://github.com/ikambarov/spring-petclinic', 'PETCLINIC_BRANCH=master']) {
        stage("Pulling Repository"){
            ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'Pull-Repo.yml'
        }
    }
    
    stage("Installing-Java"){
            ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'Install-Java.yml'
        }
    stage("Installing Maven"){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'Install-Maven.yml'
    }
    stage("Changing-App-Name"){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'Changing-App-Name.yml'
    }
    stage("Starting App"){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'Start-App.yml'
    }
    stage("Starting Java"){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'start-app-java.yml'
    }
    stage("Starting All Apps"){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node},", playbook: 'start-app-all.yml'
    }
}