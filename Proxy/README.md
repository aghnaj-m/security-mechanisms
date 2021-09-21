# Java HTTP/HTTPS Proxy Server
## Le Serveur Proxy
Un serveur proxy est un serveur qui se trouve entre le client et le serveur distant dans lequel le client souhaite récupérer des fichiers. Tout le trafic qui provient du client, est envoyé au serveur proxy et le serveur proxy fait des demandes au serveur distant au nom du client. Une fois que le serveur proxy reçoit les fichiers requis, il les transmet au client. Cela peut être bénéfique car il permet à l’administrateur du serveur proxy de contrôler ce que les machines sur son réseau peuvent faire. Par exemple, certains sites Web peuvent être bloqués par le serveur proxy, ce qui signifie que les clients ne pourront pas y accéder. Il est également bénéfique que les pages fréquemment visitées peuvent être mis en cache par le serveur proxy. Cela signifie que lorsque le client (ou d’autres clients) font des demandes ultérieures pour des fichiers qui ont été mis en cache, le Proxy peut leur remettre les fichiers immédiatement, sans avoir à les demander depuis le serveur distant ce qui peut être beaucoup plus rapide si le proxy et les clients sont sur le même réseau. Bien que ces fichiers soient connus pour être contenus dans le cache du proxy, il est intéressant de noter que les clients n’ont aucune connaissance de cela et peuvent maintenir leurs propres caches locaux. L’avantage du cache proxy est que lorsque plusieurs clients utilisent le proxy, les pages mises en cache dues à un client peuvent être consultées par un autre client.


## Implementation
Le proxy a été implémenté en utilisant Java et a fait une large utilisation des sockets TCP. Firefox a été mis en place pour émettre tout son trafic vers le port spécifié et l’adresse IP qui ont ensuite été utilisés dans la configuration proxy. Il y a deux composantes principales à l’implémentation - la classe Proxy et la classe Requesthandler.
## La classe Proxy
La classe Proxy est responsable de la création d’un Serversocket qui peut accepter les connexions socket entrantes du client. Cependant, il est essentiel que l’implémentation soit multithreadée car le serveur doit pouvoir servir plusieurs clients simultanément. Ainsi, une fois qu’une connexion socket arrive, elle est acceptée et le Proxy crée un nouveau thread qui dessert la requête (voir la classe Requesthandler). Comme le serveur n’a pas besoin d’attendre que la requête soit entièrement réparée avant d’accepter une nouvelle connexion de socket, plusieurs clients peuvent avoir leurs demandes traitées de manière asynchrone. 

La classe Proxy est également responsable de l’implémentation de la fonctionnalité de cache et de blocage. Autrement dit, le proxy est capable de mettre en cache les sites qui sont demandés par les clients et de bloquer dynamiquement les clients de visiter certains sites Web. Comme la vitesse est de la plus haute importance pour le serveur proxy, il est souhaitable de stocker les références aux sites actuellement bloqués et les sites qui sont contenus dans le cache dans une structure de données avec un temps de recherche d’ordre constant attendu. Pour cette raison, un Hashmap a été choisi. Il en résulte un cache extrêmement rapide et des temps de recherche de site bloqués. Cela n’entraîne qu’un léger surcoût si le fichier n’est pas contenu dans le cache, et une augmentation des performances si le fichier est contenu dans le cache.
Enfin, la classe proxy est également responsable de fournir un système de gestion dynamique de console, Cela permet à un administrateur d’ajouter/supprimer des fichiers vers et depuis le cache et les sites Web vers et depuis la liste noire, en temps réel. 

## Classe RequestHandler 
La classe Requesthandler est chargée de traiter les demandes qui parviennent au proxy. Le demandeur examine la demande reçue et la traite de façon appropriée. Les requêtes peuvent être subdivisées en trois catégories principales - requêtes HTTP GET, requêtes HTTP GET pour le fichier contenu dans le cache et requêtes HTTPS CONNECT.
### Requête HTTP
Il s’agit des demandes standard faites lorsqu’un client tente de charger une page Web. L’entretien de ces demandes est une tâche simple :
-Analysez l’URL associée à la requête.
-Créez une connexion HTTP à cette URL.
-Faire écho à la requête GET du client vers le serveur distant.
-Faire écho à la réponse du serveur au client.
-Enregistrez une copie locale du fichier dans le cache du proxy.
### Requête HTTP pour les fichier en cache
Comme avant, ce sont les requêtes typiques faites par les clients, seulement dans ce cas, le fichier est contenu dans le cache du proxy.
-Analysez l’URL associée à la requête
-Hachez l’URL et utilisez-la comme clé pour la structure de données Hashmap.
-Ouvrir le fichier résultant pour lecture.
-Renvoyer le contenu du fichier au client.
-Fermer le dossier.
### Requête HTTPS -  Requête CONNECT 
Les connexions HTTPS utilisent des sockets sécurisés (SSL). Les données transférées entre le client et le serveur sont cryptées. Ce système est largement utilisé dans le secteur financier pour garantir la sécurité des transactions, mais il est de plus en plus répandu sur Internet.
Cependant, à première vue, cela pose un problème pour les serveurs proxy : Comment le proxy sait-il quoi faire avec ces données cryptées provenant du client?
Afin de résoudre ce problème, au départ, un autre type de requête HTTP est fait par le client, une requête CONNECT. Cette requête est HTTP standard et est donc non cryptée et contient l’adresse avec laquelle le client souhaite créer une connexion HTTPS et peut être extraite par le proxy. Il s’agit d’un processus connu sous le nom de tunnel HTTP Connect et fonctionne comme suit :
-Le client émet une demande CONNECT
-Proxy extrait l’URL de destination.
-Proxy crée une connexion socket standard vers le serveur distant spécifié par l’URL.
-En cas de succès, le proxy envoie une réponse « 200 Connection Established » au client, indiquant que le client peut maintenant commencer à transmettre les données chiffrées au proxy.
-Le proxy transfère alors simultanément toutes les données envoyées par le client au serveur distant et toutes les données reçues par le serveur distant au client.

Toutes ces données seront cryptées et donc le proxy ne peut pas mettre en cache ni même interpréter les données.
