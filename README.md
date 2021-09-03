# znalosti.gov.sk

Centrálny portál strojovo-spracovateľných znalostí pre informačné systémy verejnej správy.


Projekt je v súčasnosti v testovacom režime vývíjaný a poskytovaný ako otvorený [Dátovou kanceláriou](https://datalab.digital) MIRRI. V súčasnosti prebieha jeho nasadenie do vládneho cloudu.

## Spustenie portálu na vlastnom serveri 

1. Najskôr je nutné mať naištalovanú RDF databázu. Počas implementácie prvotnej verzie portálu znalosti.gov.sk bola použitá otvorená databáza [RDF4J Server](https://rdf4j.org/documentation/tools/server-workbench/)

2. Následne je do nej potrebné nahrať aktuálnu verziu DB (0.5), ktorá bola vytvorená jednak z množiny základných štrukturálnych metadát , tj. Centrálny model údajov verejnej správy + vybrané základné číselníky vo formáte RDF, spolu s množinou datasetov obsahujúcich vybrané inštančné dáta (organizácie, adresy, územná štruktúra BSK, publikačné minimum MIRRI a podobne).  

   Pripravené dáta na loadnutie do databázy sú dostupné

 	`src/main/resources/db` .

   DB je možné znova vytvoriť, resp. aktualizovať prostredníctvom dátových pipelines, ktoré sú spustiteľné pomocou ETL systému [LinkedPipes](https://etl.linkedpipes.com/). Súčasťou zdrojového kódu je množina potrebných pipelines pre projekt znalosti.gov.sk, ktoré sú dostupné na

    `src/main/resources/META-INF/resources/pipelines` .


3. Po príprave databázy je možné použiť maven na zbuildovanie samotného portálu, ktorý je nutné pripojiť na vytvorenú RDF databázu (súbor aaplication.properties)