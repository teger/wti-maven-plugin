wti-maven-plugin
================

A maven plugin for the webtranslateit.com website.


Dans votre fichier pom.xml, il faut rajouter les informations suivantes : 
    <plugin>
        <groupId>org.nt.maven.plugin</groupId>
        <artifactId>wti-maven-plugin</artifactId>
        <version>0.7.1</version>
        <configuration>
            <file.name>nom_fichier</file.name>
            <project.token>XXXXXXXXXXXXXXX</project.token>
            <file.id>xxxxx</file.id>
            <locales>fr,en</locales>
            <overwrite.src>true</overwrite.src>
        </configuration>
    </plugin>

où :

    - file.name     : le nom du fichier de traduction sans la locale et l'extension (defaut: messages)
    - project.token : La clé de l’API privée (à récupérer dans les paramètre de votre compte)
    - file.id       : L'identifiant du fichier fourni par wabtranslateit.com
    - locales       : les locales que vous souhaitez pour vos traductions (séparés par des virgules)
    - overwrite.src : Permet d'effacer les fichiers sources de traductions du projet et de les remplacer par ceux récupérer depuis webtranslateit.com (defaut : false)
    - output.path   : Le chemin où copier les fichiers une fois mis à jour (defaut : ${project.build.directory}/classes)
    - src.path      : Le chemin vers les fichiers source de traduction (defaut : ${basedir}/src/main/resources)
    

3 commandes sont disponibles pour Maven à l'aide de ce plugin : 

    - init
        Permet d'initialiser les fichiers de traduction sur le site webtranslateit.com
    
    - pull
        Permet de mettre à jour les fichiers dans le projet
    
    - push
        Permet d'envoyer les modifictions sur le site webtranslateit.com
        
        
Vous pouvez donc appeler les commandes suivantes dans maven, par exemple 

    $> mvn wti:init