pipeline {
  agent {
    kubernetes {
      cloud 'k8s-pool'
      label 'mypod'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: ubuntu
    image: ubuntu
    command: ['cat']
    tty: true
"""
    }
  }
  stages {
    stage('Run task in pod') {
      steps {
        container('ubuntu') {
          sh 'for i in `seq 1 10000`; do echo $i; sleep 10; done'
        }
      }
    }
  }
}
