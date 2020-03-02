pipeline {

  agent any

	options {
		ansiColor("xterm")
	}

  parameters {
    choice(name: 'VERBOSE', choices: [' ', "-v", "-vv", "-vvv", "-vvvv"], description: "Choice verbosity on logs"),
    choice(name: 'ENV', choices: ['HOME', "LAB"], description: "Choice your environment")
  }

  stages {
      stage ('Invoke Ansible Playbook install_wordpress.yml') {
        environment {
          ANSIBLE_FORCE_COLOR = true
        }
        steps {
          ansiblePlaybook (
            colorized: true,
            playbook: 'install_wordpress.yml',
            inventory: 'inventories/${ENV}/hosts',
            extras: '${VERBOSE}'
          )
        }
      }
    }
  }
