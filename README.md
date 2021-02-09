# Simon Beat
Progetto per il corso di Programmazione per la Musica A.A 20/21 <br>
**Studente**: `Sebastiano Caccaro` <br>
**Mail**: `sebastiano.caccaro@studenti.unimi.it`

## Descrizione
Il progetto emula il gioco simon. Lo scopo del giocatore è di riprodurre la sequenza di suoni generata dal gioco. La lunghezza di tale sequenze aumenta di un'unità ogni volta che il giocatore la riproduce correttamente.

### Caratteristiche
Il progetto presenta delle variazioni rispetto al gioco originale:
- Il gioco utilizza timbri di batteria
- Il gioco tiene conto della precisione temporale del giocatore: più il giocatore è preciso, più alto è il punteggio assegnato.
- L'utente può personalizzare le seguenti caratteristiche:
  - Banco di timbri utilizzato
  - Timbri utilizzati all'interno di un banco di suoni
  - Velocità di gioco BPM
- L'utente può inoltre aggiungere uno o più banchi di suoni a suo piacimento. E' sufficiente creare una cartella, inserire i campioni in formato wav e importare la cartella dall'interfaccia del programma. E' quindi tecnicamente possibile riprodurre anche timbri diversi da quelli di batteria.

## Esecuzione
### Requisiti
Per compilare ed eseguire il progetto sono necessari:
- JDK ≥ 1.8
- Maven

### Esecuzione
Per compilare il progetto, eseguire il comando `mvn install` nella directory principale del progetto. Per eseguirlo, lanciare `mvn exec:java` nella stessa directory.
