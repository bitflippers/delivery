import * as L from 'leaflet';
import 'leaflet-rotatedmarker';
import {Observable} from 'rxjs';

export class Marker {
  marker: L.Marker;

  private transitionLock = {};
  private fastInterval = 20;

  public events: Observable<any>;
  private obs: any;

  constructor(
    public pos: L.LatLng,
    public icon: L.Icon,
    public interval = 1000,
    public layer: any = null,
    public rotationAngle = 0,
    public options = {}
  ) {
    const o = <any>Object.assign({}, {
      icon: this.icon,
      draggable: true,
      rotationAngle: this.rotationAngle
    }, options);

    this.marker = L.marker(pos, o);

    this.events = new Observable(observable => {
      // console.log('Generate observable');
      this.obs = observable;
    });
    this.events.subscribe();

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
    // console.log('set fasttransition', name, this,this.transitionLock);
    this.setTransition(this.fastInterval);
  }

  unsetFastTransition(name) {
    delete this.transitionLock[name];
    // console.log('unset fasttransition', name, this,this.transitionLock);
    if (Object.keys(this.transitionLock).length === 0) {
      this.setTransition();
    }
  }

  execIf(fn, cb) {
    if (fn()) {
      return cb();
    } else {
      setTimeout(() => this.execIf(fn, cb), 50);
    }
  }

  removeZoomTransition(map: L.Map, fastInterval = this.fastInterval) {
    if (L.DomUtil.TRANSITION) {
      this.marker.on('dragstart', e => {
        this.setFastTransition('drag');
        if (this.obs) {
          this.obs.next({ type: 'dragstart', msg: e });
        }
      });
      this.marker.on('movestart', e => {
        this.setFastTransition('move');
        if (this.obs) {
          this.obs.next({ type: 'movestart', msg: e });
        }
      });
      this.marker.on('dragend', e => {
        this.unsetFastTransition('drag');
        if (this.obs) {
          this.obs.next({ type: 'dragend', msg: e });
        }
      });
      this.marker.on('moveend', e => {
        this.unsetFastTransition('move');
        if (this.obs) {
          this.obs.next({ type: 'moveend', msg: e });
        }
      });
      this.marker.on('drag', e => {
        if (this.obs) {
          console.log('drag queen', e);
          this.obs.next({ type: 'drag', msg: e });
        }
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
      // console.log('Transition', interval);
      this.execIf(() => (<any>this.marker)._icon,
        () => (<any>this.marker)._icon.style[L.DomUtil.TRANSITION] = ('all ' + interval + 'ms linear')
      );
    }
  }

  close() {
    this.marker.removeFrom(this.layer);
    this.obs.complete();
  }
}
