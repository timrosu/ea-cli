# ea-cli

CLI vmesnik za eAsistent napisan v Javi

## Funkcije

- prijava (in odjava)
- zadnja ocena
- ocenjevanja
- dnevni urnik
- ocene (zaenkrat brez nenapovedanih popravljanj)
- izostanki

## Roadmap

- [x] več informacij glede izostankov
- [ ] tedenski urnik
- [ ] zamenjava prijavnih podatkov za `refresh_token` v `conf/credentials.json`
- [ ] zaščita prijavnih podatkov
- [ ] migracija na mobilni api
- [ ] jedilnik in naročanje

## Namestitev

Prilagam `run.sh` skript za testiranje programa. 
Za uporabnike GNU/Linux sistema sem napisal `install.sh` za poenostavljeno namestitev in `uninstall.sh` za odstranitev.

## Ozadje

Opazil sem, da eAsistentov spletni vmesnik poda veliko (nepotrebnih) zahtev njihovemu API-ju. Po pregledu zahtev in vrnjenih podatkov sem ugotovil, da je mogoče iz storitve na ta način dobiti podatke za katere sami zahtevajo plačilo.

Za raziskovanje spletnega vmesnika sem uporabil [mitmproxy](https://mitmproxy.org/), za raziskovanje mobilnega programa (MojAsistent) pa zaradi poostrene varnosti (SSL cert pinning) [frido](https://github.com/frida/frida) in [tale skript](https://codeshare.frida.re/@akabe1/frida-multiple-unpinning).

Hvala [gapidobri](https://github.com/gapidobri) za inspiracijo z [eACLI](https://github.com/gapidobri/eacli).
