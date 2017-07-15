#<p align="center">TowerWarspp</p>

#<p align="center">APP-Projekt 2017 - Gruppe "Age of Towers"</p>  

#Gruppenmitlgieder
- Alexander Wähling
- Anastasiia Kysliak
- Kai Kulhmann
- Niklas Müller
- Robin Hundt (Gruppenleiter)

##Tutor 
Dominick Leppich  


##Anleitung
Das Spiel kann folgendermaßen kompiliert und gestartet werden:

1. Starten eines Terminals `strg + alt t` (standarmäßig unter Unity)
2. Wechseln in das Verzeichnis, welches das tar Archiv enthält. Nach dem Download gewöhnlich unter   
`/home/user/Downloads` zu finden.
3. Entpacken des tar Archivs:  
`tar xvf AgeOfTowers.tar`
4. Wechseln in das neue `AgeOfTowers` Verzeichnis
5. Automatisches Übersetzen des Quelltextes, erzeugen der JavaDoc Dokumentation und erzeugung der `.jar` Datei durch Aufruf des `ant` Programms
6. Starten des Programms ist in zwei Modi möglich, über Kommandozeilenparamter oder einen interaktiven Modus:

####Kommandozeilenparameter
- Einstellungen beginnen mit einem `-` und Schalter mit `--`
- `--help` zeigt die Hilfe des Spiels an mit kurzen Eklärungen zu allen möglichen Parametern
####Nichtoptionale Einstellungen für lokale Spiele:
- Bei einem lokalen Spiel müssen die folgenden Einstellungen gesetzt werden:
- Der Spielertyp des roten und blauen Spielers (zu Spielertypen mehr unter **Spielertypen**)
    - `-red` {human, random, simple, adv1, adv2, remote}
    - `-blue` {human, random, simple, adv1, adv2, remote}
- `-size {4,..,26}` die Größe des Spielfeldes (mögliche Größe zwischen 4 und 26)

Ein Beispielaufruf des Spieles mit einem menschlichen Spieler als **RED** und einer zufälligen KI als **BLUE** auf einem Feld der Größe 8 könnte so aussehen:  

```java -jar AgeOfTowers.jar -red human -blue random -size 8```  

**Anmerkung**: Das Spiel startet Standardmäßig mit grafischer Ausgabe. Wenn Textausgabe gewünscht ist, kann die `-output` Einstellung zu `text` gesetzt werden. Möchte man das Spiel alsoLost my commit message. To tired to type again- mit textueller Ausgabe starten, würde der Aufruf wie folgt aussehen:  

```java -jar AgeOfTowers.jar -red human -blue random -size 8 -output text```  


####Optionale Parameter:
- `-output` zum setzen der gewünschten Ausgabeart. Kann zu `none` für keine Ausgabe des Spielstandes, `text` für textuelle und `graphic` für graphische Ausgabe gesetzt werden. Standardmäßig startet das Spiel im `graphic` Modus.
- `-delay <zeit in ms>`  kann gesetzt werden um eine Verzögerung zwischen Zügen zu erzwingen. Besonders sinnvoll wenn zwei schnnelle Computergegner miteinander spielen und man ihr Verhalten nachvollziehen möchte.
- `-timeout` kann gesetzt werden um Spiele nach einer gewissen Anzahl an Zügen abzubrechen
    - `-timeout 200` würde dagür sorgen, dass das Spiel nach 200 **gesamten** Zügen abgebrochen wird
- `-games <Zahl größer 1>` startet den Turniermodus mit der angegebenen Anzahl an Spielen
    - während dem Turniermodus tauscht die Rolle der Spieler
    - am Ende des Turniermodus werden die Ergebnisse der einzelnen Spiele den Spielern wieder in ihrer          Startposition zugeordnet
    - ist die Einstellung `-timeout` gesetz, gilt diese pro im Tournier gespielten Spiel
    - am Ende des Tourniers erhält man eine Statistik über die Anzahl der gespielten Spiele, die Anzahl der gewonnen Spiele pro Spieler, wie oft sie auf welche Art gewonnen haben und wie viele Züge sie im Schnitt benötigt haben

####Netzwerkspiel
Es gibt zwei Arten von Netzwerkspiel:

####1.Spieler anbieten:
- möchte man einen Spieler im Netzwerk anbieten muss die Einstellung `-offer <Player type>` und `-name <Name des Netzwerkspielers>` gesetzt sein
-   - möchte man einen Netzwerkspieler auf einem bestimmten Port anbieten, so kann dies über die Einstellung `-port <Port>`  erreicht werden
- beim anbieten eines Spielers im Netzwerk ist Standardmäßig die graphische Ausgabe aktiviert. Dies kann ebenfall über die `-output {graphic, text, none}`