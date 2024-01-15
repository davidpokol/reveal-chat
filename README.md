
![](https://i.imgur.com/hn3aTxh.png)

## Az alkalmazásról

A **REVEAL** az online ismerkedés következő szintje. <br> Döntsd el **TE** hogy **milyen témá(k)ban**, szeretnél társalgást indítani, és beszélgess olyan emberekkel **akikkel szeretnél**! <br>
**Ne aggódj!...** A személyes információk cseréje egyszerre történik, ha már mindkét fél beleegyezett!

<p align="center">
  <img title="Főoldal" src="https://i.imgur.com/i0TIPOy.png"/>
</p>

### Könnyebb online ismerkedés!

Az általam fejlesztett oldalon maga a weblap az úgynevezett harmadik fél, aki 
biztosítja a személyes információk egyidejű cseréjét. A beszélgetés mindkét tagjának 
lehetősége van információcserét kezdeményezni. </br>
Ebben az esetben, a másik felhasználó egy értesítés 
formájában vesz tudomást a kezdeményezésről, melyet vagy elfogad, vagy elutasít.

<p align="center">
  <img title="REVEAL kérés" src="https://i.imgur.com/4i540VQ.png"/>
</p>

Ha az előbbi opciót válassza, akkor alkalmazás valamennyi felhasználónak kimutatja chat partnere adatait. Ellenkező esetben a funkció visszaáll alaphelyzetbe, újabb kéréseket lehet kezdeményezni a csevegő feleknek.

<p align="center">
  <img title="REVEAL funkció, két felhasználó szemszögéből" src="https://i.imgur.com/No3oDUc.png"/>
</p>

### A lehető legjobb párosítási algoritmus

Az algoritmus először szűr a lehetséges partnerekre, majd szűrési fázisok segítségével a halmazt tovább szűkíti, addig míg az pontosan egy egyént nem tartalmaz.
Ezen szűrési feltételek alkalmazása hozzájárul ahhoz, hogy hasonló személyiségű egyének találjanak egymásra.

#### Szűrési fázisok

##### A, ha nincs kiválasztott téma

Az alkalmazáson belül nem kötelező megadni témakört. Ez esetben a rendszer, a felhasználók életkorai szerint végez szűrést oly módon, hogy azt a beszélgetésre várakozó felhasználót válassza ki aki életkorban a lehető legközelebb áll a csevegő partnert kereső felhasználóval.

Abban az esetben, ha az életkor szerinti szűrés nem lehetséges (pl:. több azonos életkorú felhasználó is várakozik a lobby-ban), akkor az a felhasználó lesz kiválasztva, aki a legtöbb ideje várakozik beszélgetésre.

##### B, ha van kiválasztott téma

Ha a felhasználó meghatároz legalább egy olyan témát, amelyben szeretne beszélgetést indítani, a rendszer első lépésben kiszűri azt a felhasználókat, akikkel a legtöbb közös topikjuk van.
Ha az így képzett halmaz egy felhasználót tartalmaz, akkor megvan a csevegő partner.
Ellenkező esetben életkori szűrésre kerül sor. Ez ugyanúgy zajlik, mint ahogyan az **A** pontban említettem. Hasonlóan, ha ezen szűrési fázis után több mint egy felhasználó marad a halmazban, akkor a csatlakozási idő dönt.

## Hogyan futtatsd saját gépeden?

Klónozd le a projektet

```bash
  git clone https://github.com/davidpokol/reveal-chat.git
```

Navigálj a projekt mappájába

```bash
  cd reveal-chat
```

Telepítsd fel a szükséges függőségeket

```bash
  mvn install
```

Indítsd el a szervert

```bash
  mvn spring-boot:run
```

Majd böngészőben navigálj a [http://localhost:3000](http://localhost:3000/) címre

- - -
#### Ez a projekt [Shane Lee videó anyaga](https://youtu.be/U4lqTmFmbAM?si=8eBZAC2xwDzIHL4c) alapján készült.
