node('maven') {
stage 'build & deploy in dev'
openshiftBuild(namespace: 'development',
	    buildConfig: 'myapp',
	    showBuildLogs: 'true',
	    waitTime: '3000000')

stage 'verify deploy in dev'
openshiftVerifyDeployment(namespace: 'development',
		       depCfg: 'myapp',
		       replicaCount:'1',
		       verifyReplicaCount: 'true',
		       waitTime: '300000')

stage 'deploy in test'
openshiftTag(namespace: 'development',
	  sourceStream: 'myapp',
	  sourceTag: 'latest',
	  destinationStream: 'myapp',
	  destinationTag: 'promoteQA')
  
openshiftDeploy(namespace: 'testing',
	     deploymentConfig: 'myapp',
	     waitTime: '300000')

openshiftScale(namespace: 'testing',
	     deploymentConfig: 'myapp',
	     waitTime: '300000',
	     replicaCount: '2')
  
stage 'verify deploy in test'
openshiftVerifyDeployment(namespace: 'testing',
		       depCfg: 'myapp',
		       replicaCount:'2',
		       verifyReplicaCount: 'true',
		       waitTime: '300000')

stage 'deploy to production'
timeout(time: 2, unit: 'DAYS') {
    input message: 'Approve to production?'
}

openshiftTag(namespace: 'development',
	  sourceStream: 'myapp',
	  sourceTag: 'latest',
	  destinationStream: 'myapp',
	  destinationTag: 'promotePRD')

openshiftDeploy(namespace: 'production',
	     deploymentConfig: 'myapp',
	     waitTime: '300000')

openshiftScale(namespace: 'production',
	     deploymentConfig: 'myapp',
	     waitTime: '300000',
	     replicaCount: '2')

stage 'verify deploy in production'
openshiftVerifyDeployment(namespace: 'production',
		       depCfg: 'myapp',
		       replicaCount:'2',
		       verifyReplicaCount: 'true',
		       waitTime: '300000')
}
