CECI EST UNE IMPLEMENTATION DE HADOOP en JAVA en Utilisant Java 

l'Implementation de HDFS est complète et fonctionne grace à TCP/IP sur Socket
Afin de tester cette implementation de HDFS vous devez:
  - Connecter votre PC à un switch ou un reseau WIFI
  - Configurez le chemin vers le dossier de données DATA_FOLDER et RECONSTRUCTED_FOLDER pour les fichiers resultats reconstitués dans la classe HdfsClient
  - Configurez le chemin d'accès vers le dossier FRAGMENT_FOLDER dans la classe HdfsServer  
  - Sur ce reseau WIFI ne laissez connecté que les PC qui participent au Cluster de HDFS

Sur chaque PC executez le service HDFS Server 
Sur le PC Client qui veut utiliser HDFS adaptez la methode main à l'operation que vous voulez ecrire HdfsRead ou HdfsWrite ou HdfsDelete
