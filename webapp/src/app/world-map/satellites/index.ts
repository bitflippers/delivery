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
  color: 'red',
  weight: 1,
  opacity: 0.5
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
  Sat_1: {
    name: 'Sat_1',
    pos: [0, -120]
  },
  Sat_2: {
    name: 'Sat_2',
    pos: [0, 0]
  },
  Sat_3: {
    name: 'Sat_3',
    pos: [0, 120]
  }
};

// Beam data
const beamList = [
  {
    satellite: 'Satellite1',
    latng: [0, 0],
    radii: [10, 20],
    rotate: 90
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
    this.count = parseInt((Math.random() * this.path.length) as any, 10);
    this.removeZoomTransition();
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
    ], {
      draggable: true,
      opacity: 0.2
    } as any).addTo(beamLayer);
    // this.ellipse.stop('click');
  }
}

const satColors = {
  Sat_1: '#008080',
  Sat_2: '#800080',
  Sat_3: '#808000'
};

class Beam {

  ellipse: Ellipse;

  l: L.LayerGroup;

  b = [];

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

    if (this.b.length > 0) {
      this.b.forEach(p => p.remove());
      this.b = [];
    }


    this.beam.footprint.setSADREMAGridCell.forEach(n => {
      // console.log('Draw circle', n);
      const {centerX, centerY, x1, y1, x2, y2} = MGRS.convert(n.columnIndex, n.rowIndex);
      // console.log('centerX', centerX, 'centerY', centerY, 'xy', x1, y1, x2, y2);
      const p = L.polygon([
        [y1, x1], [y2, x1], [y2, x2], [y1, x2], [y1, x1]
      ], {
        color: 'rgba(0,0,0,0)',
        fillColor: satColors[this.beam.satRef.name] || '#808080',
        fillOpacity: 0.4
      });
      p.bindTooltip(`Sat: ${this.beam.satRef.name} Beam: ${this.beam.name}<BR>Col: ${n.columnIndex} Row: ${n.rowIndex}<BR>X1: ${x1} Y1: ${y1} X2: ${x2} Y2: ${y2}`);
      p.addTo(this.l);
      this.b.push(p);
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
  }

  public static addSatellite(satellite) {
    if (typeof satObjs[satellite.name] === 'object') {
      satObjs[satellite.name].pos = satellite.pos;
      satObjs[satellite.name].s.marker.setLatLng(satellite.pos);
    } else {
      satObjs[satellite.name] = satellite;
      satObjs[satellite.name].s = new Satellite(satellite);
    }
  }

  public static delSatellite(satellite) {
    if (typeof satObjs[satellite.name] === 'object') {
      satObjs[satellite.name].s.closeX();
      delete satObjs[satellite.name];
    }
  }

  public static uniqueId(beam /*, satellite*/) {
    return beam.uuid;
  }

  public static addBeam(beam /*, satellite */) {
    const id = Sat.uniqueId(beam /*, satellite */);
    if (typeof beamObjs[id] === 'object') {
      beamObjs[id].pos = beam.pos;
      beamObjs[id].b.draw(beam.pos);
    } else {
      beamObjs[id] = beam;
//      beamObjs[id].sat = satellite;
      beamObjs[id].b = new Beam(beamObjs[id]);
    }
  }

  public static delBeam(beam) {
      beam.b.b.forEach(b => b.remove());
      delete beamObjs[Sat.uniqueId(beam)];
  }

  public static dropAllBeams() {
    Object.values(beamObjs).forEach(n => Sat.delBeam(n)); // Remove all beams
  }

}
