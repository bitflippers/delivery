import * as L from 'leaflet';
import 'leaflet-rotatedmarker';

export class Marker {
  marker: L.Marker;

  constructor(
    public pos: L.LatLng,
    public icon: L.Icon,
    public interval = 1000,
    public layer: any = null,
    public rotationAngle = 0,
    public options = {}
  ) {
    let o = <any>Object.assign({}, {
      icon: this.icon,
      draggable: true,
      rotationAngle: this.rotationAngle
    }, options);
    //console.log("Option display: ",o);
    this.marker = L.marker(pos, o);

    this.setTransition();

    if (this.layer) {
      this.marker.addTo(this.layer);
    }
  }

  removeZoomTransition(map: L.Map, fastInterval = 50) {
    if (L.DomUtil.TRANSITION) {
      map.on('movestart', e => {
        this.setTransition(fastInterval);
      });
      map.on('moveend', e => {
        this.setTransition();
      });
      map.on('zoomstart', e => {
        this.setTransition(fastInterval);
      });
      map.on('zoomend', e => {
        this.setTransition();
      });
    }
  }

  setTransition(interval = this.interval) {
    if (L.DomUtil.TRANSITION) {
      //console.log('Transition', interval);
      if ((<any>this.marker)._icon) {
        (<any>this.marker)._icon.style[L.DomUtil.TRANSITION] = ('all ' + interval + 'ms linear');
      } else { // Wait the icon to reappear
        setTimeout(() => this.setTransition(interval), 50);
      }
    }
  }

  close() {
    this.marker.removeFrom(this.layer);
  }
}
