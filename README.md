# TP 15 — Conteneurisation avec Docker

### 1. Construire l'image

```bash
docker build -t usmba/conteneur-mediatheque:1.0 .
# ou
make image
```

### 2. Lancer la pile complète

```bash
docker compose up -d
docker compose ps
# ou
make demarrer
```

| Service | Conteneur | Port hôte | Rôle |
|---|---|---|---|
| `base-mediatheque` | `mediatheque-mysql` | **3307** → 3306 | MySQL 8.0, base `mediatheque_db` |
| `application-mediatheque` | `mediatheque-app` | **8111** → 8111 | l'API Spring Boot |
| `console-mediatheque` | `mediatheque-pma` | **8112** → 80 | phpMyAdmin |

L'application démarre **après** que MySQL soit `healthy` (`depends_on: condition: service_healthy`)
et son URL JDBC pointe vers `base-mediatheque:3306`, c'est-à-dire le **nom du service** dans
le réseau Docker — pas `localhost`.

### 3. Vérifier

```bash
curl http://localhost:8111/api/catalogue/synthese
curl http://localhost:8111/actuator/health
```

### 4. Arrêter

```bash
docker compose down      # arrête et supprime les conteneurs, GARDE le volume
docker compose down -v   # arrête et supprime aussi le volume MySQL
```

## Résultats obtenus

### 1. Tableau de bord servi depuis le conteneur

![Tableau de bord dans le conteneur](docs/screenshots/01-application-conteneur.png)

L'application tourne dans le conteneur `mediatheque-app` et répond sur le port **8111**. Le
tableau de bord affiche les trois compteurs calculés par `/api/catalogue/synthese` — 5 ouvrages,
3 disponibles, 2 en informatique — ainsi que la liste des cinq ouvrages avec le badge de
disponibilité et la catégorie.

### 2. API REST du catalogue

![API ouvrages](docs/screenshots/02-api-ouvrages.png)

`GET /api/catalogue/ouvrages` renvoie le JSON complet des cinq ouvrages, tels qu'ils sont lus
dans MySQL. C'est cette réponse que le tableau de bord consomme en JavaScript.

### 3. Sonde de santé et preuve d'exécution en conteneur

![Health et base MySQL](docs/screenshots/03-health-db.png)

`/actuator/health` expose `"status":"UP"`, la base `"database":"MySQL"` avec sa version, et
surtout `"path":"/app/."` : le répertoire de travail est celui du conteneur, ce qui prouve
que l'application n'est pas lancée depuis la machine hôte.

### 4. Construction de l'image puis lancement manuel des deux conteneurs

![Images, docker run et journaux](docs/screenshots/04-docker-images-ps-logs.png)

Fenêtre de console réelle, dans l'ordre : `docker build` de l'image
`usmba/conteneur-mediatheque:1.0`, `docker run` du conteneur MySQL sur le port 3307,
`docker run` du conteneur applicatif sur le port 8111, puis `docker ps` qui montre les deux
conteneurs `Up` — l'applicatif avec l'état `(healthy)` remonté par le `HEALTHCHECK`.
Les journaux filtrés (`| findstr`) affichent les lignes `HikariPool-1 - Start completed`,
`Tomcat started on port 8111` et `Started MediathequeApplication in 12.045 seconds`, suivies
de la réponse JSON de `/api/catalogue/synthese`. La version texte de ces commandes est dans
[`docs/captures/02-docker-run-essai.txt`](docs/captures/02-docker-run-essai.txt), le log de
construction complet dans [`docs/captures/01-docker-build.txt`](docs/captures/01-docker-build.txt).

### 5. Cycle Docker Compose : `down -v` puis `up -d`

![Cycle docker compose](docs/screenshots/05-docker-compose-up.png)

Le volume est supprimé (`docker compose down -v`), puis `docker compose up -d` recrée le
réseau `tp15_conteneur_mediatheque_default`, les trois conteneurs et le volume nommé.
`docker compose ps` montre les trois services ; la colonne `STATUS` passe à `(healthy)` pour
MySQL et pour l'application après la période de démarrage. Le `curl` final renvoie les mêmes
compteurs, ce qui signifie que la base a été initialisée automatiquement par l'amorçage.

### 6. Journaux et santé de la pile

![Journaux docker compose](docs/screenshots/06-docker-compose-logs.png)

`docker compose logs` montre la séquence complète : MySQL qui termine son initialisation
(`ready for connections`), puis l'application qui se connecte sur `base-mediatheque:3306`,
démarre Tomcat sur 8111 et se déclare `Started`. La commande `docker compose ps` en fin de
capture confirme les deux états `(healthy)`. L'état détaillé (services déclarés, réseau,
volume, images utilisées) est conservé dans
[`docs/captures/03-docker-compose-etat.txt`](docs/captures/03-docker-compose-etat.txt).

### 7. Persistance des données grâce au volume nommé

![Persistance du volume](docs/screenshots/07-persistance-volume.png)

La démonstration procède en quatre temps. D'abord un `SELECT COUNT(*)` dans MySQL renvoie **5**
ouvrages et `docker volume inspect` affiche `volume-mediatheque` avec son point de montage
`/var/lib/mysql`. Ensuite `docker compose down` : les conteneurs et le réseau disparaissent,
mais `docker volume ls` montre que `tp15_conteneur_mediatheque_volume-mediatheque` est
**toujours présent**. Puis `docker compose up -d` relance la pile, et un second `SELECT` sur
`id`, `titre`, `categorie`, `disponible` renvoie exactement les mêmes cinq lignes, sans
doublon. Le `curl` final sur `/api/catalogue/synthese` retourne toujours `totalOuvrages: 5`.
C'est l'illustration qu'un conteneur est éphémère mais qu'un **volume nommé** conserve les
données.

### 8. Contrôle direct de la table dans phpMyAdmin

![phpMyAdmin sur la table ouvrage](docs/screenshots/08-phpmyadmin-ouvrage.png)

phpMyAdmin est ajouté à la pile comme troisième service (`console-mediatheque`, port 8112) et
joint MySQL par le nom de service `base-mediatheque:3306`. La capture montre la table
`mediatheque_db.ouvrage` avec ses 5 enregistrements et la requête `SELECT * FROM ouvrage`
exécutée depuis l'interface : les données sont bien dans la base du conteneur, pas en mémoire.

