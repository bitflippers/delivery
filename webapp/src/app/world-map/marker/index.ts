import * as L from 'leaflet';
import 'leaflet-rotatedmarker';
import {Observable} from 'rxjs';
import {World} from '../map';

let markerID = 0;
const markerList = {};

// Performance optimization
World.cbOnMap(map => {
  map.map.on('movestart', e => {
    Object.values(markerList).forEach(m => (<any>m).setFastTransition('mapmove'));
  });

  map.map.on('moveend', e => {
    Object.values(markerList).forEach(m => (<any>m).unsetFastTransition('mapmove'));
  });

  map.map.on('zoomstart', e => {
    Object.values(markerList).forEach(m => (<any>m).setFastTransition('mapzoom'));
  });

  map.map.on('zoomend', e => {
    Object.values(markerList).forEach(m => (<any>m).unsetFastTransition('mapzoom'));
  });
});


export class Marker {
  marker: L.Marker;

  private transitionLock = {};
  private fastInterval = 20;

  public events: Observable<any>;
  private obs: any;
  id = markerID;

  constructor(
    public pos: L.LatLng,
    public icon: L.Icon,
    public interval = 1000,
    public layer: any = null,
    public rotationAngle = 0,
    public options = {}
  ) {
    this.id = markerID++;

    markerList[this.id] = this;

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

    this.setTransition(this.fastInterval); // The first transition is always the faster

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

  removeZoomTransition(fastInterval = this.fastInterval) {
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
        //console.log('drag queen', e);
        this.obs.next({ type: 'drag', msg: e });
      }
    });
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
    delete markerList[this.id];
  }
}
