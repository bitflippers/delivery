import {World} from '../map';
import {Marker} from '../marker';
import * as L from 'leaflet';

let map;
const userMarkerLayer = L.layerGroup();
const mrks = {};

export abstract class Markers {
  public static init() {
    map = World.map().map;
    userMarkerLayer.addTo(map);
    World.map().control.addOverlay(userMarkerLayer, 'Markers');
  }

  public static univId(marker) {
    return marker.slot.identifier + '.' + marker.uuid;
  }

  public static buildIcon(marker) {
    // console.log("icon color option", marker);
    return L.icon({
      iconUrl: marker.icon,
      iconSize: [32, 32],
      iconAnchor: [0, 0],
      className: 'markerColor' + (marker.priority || 1),
    });

  }

  public static updateMarker(marker) {
    const id = Markers.univId(marker);
    const m = marker.location.mgrsWGS84Coordinate;
    const latlng = [m.latitude, m.longitude];
    // console.log('marker', m, latlng);
    if (typeof mrks[id] == 'object') {
      mrks[id].latlng = latlng;
      mrks[id].marker.marker.setLatLng(mrks[id].latlng);
      if (marker.priority !== mrks[id].marker.priority) {
        mrks[id].marker.priority = marker.priority;
        mrks[id].marker.className = 'markerColor' + (marker.priority || 1);
        mrks[id].marker.iconL = Markers.buildIcon(marker);
        mrks[id].marker.marker.setIcon(mrks[id].marker.iconL); // Change the icon
      }
    } else {
      // Lets add a marker
      marker.latlng = latlng;
      marker.iconL = Markers.buildIcon(marker);
      mrks[id] = marker;
      mrks[id].marker = new UserMarker(marker);
    }
  }

  public static deleteMarker(marker) {
    const id = Markers.univId(marker);
    console.log('We delete marker');
    if (mrks[id]) {
      mrks[id].marker.close();
      delete mrks[id];
    }
  }
}

class UserMarker extends Marker {
  constructor(public m, public interval = 1000) {
    super(m.latlng, m.iconL, interval, userMarkerLayer, 0);
    this.removeZoomTransition(map);
    console.log('marker priority:', m.priority);
    this.marker.bindTooltip('Priority: ' + m.priority.toString());
  }
}
