import * as L from 'leaflet';
import 'leaflet-rotatedmarker';

export class Marker {
  marker: L.Marker;

  private transitionLock = {};
  private fastInterval = 20;

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

    this.marker = L.marker(pos, o);

    this.setTransition();

    if (this.layer) {
      this.marker.addTo(this.layer);
    }
  }

  setRotation(deg) {
    this.rotationAngle = deg;
    (<any>this.marker).setRotationAngle(deg);
  }

  setFastTransition(name) {
    this.transitionLock[name] = true;
    console.log('set fasttransition', name, this,this.transitionLock);
    this.setTransition(this.fastInterval);
  }

  unsetFastTransition(name) {
    delete this.transitionLock[name];
    console.log('unset fasttransition', name, this,this.transitionLock);
    if (Object.keys(this.transitionLock).length === 0) {
      this.setTransition();
    }
  }

  removeZoomTransition(map: L.Map, fastInterval = this.fastInterval) {
    if (L.DomUtil.TRANSITION) {
      this.marker.on('dragstart', e => {
        this.setFastTransition('drag');
      });
      this.marker.on('dragend', e => {
        this.unsetFastTransition('drag');
      });
      map.on('movestart', e => {
        this.setFastTransition('move');
      });
      map.on('moveend', e => {
        this.unsetFastTransition('move');
      });
      map.on('zoomstart', e => {
        this.setFastTransition('zoom');
      });
      map.on('zoomend', e => {
        this.unsetFastTransition('zoom');
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
