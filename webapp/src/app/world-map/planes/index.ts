import {map as Map} from '../map';
import {Marker} from '../marker';
import * as L from 'leaflet';

let map;
let planeLayer = L.layerGroup();
let plns = {};

let icon = L.icon({
  iconUrl: 'assets/aeroplane.svg',
  iconSize: [16, 16],
  iconAnchor: [0, 0]
});

class Plane extends Marker {
  speed: number; // speed in m/s
  interval = 2000;
  deg: number;
  intervalT: any;
  lon: number;
  lat: number;
  count: number = 0;

  constructor(plane) {
    super(plane.latlng, icon, 2000, planeLayer, plane.deg);

    this.lat = plane.latlng[0];
    this.lon = plane.latlng[1];
    this.speed = plane.speed;
    this.deg = plane.deg;

    this.removeZoomTransition(map);

    this.intervalT = setInterval(() => {
      this.nextStep();
    }, this.interval);
  }

  z = x => ((450 - x) % 360);

  nextStep() {
    this.count++;
    let {lon, lat} = this.getLonLat();
    this.marker.setLatLng([lat, lon]);
  }

  getLonLat() {
    let R = this.count * this.speed * (1000 / this.interval) * 90 / (10750 * 1000);
    let lon = this.lon + R * Math.cos(this.z(this.deg) * Math.PI / 180);
    let lat = this.lat + R * Math.sin(this.z(this.deg) * Math.PI / 180);
    return {lon: lon, lat: lat, deg: this.deg, speed: this.speed, R: R};
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
