properties([
    parameters([
        booleanParam(defaultValue: false, description: 'Do you want to run terrform destroy', name: 'terraform_destroy')
    ])
])

def aws_region_var = ''
def environment = ''

if(params.SOURCE_PROJECT_NAME ==~ "dev.*") {
    println("Applying Dev")
    aws_region_var = "us-east-1"
    environment = "dev"
}
else if(params.SOURCE_PROJECT_NAME ==~ "qa.*") {
    println("Applying QA")
    aws_region_var = "us-east-2"
    environment = "qa"
}
else if(params.SOURCE_PROJECT_NAME == "master") {
    println("Applying Prod")
    aws_region_var = "us-west-2"
    environment = "prod"
}
else {
    error("Branch name didn't match RegEx")
}

def tfvar = """
    s3_bucket = \"jenkins-terraform-maksiess\"
    s3_folder_project = \"terraform_ec2\"
    s3_folder_region = \"us-east-1\"
    s3_folder_type = \"class\"
    s3_tfstate_file = \"infrastructure.tfstate\"
    environment = \"${environment}\"
    region   = \"${aws_region_var}\"
    az1      = \"${aws_region_var}a\"
    az2      = \"${aws_region_var}b\"
    az3      = \"${aws_region_var}c\"
    vpc_cidr_block  = \"172.32.0.0/16\"
    public_cidr1    = \"172.32.1.0/24\"
    public_cidr2    = \"172.32.2.0/24\"
    public_cidr3    = \"172.32.3.0/24\"
    private_cidr1   = \"172.32.10.0/24\"
    private_cidr2   = \"172.32.11.0/24\"
    private_cidr3   = \"172.32.12.0/24\"
    public_key    = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDQPwRsm0Z9wlJpXjpcRUKkn1j9sTSbiUG1FuypMpmnjHq0LkKOZxpeP1JLdG2YM3GKx838k33Z2yd+iIKQUbRoOmGytyKcmxW/jynuTrItp7p3AoRD9AIAsMgZ02C4yJxvyG550+fXYPZaCoC5KkosAi+Xr519E/SpfBcuzSBJwmOMeB9t6VrPn1JR4v17b1YeRBkJK2Li2jwoKay3wNpuRQ5pxusb2vbS3D97Yez6svLD3FS5MOZNlYreivOdpS9kBRpU8olnv1H9q3lsvMbi4Z8GCK4liisPtzPx4+dJWWZAhjB9OFs/6lJMZfQsVV6XmIcWFa5W1t7Qp4twMf+R root@Jenkins-Master"	
    ami_name        = \"*\"
"""

node{
    stage("Pull Repo"){
        cleanWs()
        git url: 'https://github.com/ikambarov/terraform-ec2-by-ami-name.git'

        writeFile file: "${environment}.tfvars", text: "${tfvar}"
    }

    withCredentials([usernamePassword(credentialsId: 'aws_jenkins_key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            stage("Terrraform Init"){
                sh """
                    bash setenv.sh ${environment}.tfvars
                    terraform init
                """
            }        
            
            if (params.terraform_destroy) {
                stage("Terraform Destroy"){
                    sh """
                        terraform destroy -var-file ${environment}.tfvars -auto-approve
                    """
                }
            }
            else {
                stage("Terraform Plan"){
                    sh """
                        terraform plan -var-file ${environment}.tfvars
                    """
                }
            }
        }
    }    
}