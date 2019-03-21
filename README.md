# SaDReMa

* The bit flippers submission to the 2019 https://www.gameofcode.eu/ !
*  http://sadrema.app (currently in simulation mode !)

# The Bit Flippers

* David Valcarcel (Developer)
* Loula Beck (Developer)
* Chetan Arora (Developer)
* Delian Delchev (Developer)
* Pierre Muller (Coach)

# SaDReMa

SaDReMa (pronounced sadriːmə) is the artificial entity behind the real-time complex decision making in BeamCatcher. SaDreMa is inspired by real-world Satellite Dynamic Resource Management systems and relies on sophisticated multi-objective optimization techniques and algorithms. Don't be mad at SaDreMa if she doesn't serve your needs, she really tries to be fair !

# Power and Priority

Keep in mind that the satellites have a limit to the total power they can deliver. Also, beams can split, grow and move, but cannot grow infinitely! Try to better lure the beams by increasing your marker's priority and lowering your requested power. See how long you can stay covered and try to catch the beam !

# Visualize

In SaDReMa you play the role of a satellite customer. Satellites provide connectivity by bathing earth in a shower of bits - you just need to catch the beam ! Place your marker anywhere on the globe to request connectivity. Beams from satellites are represented by blue shapes on earth. Beams deliver a certain power - this is the number displayed in the beam. SaDreMa will do her best to instruct the satellites to cover your markers with beams.

# Built With

* Angular, NodeJS, Leaflet, Twitter Bootstrap, Angular Material...
* Backend in Java
* Optimation algorithmics in Java
* Contributions from: http://jmetal.sourceforge.net/, https://github.com/Berico-Technologies/Geo-Coordinate-Conversion-Java, Opensky-Networks
* Modeled on: https://en.wikipedia.org/wiki/World_Geodetic_System and https://en.wikipedia.org/wiki/Military_Grid_Reference_System

# UI App
* Angular.IO 7.x PWA + Leaflet and Socket.IO frontend, NodeJS + Socket.IO backend.
* Stateful track of beams, planes, markers, satellites.
* The planes open-data comes from OpenSky-Networks.
* Every marker is animated.
* All planes are smoothly animated within the predictred direction and speed, according to the data provided by the Opensky.
* All markers can be moved freely and this is viewed in real time among all the current clients.
* With the exception of the beams, everything is updated only if a change in its status has been received
