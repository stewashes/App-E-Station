E-station : Progetto e Sviluppo di un' applicazione Android a

supporto della mobilità elettrica

- l’applicazione E-station ha l’obiettivo di fornire un supporto agli utenti che
possiedono o sfruttano mezzi elettrici per la propria mobilità personale . in
particolar modo tramite l’applicazione facilitiamo la ricerca dei punti di ricarica
segnalati sul territorio dalla community degli utenti registrati .

Specifiche
- lo sviluppo dell’applicazione ha richiesto un approfondimento specifico delle
seguenti piattaforme di supporto:
1) API per l’accesso a Google maps
2) Firebase
- Realtime Database
- Authentication
- Storage

- la gestione dei dati viene effettuata tramite il realtime db , in particolare
gestiamo l’aggiunta e la rimozione di un punto di ricarica . il nodo è caratterizzato
dai seguenti campi :
- Città
- Coordinate
- Marca
- N-posti auto
- Stato
- Via

l’utente può selezionare la marca e il numero posti disponibili , mentre i restanti
dati vengono ricavati tramite la geolocalizzazione .
Come chiave invece sfruttiamo la combinazione delle coordinate ( in termini di
longitudine e latitudine)

- l’autenticazione gestita tramite Firebase richiede i seguenti campi :

- Username
- E-mail
-Password
( con relativi controlli sull’input inserito,come pattern per e-mail e lunghezza
minima / massima username e pwd)
in caso l’utente sia già registrato può accedere tramite:

- E-mail e Password
- tramite Account Google

- tramite il servizio di Storage viene fornita la possibilità di personalizzare la propria
immagine dell’account , in caso di registrazione con account Google sarà
visualizzata quella presente nel profilo di google , altrimenti viene visualizzata
un’immagine di default . in entrambi i casi è comunque possibile impostare
un’immagine a propria scelta fra quelle disponibili nel proprio dispositivo.

Interazione con l’utente

Al primo avvio tramite l’activity relativa al tutorial mostriamo il funzionamento della
nostra applicazione .
successivamente tramite la Authentication activity gestiamo le schermate di login e
registrazione. In particolare con l’utilizzo dei fragment forniamo layout differenti a
seconda delle dimensioni del dispositivo e dell’orientamento ( es. sul tablet in modalità
LandScape presentiamo login e Registrazione affiancati) .
dopo una corretta registrazione ( o login ) l’utente viene reindirizzato al main activity . in
quest’ultima si può navigare tra i vari fragment tramite una navigation bar posta in basso .
in particolare abbiamo :
- un fragment che fornisce la mappa. dove sono indicate tutte le colonnine
segnalate. inoltre tramite un editText è possibili effettuare ricerche in funzione di

vie o città , è anche possibile geolocalizzare la propria posizione tramite un
apposito pulsante .
- un fragment che presenta una Recycler view contente la lista delle stazioni
disponibili, viene fornita anche: la possibilità di ricerca ( per marca o citta) , di
segnalare un nuovo stallo per la ricarica o di modificarne uno già esistente .

su ogni elemento della RV sono disponibili due azioni :
1) swipe dx per la rimozione di un punto di ricarica
2) swipe sx per visualizzare il punto selezionato sulla mappa

- infine l’ultimo fragment presenta una listview con le news ricavate dal RSS di
Automoto.it con il click su un elemento è possibile visualizzare (in
un’ulteriore activity) parte della descrizione, la data e il link originale ( in questa
forniamo anche la possibilità di condivisione delle news nell altre applicazione
presenti sul dispositivo)il servizio per la gestione e l’aggiornamento delle news
parte con il boot del dispositivo e tramite un timer task verifichiamo ogni trenta
minuti la presenza di novità.
il menù presente nella main activity permette di effettuare il logout e di accedere
all’area personale. in particolare quest’ultima presenta l’immagine di profilo e i principali
dati dell’utente, qui è inoltre possibile effettuare la modifica della propria immagine
tramite upload di foto/ immagine salvate , la cancellazione dell’account da firebase
oppure cambiare la password , questo avviene tramite l’invio di una e-mail precompilata
all’indirizzo inserito in firebase durante la registrazione .
nell’applicazione è stato anche implementato un servizio di notifiche. in particolare viene
segnalato all’utente l’inserimento di una nuova colonnina ( da parte di altri utenti ) , per
rispettare le specifiche di google sulle notifiche il servizio parte con la prima apertura
dell’applicazione , visto che tramite il tap sull’elemento l’utente può accedere alla lista
contente tutte le colonnine con anche quella nuova , il servizio mostra le notifiche solo
con account valido ( al momento loggato ) .
