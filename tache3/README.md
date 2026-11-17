# IFT3913 – Tâche 3 : Semgrep et GitHub Actions

**Binôme :** Fatima Zahrae El Fakir et Marwa Houmayed  
**Dépôt :** [marwahoumayed22/tika](https://github.com/marwahoumayed22/tika)

## 1. Intégration de Semgrep

On a ajouté le workflow [`semgrep.yml`](../.github/workflows/semgrep.yml) pour détecter les nouvelles vulnérabilités lors des push et pull requests vers `main`.

On utilise `--baseline-commit` pour comparer les modifications avec le code précédent et `--error` pour faire échouer le workflow lorsqu'une nouvelle alerte est détectée.

On a choisi cette méthode parce que Tika avait déjà 28 alertes. On voulait donc bloquer uniquement les nouvelles vulnérabilités. On a aussi utilisé `fetch-depth: 0` pour accéder aux commits précédents, fixé Semgrep à la version `1.185.0` et les règles Java à un commit précis pour éviter que les résultats changent entre les exécutions.

L'analyse concerne les cinq modules du cas d'étude, sans les fichiers de tests. Un rapport JSON est généré même lorsque Semgrep échoue.

## 2. Validation

On a testé trois situations :

- Code initial : aucune nouvelle alerte, workflow réussi.
- Ajout des sept vulnérabilités : onze nouvelles alertes, workflow en échec.
- Ajout d'un commentaire sans nouvelle vulnérabilité : workflow réussi.

On a également vérifié ces résultats sur GitHub Actions.

**Preuves :** [Exécution réussie](https://github.com/marwahoumayed22/tika/actions/runs/43771129386) · [PR #3](https://github.com/marwahoumayed22/tika/pull/3) · [Échec Semgrep attendu](https://github.com/marwahoumayed22/tika/actions/runs/43771990755)

## 3. Les sept vulnérabilités

On a choisi des fichiers où les vulnérabilités correspondent à leur utilisation dans Tika.

**1. TLS non vérifié — `NetworkParser.java` (CWE-295)**

Ce fichier gère les connexions réseau. On a ajouté un gestionnaire qui accepte tous les certificats, ce qui peut permettre l'interception des données HTTPS. Semgrep détecte quatre alertes : `insecure-trust-manager` et `weak-ssl-context`.

**2. Injection XXE — `XMLReaderUtils.java` (CWE-611)**

On a ajouté un parseur XML sans désactiver les entités externes. Un fichier XML malveillant pourrait alors accéder à des ressources locales. Semgrep détecte une alerte `documentbuilderfactory-disallow-doctype-decl-missing`.

**3. Aléa prévisible — `TemporaryResources.java` (CWE-330)**

On a utilisé `java.util.Random` pour générer des noms de fichiers temporaires. Ces valeurs peuvent être prévisibles, contrairement à une génération sécurisée. Semgrep détecte une alerte `weak-random`.

**4. Désérialisation non sécurisée — `JsonMetadata.java` (CWE-502)**

On a ajouté `ObjectInputStream.readObject()` sans filtrer les classes autorisées. Des données malveillantes pourraient provoquer un comportement dangereux. Semgrep détecte une alerte `object-deserialization`.

**5. Injection de commande — `TesseractOCRParser.java` (CWE-78)**

On a construit une commande shell en utilisant directement la langue OCR. Une valeur malveillante pourrait modifier la commande exécutée. Semgrep détecte une alerte `command-injection-formatted-runtime-call`.

**6. Chiffrement faible — `PDFParser.java` (CWE-326/327)**

On a utilisé DES en mode ECB avec une clé écrite dans le code. DES est trop faible et ECB peut révéler des motifs dans les données. Semgrep détecte deux alertes : `des-is-deprecated` et `ecb-cipher`.

**7. Hachage MD5 — `OfficeParser.java` (CWE-328)**

On a utilisé MD5 pour calculer une empreinte de mot de passe. Cet algorithme est trop rapide et n'est pas adapté à la protection des mots de passe. Semgrep détecte une alerte `use-of-md5`.

Les modifications se trouvent dans la branche [`tache3-vulnerabilites`](https://github.com/marwahoumayed22/tika/tree/tache3-vulnerabilites).

## 4. Comparaison des rapports

Avant nos modifications, Semgrep détectait **28 alertes dans 19 fichiers**. Après, il détecte **39 alertes dans 23 fichiers**.

On a donc onze nouvelles alertes pour sept vulnérabilités. La différence vient du fait que certaines vulnérabilités déclenchent plusieurs règles.

Les 28 alertes initiales restent présentes, mais ne bloquent pas le workflow grâce au mode différentiel.

Les rapports avant, après et différentiels sont disponibles dans [`tache3/rapports`](rapports/).

## 5. Rickroll

On a créé une [action Rickroll réutilisable](../.github/actions/rickroll/action.yml) en Bash, à partir de l'idée de `random-rickroll`.

Elle affiche un GIF et un lien Rickroll dans le résumé GitHub Actions lorsqu'un test échoue.

Pour vérifier son fonctionnement, on a fait échouer volontairement un test sur la [PR #2](https://github.com/marwahoumayed22/tika/pull/2). L'action s'est déclenchée comme prévu.

**Limite :** notre workflow déclenche actuellement le Rickroll sur un échec de Tika Core, et non de GraphHopper. Cette adaptation reste nécessaire pour respecter exactement la consigne.

### Validation sur GraphHopper

On a aussi intégré notre action Rickroll dans le workflow de tests de GraphHopper.

Pour vérifier son fonctionnement, on a créé une branche de test avec un échec volontaire (`exit 1`). Les deux jobs ont échoué et l'action Rickroll s'est bien exécutée.

- [Workflow GraphHopper modifié](https://github.com/fatimazahrae5/graphhopper/blob/tache3-rickroll/.github/workflows/build.yml)
- [Action Rickroll](https://github.com/fatimazahrae5/graphhopper/blob/tache3-rickroll/.github/actions/rickroll/action.yml)
- [Test du Rickroll](https://github.com/fatimazahrae5/graphhopper/actions/workflows/build.yml?query=branch%3Atest-rickroll)

Ce test valide le déclenchement du Rickroll après un échec du build. Il ne s'agit pas d'un véritable test unitaire GraphHopper échoué.

## 6. Conclusion

Semgrep détecte correctement les nouvelles vulnérabilités et fait échouer le workflow sans bloquer les alertes déjà présentes. Les sept vulnérabilités ont produit onze nouvelles alertes, confirmées par les rapports et les exécutions GitHub Actions.
