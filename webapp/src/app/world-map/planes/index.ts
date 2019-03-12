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
  surface90 = 10750; // How much km is 90 degree
  count = 0;

  constructor(plane, public pollInterval = 5000) {
    super(plane.latlng, icon, pollInterval, planeLayer, plane.deg);

    this.interval = pollInterval;
    this.lat = plane.latlng[0];
    this.lon = plane.latlng[1];
    this.speed = plane.speed;
    this.deg = plane.deg;

    this.removeZoomTransition(map);
    this.setTransition();
    this.poller();
  }

  z = x => ((450 - x) % 360);

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
    const {lon, lat, interval} = this.getLonLat();
    if (interval !== this.interval) {
      this.interval = interval;
      this.setTransition();
      this.poller(); // Change the right speed
    }
    this.marker.setLatLng([lat, lon]);
  }

  getLonLat() {
    const R = this.count * this.speed * (1000 / this.interval) * 90 / (this.surface90 * 1000);
    const lon = this.lon + R * Math.cos(this.z(this.deg) * Math.PI / 180);
    const lat = this.lat + R * Math.sin(this.z(this.deg) * Math.PI / 180);
    const interval = this.surface90 / (90 * this.speed);
    return {lon: lon, lat: lat, deg: this.deg, speed: this.speed, R: R, interval: interval};
  }

  setLonLat(lon, lat, deg = this.deg, speed = this.speed) {
    this.lon = lon;
    this.lat = lat;
    this.speed = speed;
    this.deg = deg;
    this.count = 0;
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
      //plns[id].marker.marker.setLatLng(plane.latlng);
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
