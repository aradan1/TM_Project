# TM_Project

1. Desenvolupar un còdec (codificador i descodificador) de vídeo basat en l’algorisme de detecció
i compensació de moviment.
2. Optimitzar dels paràmetres del còdec per obtenir un bon equilibri entre rendiment, temps de
compressió, reproducció en temps real i qualitat d’imatge.
3. Crear un reproductor de vídeo a partir d’una seqüència d’imatges que les reprodueixi a un ritme
controlat.
4. Implementar filtres puntuals i altres transformacions lineals sobre les imatges.

#### ARGUMENTS LÍNIA DE COMANDES

- –i, --input <path to file.zip> : Fitxer d’entrada. Argument obligatori. 
- –o, --output <path to file> : Nom del fitxer en format propi amb la seqüència d’imatges
de sortida i la informació necessària per la descodificació.
- –e, --encode : Argument que indica que s’haurà d’aplicar la codificació sobre el conjunt d’imatges
d’input i guardar el resultat al lloc indicat per output. En acabar, s’ha de procedir a reproduir el
conjunt d’imatges sense codificar (input). Per una descripció detallada del que ha de realitzar, vegeu
l’apartat --Encode.
- –d, --decode : Argument que indica que s’haurà d’aplicar la descodificació sobre el conjunt
d’imatges d’input provinents d’un fitxer en format propi i reproduir el conjunt d’imatges descodificat
(output). Per una descripció detallada del que ha de realitzar, vegeu l’apartat --Decode.
- --fps <value> : nombre d’imatges per segon amb les quals és reproduirà el vídeo.
- --binarization <value> : filtre de binarització utilitzant el valor llindar indicat.
- --negative : aplicació d’un filtre negatiu sobre la imatge.
- --averaging <value>: aplicació d’un filtre de promig en zones de value x value.
- --nTiles <value,...> : nombre de tessel·les en la qual dividir la imatge. Es poden indicar
diferents valors per l’eix vertical i horitzontal, o bé especificar la mida de les tessel·les en píxels.
- --seekRange <value> : desplaçament màxim en la cerca de tessel·les coincidents.
- --GOP <value> : nombre d’imatges entre dos frames de referència
- --quality <value> : factor de qualitat que determinarà quan dos tessel·les és consideren
coincidents.
- -b, --batch : en aquest mode no s’obrirà cap finestra del reproductor de vídeo. Ha de permetre
executar el còdec a través de Shell scripts per avaluar de forma automatitzada el rendiment de
l’algorisme implementat en funció dels diferents paràmetres de configuració.
