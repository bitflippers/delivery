import * as L from 'leaflet';

let mapObj: Map = null;

const gridGeoJSON = {
  'type': 'FeatureCollection',
  'features': []
};

class Map {
  map: L.Map;
  grid: any;
  gridLayer: any;
  control: any;

  constructor() {
    this.map = L.map('worldmap', {
      center: [0, 0],
      zoom: 3,
      attributionControl: false,
      minZoom: 2,
      maxZoom: 15
    }).fitWorld();

    L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'OpenStreetMaps',
      subdomains: ['a', 'b', 'c']
    }).addTo(this.map);


    let bounds = L.latLngBounds([[-88, -205], [88, 205]]);
    this.map.setMaxBounds(bounds);
    this.map.on('drag', () => this.map.panInsideBounds(bounds, {animate: false}));

    this.drawGridInit();
    this.drawGrid();
  }

  drawGrid() {
    this.gridLayer = L.layerGroup();
    this.grid = L.geoJSON(<any>gridGeoJSON, {
      'style': feature => {
        return {
          'stroke': true,
          'weight': 1,
          'opacity': 0.3,
          'lineCap': 'round'
        }
      }
    }).addTo(this.gridLayer);
    this.gridLayer.addTo(this.map);
    this.control = L.control.layers({}, {
      'Grid': this.gridLayer
    }).addTo(this.map);
    // console.log('Control', this.control);
  }

  drawGridInit() {
    for (let i = -180; i <= 180; i += 6) {
      gridGeoJSON.features.push({
        'type': 'Feature',
        'geometry': {
          'type': 'LineString',
          'coordinates': [
            [i, -88], [i, 88]
          ]
        }
      });
    }
    for (let i = -88; i <= 88; i += 8) {
      gridGeoJSON.features.push({
        'type': 'Feature',
        'geometry': {
          'type': 'LineString',
          'coordinates': [
            [-180, i], [180, i]
          ]
        }
      });
    }
  }
}

const cbList = [];

export abstract class World {
  public static init() {
    mapObj = new Map();
    cbList.forEach(cb => cb(mapObj));
    cbList.splice(0); // Empty it without assigning new
  }

  public static map() {
    return mapObj;
  }

  public static cbOnMap(cb) {
    if (mapObj) { return cb(mapObj); }
    cbList.push(cb);
  }
}
