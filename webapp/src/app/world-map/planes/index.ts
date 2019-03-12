import {map as Map} from '../map';
import {Marker} from '../marker';
import * as L from 'leaflet';
import {interval} from "rxjs";

let map;
let planeLayer = L.layerGroup();
let plns = {};

const icon = L.icon({
  iconUrl: 'assets/aeroplane.svg',
  iconSize: [16, 16],
  iconAnchor: [0, 0]
});

class Plane extends Marker {
  speed: number; // speed in m/s
  deg: number;
  intervalT: any;
  lon: number;
  lat: number;
  surfaceDeg2Km = 90 / 10750; // 90 degree is 10750km, 1 deg is ~120km
  count = 0;

  constructor(public plane, public pollInterval = 5000) {
    super(plane.latlng, icon, pollInterval, planeLayer, plane.deg);

    this.interval = pollInterval;
    this.tooltip();
    this.setLonLat(plane.latlng[1], plane.latlng[0], plane.deg, plane.speed);
    this.removeZoomTransition(map);
    this.poller();
  }

  z = x => ((450 - x) % 360);

  tooltip() {
    let p = this.plane;
    this.marker.bindTooltip(`ID: ${p.id}<BR>Flight: ${p.n[1]} ${p.n[2]}<BR>Lat: ${p.n[5]} Lon: ${p.n[6]}`);
  }

  poller() {
    if (this.intervalT) {
      clearInterval(this.intervalT);
    }
    this.intervalT = setInterval(() => {
      this.nextStep();
    }, this.interval);
  }

  nextStep() {
    this.count++;
    const {lon, lat} = this.getLonLat();
    this.marker.setLatLng([lat, lon]);
  }

  getLonLat() {
    // R is in Degrees. Lets check how many degress the plane will move for the interval of time
    const R = this.count * (this.speed * this.interval * this.surfaceDeg2Km) / 1000000;
    const lon = this.lon + R * Math.cos(this.z(this.deg) * Math.PI / 180);
    const lat = this.lat + R * Math.sin(this.z(this.deg) * Math.PI / 180);
    return {lon: lon, lat: lat, deg: this.deg, speed: this.speed, R: R};
  }

  setLonLat(lon, lat, deg = this.deg, speed = this.speed) {
    this.lon = lon;
    this.lat = lat;
    this.speed = speed;
    this.deg = deg;
    this.count = 0;
    this.tooltip();
    this.setRotation(this.deg);
    this.setTransition();
    this.nextStep();
  }

  closeX() {
    clearInterval(this.intervalT);
    this.marker.remove();
  }

}

export abstract class Planes {
  public static init() {
    map = Map().map;
    planeLayer.addTo(map);
    Map().control.addOverlay(planeLayer, "Planes");
  }

  public static univId(plane) {
    return plane.id;
  }

  public static updatePlane(plane) {
    //console.log('update plane', plane);
    let id = Planes.univId(plane);
    if (typeof plns[id] == 'object') {
      // TODO: Check!!!!
      plns[id].plane = plane;
      plns[id].marker.setLonLat(plane.latlng[1], plane.latlng[0], plane.deg, plane.speed);
    } else {
      plns[id] = plane;
      plns[id].marker = new Plane(plane);
    }
  }

  public static deletePlane(plane) {
    let id = Planes.univId(plane);
    if (typeof plns[id] == 'object') {
      plns[id].marker.close();
      delete plns[id];
    }
  }
}
