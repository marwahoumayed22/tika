# IFT3913 – Tâche 3 : Semgrep et GitHub Actions

**Binôme :** Fatima Zahrae El Fakir et Marwa Houmayed  
**Dépôt :** [marwahoumayed22/tika](https://github.com/marwahoumayed22/tika)

## 1. Intégration de Semgrep

On a ajouté le workflow [`semgrep.yml`](../.github/workflows/semgrep.yml) pour détecter les nouvelles vulnérabilités lors des push et pull requests vers `main`.

On utilise `--baseline-commit` pour comparer le code à une version de référence et `--error` pour faire échouer le workflow lorsqu'une nouvelle alerte apparaît.

On a choisi cette méthode parce que Tika contenait déjà 28 alertes. Le but était donc de bloquer uniquement les nouvelles vulnérabilités.

Pour éviter des résultats différents entre les exécutions, on a fixé Semgrep à la version `1.185.0` et les règles Java à un commit précis. On utilise aussi `fetch-depth: 0` pour récupérer l'historique Git nécessaire à la comparaison.

L'analyse concerne les cinq modules du cas d'étude, en excluant les fichiers de tests. Les résultats sont enregistrés dans un rapport JSON et dans le résumé GitHub Actions.

## 2. Validation du workflow

On a vérifié trois situations :

- **Code initial :** aucune nouvelle alerte, le workflow réussit.
- **Ajout des sept vulnérabilités :** onze nouvelles alertes, le workflow échoue.
- **Modification sans nouvelle vulnérabilité :** aucune nouvelle alerte, le workflow réussit.

Ces tests montrent que Semgrep ne bloque pas les alertes déjà présentes.

**Preuves :** [Workflow réussi](https://github.com/marwahoumayed22/tika/actions/runs/43771129386) · [PR #3](https://github.com/marwahoumayed22/tika/pull/3) · [Échec Semgrep attendu](https://github.com/marwahoumayed22/tika/actions/runs/43771990755)

## 3. Les sept vulnérabilités

Les modifications se trouvent dans la branche [`tache3-vulnerabilites`](https://github.com/marwahoumayed22/tika/tree/tache3-vulnerabilites).

**1. TLS non vérifié — `NetworkParser.java` (CWE-295)**

Ce fichier gère les connexions réseau. On a ajouté un gestionnaire qui accepte les certificats sans les vérifier, ce qui peut permettre l'interception des données HTTPS.

Semgrep détecte quatre alertes : `insecure-trust-manager` et `weak-ssl-context`.

**2. Injection XXE — `XMLReaderUtils.java` (CWE-611)**

On a ajouté un parseur XML sans désactiver les entités externes. Un fichier malveillant pourrait alors accéder à des ressources locales.

Semgrep détecte une alerte `documentbuilderfactory-disallow-doctype-decl-missing`.

**3. Aléa prévisible — `TemporaryResources.java` (CWE-330)**

On a utilisé `java.util.Random` pour générer des noms de fichiers temporaires. Ces noms peuvent être prévisibles et faciliter certaines attaques.

Semgrep détecte une alerte `weak-random`.

**4. Désérialisation non sécurisée — `JsonMetadata.java` (CWE-502)**

On a ajouté `ObjectInputStream.readObject()` sans filtrer les classes autorisées. Des données non fiables pourraient provoquer un comportement dangereux.

Semgrep détecte une alerte `object-deserialization`.

**5. Injection de commande — `TesseractOCRParser.java` (CWE-78)**

On a construit une commande shell en utilisant directement une valeur de langue OCR. Une entrée malveillante pourrait modifier la commande exécutée.

Semgrep détecte une alerte `command-injection-formatted-runtime-call`.

**6. Chiffrement faible — `PDFParser.java` (CWE-326/327)**

On a utilisé DES en mode ECB avec une clé codée en dur. DES est trop faible et ECB peut révéler des motifs dans les données.

Semgrep détecte deux alertes : `des-is-deprecated` et `ecb-cipher`.

**7. Hachage MD5 — `OfficeParser.java` (CWE-328)**

On a utilisé MD5 pour calculer une empreinte de mot de passe. Cet algorithme est trop rapide et n'est pas adapté à la protection des mots de passe.

Semgrep détecte une alerte `use-of-md5`.

## 4. Comparaison Semgrep

Avant les modifications, Semgrep détectait **28 alertes dans 19 fichiers**. Après l'ajout des vulnérabilités, il en détecte **39 dans 23 fichiers**.

On obtient donc onze nouvelles alertes pour sept vulnérabilités. Certaines modifications déclenchent plusieurs règles, ce qui explique cette différence.

Les alertes initiales restent présentes, mais ne bloquent pas le workflow grâce au mode différentiel.

Les rapports avant, après et différentiels sont disponibles dans [`tache3/rapports`](rapports/).

## 5. Rickroll

On a créé une [action Rickroll réutilisable](../.github/actions/rickroll/action.yml) en Bash, basée sur l'idée de `random-rickroll`.

Elle affiche un GIF et un lien Rickroll dans GitHub Actions lorsqu'une étape de tests échoue.

On l'a d'abord testée sur Tika Core avec un test volontairement échoué sur la [PR #2](https://github.com/marwahoumayed22/tika/pull/2). L'action s'est déclenchée correctement.

### Validation sur GraphHopper

On a ensuite intégré cette action dans le workflow de tests de GraphHopper.

Sur la branche `test-rickroll`, on a ajouté `RickrollValidationTest`, un test JUnit qui échoue volontairement, afin de vérifier le comportement du workflow.


- [Workflow GraphHopper modifié](https://github.com/fatimazahrae5/graphhopper/blob/tache3-rickroll/.github/workflows/build.yml)
- [Action Rickroll](https://github.com/fatimazahrae5/graphhopper/blob/tache3-rickroll/.github/actions/rickroll/action.yml)
- [Validation sur GitHub Actions](https://github.com/fatimazahrae5/graphhopper/actions/workflows/build.yml?query=branch%3Atest-rickroll)

La branche de validation reste séparée de la branche principale pour ne pas conserver un test volontairement échoué dans le projet.

## 6. Conclusion

Semgrep détecte les nouvelles vulnérabilités et fait échouer le workflow sans bloquer les alertes déjà présentes. Les sept vulnérabilités introduites ont produit onze nouvelles alertes.

On a également vérifié le déclenchement de notre action Rickroll sur Tika et GraphHopper.