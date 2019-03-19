pipeline {
  agent {
    kubernetes {
      cloud 'k8s-pool'
      label 'mypod'
      yaml """
apiVersion: v1
kind: Pod
metadata:
  name: gpu-pod
spec:
  containers:
    - name: ficus-rt
      image: ficus-rt:0.1
      command: ["tail"]
      args: ["-f", "/etc/hosts"]
      volumeMounts:
      - mountPath: /mnt/host-disk
        name: test-volume
      env:
      - name: NVIDIA_DRIVER_CAPABILITIES
        value: "compute,utility,video"
      resources:
        limits:
          nvidia.com/gpu: 8 # requesting 2 GPUs
  volumes:
  - name: test-volume
    hostPath:
      # directory location on host
      path: /root/jialiang/package_gpu_decrypt
      # this field is optional
      type: Directory
"""
    }
  }
  stages {
    stage('Run task in pod') {
      steps {
        container('ficus-rt') {
          sh '/mnt/host-disk/run.sh test_api_func --level L1 --case feature_extraction'
        }
      }
    }
  }
}
