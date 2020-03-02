pipeline {

  agent any

	options {
		ansiColor("xterm")
	}

  parameters {
    choice(name: 'VERBOSE', choices: [' ', "-v", "-vv", "-vvv", "-vvvv"], description: "Choice verbosity on logs")
    choice(name: 'ENV', choices: ['HOME', "LAB"], description: "Choice your environment")
  }

  stages {
      stage ('Vérification de la qualité du code ansible') {
        steps {
          sh ' echo "Tests ansible-lint" '
        }
      }
      stage ('Installation des dependances') {
        environment {
          ANSIBLE_FORCE_COLOR = true
        }
        steps {
          ansiblePlaybook (
            colorized: true,
            playbook: 'install_dependances.yml',
            inventory: 'inventories/${ENV}/hosts',
            extras: '${VERBOSE}'
          )
        }
      }
      stage ('Installation de wordpress') {
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
        stage ('Tests fonctionels') {
          steps {
            sh ' echo "Tests Selenium" '
          }
        }
      }
    }
  }
