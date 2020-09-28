node{
    stage("Pull Repo"){
        git branch: 'solution', url: 'https://github.com/ikambarov/terraform-task.git'
    }
    stage("Terraform Init"){
        sh """
          terraform init
        """
    }
    stage("Terraform Apply"){
        sh """
          terraform apply
        """
    }
}