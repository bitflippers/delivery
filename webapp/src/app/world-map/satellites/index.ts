import {World} from '../map';
import {Marker} from '../marker';
import * as L from 'leaflet';
// import 'leaflet-draw';
// import '@jjwtay/leaflet.ellipse';
// import '@jjwtay/leaflet.draw-ellipse';
import {MGRS} from '../mgrs';

let map;
const equatorLayer = L.layerGroup();
const equatorLine = L.polyline([[0, -300], [0, 300]], {
  'color': 'red',
  'weight': 1,
  'opacity': 0.5
}).addTo(equatorLayer);

const beamLayer = L.layerGroup();
// beamLayer.stop('click');

const icon = L.icon({
  iconUrl: 'assets/satellite-icon.png',
  iconSize: [32, 32],
  iconAnchor: [20, 12]
});

// Mockup data
const satList = {
  'Sat_1': {
    'name': 'Sat_1',
    'pos': [0, -120]
  },
  'Sat_2': {
    'name': 'Sat_2',
    'pos': [0, 0]
  },
  'Sat_3': {
    'name': 'Sat_3',
    'pos': [0, 120]
  }
};

// Beam data
const beamList = [
  {
    'satellite': 'Satellite1',
    'latng': [0, 0],
    'radii': [10, 20],
    'rotate': 90
  }
];


const satObjs = {};
const beamObjs = {};

class Satellite extends Marker {

  path = [[-0.3, 0], [0, -0.3], [0.3, 0], [0, 0.3]];
  count = 0;
  private intervalT: any = null;

  constructor(public s, public interval = 1000) {
    super(s.pos, icon, interval, equatorLayer);
    this.count = parseInt(<any>(Math.random() * this.path.length));
    this.removeZoomTransition(map);
    this.intervalT = setInterval(() => {
      this.nextStep();
    }, this.interval);
    this.marker.bindTooltip(s.name);
  }

  nextStep() {
    this.count = (++this.count) % this.path.length;
    this.marker.setLatLng([this.s.pos[0] + this.path[this.count][0], this.s.pos[1] + this.path[this.count][1]]);
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
    // this.ellipse.stop('click');
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

  // TODO: Do the beam draw and animation
  draw() {
    this.l = L.layerGroup();
    this.l.addTo(beamLayer);


    this.beam.footprint.setSADREMAGridCell.forEach(n => {
      console.log('Draw circle', n);
      const {centerX, centerY, x1, y1, x2, y2} = MGRS.convert(n.columnIndex, n.rowIndex);
      console.log('centerX', centerX, 'centerY', centerY, 'xy', x1, y1, x2, y2);
      // const c = L.circle([centerY, centerX], {
      //   color: 'blue',
      //   fillColor: '#800000',
      //   fillOpacity: 0.3,
      //   radius: 330000
      // }).addTo(this.l);
      const p = L.polygon([
        [y1, x1], [y2, x1], [y2, x2], [y1, x2], [y1, x1]
      ], {
        color: 'blue',
        fillColor: '#008080',
        fillOpacity: 0.3
      });
      p.bindTooltip(`Name: ${this.beam.name} Col: ${n.columnIndex} Row: ${n.rowIndex}<BR>X1: ${x1} Y1: ${y1} X2: ${x2} Y2: ${y2}`);
      p.addTo(this.l);
      // c.stop('click');
    });
  }
}


export abstract class Sat {
  public static init() {
    map = World.map().map; // Now we have map
    equatorLayer.addTo(map);
    beamLayer.addTo(map);
    Object.values(satList).forEach(s => Sat.addSatellite(s));
    World.map().control.addOverlay(equatorLayer, 'Satelltes');
    World.map().control.addOverlay(beamLayer, 'Beams');
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
    if (typeof beamObjs[beam.name] === 'object') {
      beamObjs[beam.name].pos = beam.pos;
      beamObjs[beam.name].b.draw(beam.pos);
    } else {
      beamObjs[beam.name] = beam;
      beamObjs[beam.name].b = new Beam(beam);
    }
  }

  public static delBeam(beam) {
    if (typeof beamObjs[beam.name] === 'object') {
      beamObjs[beam.name].s.closeX();
      delete beamObjs[beam.name];
    }
  }

}
