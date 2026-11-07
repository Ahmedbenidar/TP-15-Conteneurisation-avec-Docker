.PHONY: aide image demarrer arreter logs etat nettoyer

AIDE = @echo "Cibles disponibles :"; \
       echo "  make image     - construit l'image usmba/conteneur-mediatheque:1.0"; \
       echo "  make demarrer  - lance la pile docker compose"; \
       echo "  make etat      - affiche les conteneurs et leur sante"; \
       echo "  make logs      - suit les journaux de l'application"; \
       echo "  make arreter   - arrete la pile sans supprimer le volume"; \
       echo "  make nettoyer  - arrete la pile et supprime le volume MySQL"

aide:
	$(AIDE)

image:
	docker build -t usmba/conteneur-mediatheque:1.0 .

demarrer:
	docker compose up -d
	docker compose ps

etat:
	docker compose ps
	docker volume ls | grep mediatheque || true

logs:
	docker compose logs -f application-mediatheque

arreter:
	docker compose down

nettoyer:
	docker compose down -v
