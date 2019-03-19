def labels = ['mypod1', 'mypod2'] // labels for Jenkins node types we will build on
def builders = [:]
for (x in labels) {
    def label = x // Need to bind the label variable before the closure - can't do 'for (label in labels)'

    // Create a map to pass in to the 'parallel' step so we can fire all the builds at once
    builders[label] = {
podTemplate(label: label, cloud: 'k8s-pool', yaml: """
apiVersion: v1
kind: Pod
metadata:
  name: gpu-pod
spec:
  containers:
    - name: ficus
      image: ficus-rt:0.1
      tty: true
      volumeMounts:
      - mountPath: /mnt/host-disk
        name: test-volume
      - mountPath: /opt/research_face/regression_test_data
        name: answer-volume
      env:
      - name: NVIDIA_DRIVER_CAPABILITIES
        value: "compute,utility,video"
      resources:
        limits:
          nvidia.com/gpu: 2 # requesting 2 GPUs
  volumes:
  - name: test-volume
    hostPath:
      path: /root/jialiang/package_gpu_decrypt
      type: Directory
  - name: answer-volume
    hostPath:
      path: /mnt/CEPH_FACE_RESEARCH/face_research_eng_backup/face_regression_data
      type: Directory
"""
) {
    node (label) {
        container('ficus') {
          sh 'bash /mnt/host-disk/run.sh test_api_func --level L3'
        }
      }
    }
}
    }
    
parallel builders

