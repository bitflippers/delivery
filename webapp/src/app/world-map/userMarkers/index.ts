import {World} from '../map';
import {Marker} from '../marker';
import * as L from 'leaflet';

let map;
const userMarkerLayer = L.layerGroup();
const mrks = {};

class UserMarker extends Marker {
  constructor(public m, public interval = 200) {
    super(m.latlng, m.iconL, interval, userMarkerLayer, 0);
    this.removeZoomTransition();
    // console.log('marker:', m);
    // this.marker.bindTooltip('Priority: ' + m.priority.toString());
  }
}

export abstract class Markers {
  public static init() {
    map = World.map().map;
    userMarkerLayer.addTo(map);
    World.map().control.addOverlay(userMarkerLayer, 'Markers');
  }

  public static univId(marker) {
    return marker.slot.identifier + '.' + marker.uuid;
  }

  public static buildIcon(marker, c = '') {
    // console.log("icon color option", marker);
    return L.icon({
      iconUrl: marker.icon,
      iconSize: [32, 32],
      iconAnchor: [16, 32],
      className: `${c} markerColor${marker.priority || 1}`,
    });

  }

  public static updateMarkerPos(id, data) {
    if (typeof mrks[id] === 'object') {
      mrks[id].latlng = data.latlng;
      mrks[id].marker.marker.setLatLng(mrks[id].latlng);
    }
  }

  public static updateMarkerStart(id, data) {
    if (typeof mrks[id] === 'object') {
      mrks[id].marker.marker._icon.classList.add('markerSelected');
    }
  }

  public static updateMarkerEnd(id, data) {
    if (typeof mrks[id] === 'object') {
      mrks[id].marker.marker._icon.classList.remove('markerSelected');
    }
  }

  public static updateMarker(marker) {
    const id = Markers.univId(marker);
    const m = marker.location.mgrsWGS84Coordinate;
    const latlng = [m.latitude, m.longitude];
    // console.log('marker', marker, m, latlng);
    if (typeof mrks[id] === 'object') {
      mrks[id].latlng = latlng;
      mrks[id].marker.marker.setLatLng(mrks[id].latlng);
      if (marker.priority !== mrks[id].marker.priority) {
        mrks[id].marker.priority = marker.priority;
        mrks[id].marker.iconL = Markers.buildIcon(marker);
        Markers.tooltip(mrks[id], marker);
        mrks[id].marker.marker.setIcon(mrks[id].marker.iconL); // Change the icon
      }
    } else {
      // Lets add a marker
      marker.latlng = latlng;
      marker.iconL = Markers.buildIcon(marker);
      mrks[id] = marker;
      mrks[id].markerID = id;
      mrks[id].marker = new UserMarker(marker);
      Markers.tooltip(mrks[id], marker);
    }
    return mrks[id];
  }

  public static tooltip(m, marker) {
    m.marker.marker.bindTooltip(`<IMG STYLE="width: 8pt; height: 8pt;" SRC="${marker.icon}">${marker.user.username}<BR>Priority: ${marker.priority}<BR>Requested Units: ${marker.requestedUnits}`);
  }

  public static deleteMarker(marker) {
    const id = Markers.univId(marker);
    // console.log('We delete marker');
    if (mrks[id]) {
      mrks[id].marker.close();
      delete mrks[id];
    }
  }
}
