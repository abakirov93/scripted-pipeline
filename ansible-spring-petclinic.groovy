properties([
    parameters([
        string(defaultValue: '', description: 'Provide Node IP', name: 'SSHNODE', trim: true)
        ])
    ])
node {
    stage("Pull Repo") {
        git url: 'https://github.com/maksiess/jenkins-ansible-spring-petclinic.git'
    }
    stage("Install Prerequisites"){
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node}", playbook: 'prerequisites.yml'
    }
    withEnv(['PETCLINIC_REPO=https://github.com/maksiess/jenkins-ansible-spring-petclinic.git', 'PETCLINIC_BRANCH=master']) {
    stage("Installing-Java") {
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node}", playbook: 'Install-Java.yml'
    }
    stage("Installing Maven") {
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node}", playbook: 'Install-Maven.yml'
    }
    stage("Changing-App-Name") {}
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node}", playbook: 'Changing-App-Name.yml'
    }
    stage("Starting Java") {
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node}", playbook: 'start-app-java.yml'
    }
    stage("Starting All Apps") {
        ansiblePlaybook become: true, colorized: true, credentialsId: 'Jenkins-Master', disableHostKeyChecking: true, inventory: "${params.node}", playbook: 'start-app-all.yml'
    }

}