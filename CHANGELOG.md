#  CHANGELOG

_Comme vu ensemble, je vais me servir des tags git pour séparer les différentes étapes du tutoriel._

## 01.00.00 Ping de ou des machines cibles
* Un fichier inventories/hosts avec comme user de deploiement "devops"
  * Ce compte doit pouvoir passer root sans mot de passe sur la machine cible)
  * Un échange de clés ssh doit être fait entre le user "jenkins" du serveur jenkins et le compte devops de la machine cible
* Un premier playbook "ping.yml" faisant appel au module ping 
