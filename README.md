# Meteo
##### [IGONIN Jérémy](https://github.com/jeremyIgn)
##### [LEFAURE Adrien](https://github.com/alefaure)

## Descriptif de l'application

Cette application permet de recevoir la météo de toutes les villes de France.

## Screenshots

![](/images/1.png)
![](/images/2.png)
![](/images/3.png)
![](/images/4.png)

## Fonctionnement de l'application

Lors du premier lancement de l'application celle-ci créer la base de données pour que les villes 
renseignées par l'utilisateur puissent étre sauvegardées et réaffichées lors des futures
utilisations.

Pour ajouter une ville, il suffit simplement de cliquer l'un des deux boutons d'ajouts et de
renseigner le nom de la ville.

L'uilisateur à la possibilité de supprimer une ville en faisant glisser le nom de ville
sur la droite.

Si plusieurs villes sont enregistrées dans la base de données l'utilisateur peut naviguer entre les
pages en faisant un balayage sur la droite ou sur la gauche.

Si l'utilisateur à cliquer sur une ville il peut alors s'envoyer une notification qui lui indiquera
la dernier ville dont il a regardé la météo. Il pourra alors cliquer sur cette notification qui
ouvrira l'application sur la ville en question.

## Fonctionnalités

- Master detail
- Recycler view
- View pager
- Swipe refresh layout
- Connection internet
- Notifications (seulement lors du clic sur le bouton `Nouvelle notification`)
- Content provider
- Cursor
- Dialog fragment
- Item touch helper
- Retrofit : [Weather underground](https://www.wunderground.com)
- Picasso
- Activité et fragment
- Support smartphone et tablette
- Sdk minimun : 16 `Jelly Bean v4.1.x`
- Application disponible en `Français` et en `Englais`

## Contribution

Le projet a été réalisé par [jeremyIgn](https://github.com/jeremyIgn) et [alefaure](https://github.com/alefaure).

La répartition des taches c'est fait de façon équilibrer, 50% réaliser par chaqu'une des personnes.