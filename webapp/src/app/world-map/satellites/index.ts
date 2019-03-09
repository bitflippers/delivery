import { map as Map } from '../map';
import { Marker } from '../marker';
import * as L from 'leaflet';
//import 'leaflet-draw';
//import '@jjwtay/leaflet.ellipse';
//import '@jjwtay/leaflet.draw-ellipse';

let map;
let equatorLayer = L.layerGroup();
let equatorLine = L.polyline([[0, -300], [0, 300]], {
    "color": "red",
    "weight": 1,
    "opacity": 0.5
}).addTo(equatorLayer);

let beamLayer = L.layerGroup();
//beamLayer.stop('click');

let icon = L.icon({
    iconUrl: 'assets/satellite-icon.png',
    iconSize: [32, 32],
    iconAnchor: [20, 12]
});

// Mockup data
let satList = {
  "Sat_1": {
    "name": "Sat_1",
    "pos": [0, -120]
  },
  "Sat_2": {
    "name": "Sat_2",
    "pos": [0, 0]
  },
  "Sat_3": {
    "name": "Sat_3",
    "pos": [0, 120]
  }
}

// Beam data
let beamList = [
  {
    "satellite": "Satellite1",
    "latng": [0, 0],
    "radii": [10, 20],
    "rotate": 90
  }
];


const satObjs = { };
const beamObjs = { };

class Satellite extends Marker {

    private intervalT: any = null;
    path = [ [ -0.3, 0 ], [ 0, -0.3 ], [ 0.3, 0 ], [ 0, 0.3 ] ];
    count = 0;

    constructor(public s, public interval = 1000) {
        super(s.pos, icon, interval, equatorLayer);
        this.count = parseInt(<any>(Math.random()*this.path.length));
        this.removeZoomTransition(map);
        this.intervalT = setInterval(() => {
          this.nextStep();
        }, this.interval);
        this.marker.bindTooltip(s.name);
    }

    nextStep() {
        this.count = (++this.count) % this.path.length;
        this.marker.setLatLng([this.s.pos[0] + this.path[this.count][0], this.s.pos[1]+this.path[this.count][1]]);
    }

    closeX() {
        clearInterval(this.intervalT);
        this.marker.remove();
    }
}

class Ellipse {
  public ellipse: any;
  constructor(public e) {
    this.ellipse = L.polygon([
      [51.509, -0.08],
      [51.503, -0.06],
      [51.51, -0.047],
      [0, 10],
      [-10, 0]
    ], <any>{
      draggable: true,
      opacity: 0.2
    }).addTo(beamLayer);
    //this.ellipse.stop('click');
  }
}

class Beam {

  ellipse: Ellipse;

  l: L.LayerGroup;

  constructor(public beam) {
    // this.ellipse = new Ellipse({
    // });
    this.draw();
  }

  remove() {
    this.l.remove();
  }

  //TODO: Do the beam draw and animation
  draw() {
    this.l = L.layerGroup();
    this.l.addTo(beamLayer);


    this.beam.footprint.setSADREMAGridCell.forEach(n => {
      console.log('Draw circle', n);
      let lon = (parseInt(n.columnIndex) * 6) - 180;
      let lat = (parseInt(n.rowIndex) * 8) - 90;
      let c = L.circle([lat, lon], {
        color: 'blue',
        fillColor: '#008080',
        fillOpacity: 0.3,
        radius: 330000
      });
      c.bindTooltip(this.beam.name+' '+n.rowIndex+' '+n.columnIndex + ' out ' + lat + ' ' + lon);
      c.addTo(this.l);
      //c.stop('click');
    })
  }
}


export abstract class Sat {
  public static init() {
    map = Map().map; // Now we have map
    equatorLayer.addTo(map);
    beamLayer.addTo(map);
    Object.values(satList).forEach(s => Sat.addSatellite(s));
    Map().control.addOverlay(equatorLayer, "Satelltes");
    Map().control.addOverlay(beamLayer, "Beams");
    // let x = new Beam({
    // });
  }

  public static addSatellite(satellite) {
    if (typeof satObjs[satellite.name] == 'object') {
      satObjs[satellite.name].pos = satellite.pos;
      satObjs[satellite.name].s.marker.setLatLng(satellite.pos);
    } else {
      satObjs[satellite.name] = satellite;
      satObjs[satellite.name].s = new Satellite(satellite);
    }
  }

  public static delSatellite(satellite) {
    if (typeof satObjs[satellite.name] == 'object') {
      satObjs[satellite.name].s.closeX();
      delete satObjs[satellite.name];
    }
  }

  public static addBeam(beam) {
    if (typeof beamObjs[beam.name] == 'object') {
      beamObjs[beam.name].pos = beam.pos;
      beamObjs[beam.name].b.draw(beam.pos);
    } else {
      beamObjs[beam.name] = beam;
      beamObjs[beam.name].b = new Beam(beam);
    }
  }

  public static delBeam(beam) {
    if (typeof beamObjs[beam.name] == 'object') {
      beamObjs[beam.name].s.closeX();
      delete beamObjs[beam.name];
    }
  }

}
