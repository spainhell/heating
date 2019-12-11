Aplikace HEATING slouží k ovládání vytápění chalupy.

Na chalupě je elektrokotel, který je připojen k Raspberry Pi 1B+. Dále jsou k RPi připojena teplotní čidla z pěti místností.
Na RPi běží program v Pythonu, který ve smyčce kontroluje teploty a požadavky. Ven je vystaveno API, které umožňuje získat informace o aktuálním stavu a také umožňuje měnit parametry vytápění.

Aplikace komunikuje s výše popsaným API.
Při 1. spuštění je uživatel vyzván k zadání URL, uživatelského jména a hesla. Údaje jsou uloženy v SharedPreferences.

Aplikace zobrazí úvodní přehled, kde je informace o vytápění (on/off) a ohřevu TUV (on/off).
