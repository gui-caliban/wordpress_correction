#  CHANGELOG

_Comme vu ensemble, je vais me servir des tags git pour séparer les différentes étapes du tutoriel._

## 01.00.00 Ping de ou des machines cibles
* Un fichier inventories/hosts avec comme user de deploiement "devops"
  * Ce compte doit pouvoir passer root sans mot de passe sur la machine cible)
  * Un échange de clés ssh doit être fait entre le user "jenkins" du serveur jenkins et le compte devops de la machine cible
* Un premier playbook "ping.yml" faisant appel au module ping

## 01.01.00  Installation des dépendances php, mysql et nginx
* Installation de php
  * Utilisation du module "package" capable de faire appel aux commandes yum sur centOS et apt sur Ubuntu par exemple.
  * Notion de boucle avec le mot clé "loop"
  * Suppression de apache2, installé automatiquement avec PHP sur Ubuntu (exemple du state aabsent pour le mùodule package)
* Installation de MySQL
  * Installation des packages necessaire (python-mysqldb permet d'utiliser les modules mysql_user, mysql_db, etc.)
  * "Ansibilisation" de mysql_secure_installtion:
    * Génération d'un nouveau mot de passe route pour mysql
      * Idempotence : Mot clé "creates", cette action n'est effectuée que si le fichier /root/.my.cnf n'est pas présent sur le serveur.
      * Création d'un variable: Mot clé "register", la valeur de la vartiable "mysql_new_root_pass" correspond au retour de la commande.
    * Suppression des users ananyms avec le module mysql_user
    * Suppression de la base de test
    * Affichage du nouveau mot de passe:
      * Module "debug": permet d'afficher des chaînes de caractère avec appel des valeurs des variables
      * La variable "mysql_new_root_pass" est un objet, "mysql_new_root_pass.stdout" correspond l'affichage du retour de la commande.
    * Update du mot de passe root de mysql avec ce nouveau mot de passe.
    * Dépôt du fichier /root/.my.cnf:
      * Permet une connexion sans mdp à la base
      * Notion de template : les fichier mmy.cnf.j2 est un template jinja, les variables appelés dans ce fichier sont valorisées lors de leur dépôt sur le serveur.
    * Dépôt du fichier mysqld.cnf
      * Comment de la ligne bind-adresse pour permettre à ansible de se connecté à la base
      * Notion de handler: la Tâche "restart mysql" n'est effectuée que si le fichier de conf mysqld.cnf est modifié.
  * Installation de nginx
