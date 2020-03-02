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

## 01.02.00  Configuration de nginx
* Récupération de la version de PHP via register et travail sur la variable fpm.sock à l'aide de la fonction split.
* Dépôt du fichier de conf nginx avec configuration du fpm-sock.

## 01.03.00  Installation de WordPress
* Ajout du fichier source de wordpres dans le répertoire Files
* Désarchivage du package sur le serveur attention unzip est nécessaire pour pourvoir utiliser le module unarchive.
* Copie des statiques dans le répertoire du serveur web
* Création de la base de donnée de wordpress à l'aide des modules mysql_db et mysql_user.
* Une première installation manuelle nous permet de généré le fichier wp-config.php et de peupler la base de donnée.
* Automatisation de cette fin d'installation manuelle:
  * Transformation de wp-config.php en template
  * Export de la base à l'aide de la comande "mysqldump wordpress > /tmp/wp-database.sql
  * Transformation du fichier wp-database.sql en template et variabilisation de l'IP du serveur dans ce template à l'aide des facts ansible.
  * Récréation de l'idempotence, l'import de la base ne se fait que si elle n'existe pas.

## 01.04.00  Réorganisation du projet à l'aide de rôles
* Création d'un répertoire roles à la racine.
  * Utilisation de la commande ansible-galaxy sur un serveur où ansible est installé:
    * ansible-galaxy init wp.php
    * ansible-galaxy init wp.mysql
    * ansible-galaxy init wp.nginx
    * ansible-galaxy init wp.wordpress
* L'appel des modules correspondant au role doit être dans roles/<nom_du_role>/tasks/main.yml
* Les templates nécessaires au rôle sont dans roles/<nom_du_role>/templates/
  * A l'appel du module template, le paramètre src doit juste reprendre le nom du template
* Les fichiers nécessaires au rôle sont dans roles/<nom_du_role>/files/
  * A l'appel du module copy, le paramètre src doit juste reprendre le nom du fichier
* Les handlers necéssaires au role sont situé dans le répertoire roles/<nom_du_role>/handlers/main.yml
* L'appel des roles dans le playbook se fait grâce au mot-clé "roles:" (voir le playbook install_wordpress.yml)

## 01.05.00  Variabilisation
* Création de deux environnements LAB et HOM à l'aide des répertoires /inventories/<ENV>/
* Pour chaque fichier hosts, création des groupes de serveurs web, data et wordpress (qui comprend les serveurs appartenant au groupe data et web)
* Création de l'arborescence de repertoires "group_vars" pour classer les variables en fonction de la portée voulue
  * Répertoire "group_vars" à la racine de l'arborescence pour les variables communes aux deux environnements
  * Répertoire "group_vars" sous le répertoire "inventories/<ENV>" pour les varibles spécifiques à un environnement.
* Exemple de porté de variable (voir slide dédié dans la présentation pour plus de détails):
  * "database_user" :
    * Le nom de l'user de la base de donnee est commun à tous les environnements
    * Sa valeur doit être connue pour le role wp.wordpress
    * La variable est donc déclarée dans "group_vars/wordpress/wordpress.yml"
  * "database_name"
    * Le nom l'instance de la base de donnée est spécifique de l'environnement.
    * Sa valeur doit être connue pour le role wp.wordpress
    * La variable est donc déclarée dans "inventories/<ENV>/group_vars/wordpress/wordpress.yml"
