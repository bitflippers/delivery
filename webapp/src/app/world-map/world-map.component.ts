import {Component, OnInit} from '@angular/core';
import {World} from './map';
import {Sat} from './satellites';
import {MessagingService} from '../messaging/messaging.service';
import {iconList} from '../ressources/iconConvertor';
import {Markers} from './userMarkers';
import {Planes} from './planes';

@Component({
  selector: 'app-world-map',
  templateUrl: './world-map.component.html',
  styleUrls: ['./world-map.component.css']
})
export class WorldMapComponent implements OnInit {

  constructor(public msg: MessagingService) {
  }

  ngOnInit() {
    // Generic INIT
    World.init();
    Sat.init();
    Markers.init();
    Planes.init();

    this.msg.markers.subscribe(data => {
//      console.log('I receive markers', data);
      data.forEach(n => {
        n.data.icon = 'assets/' + iconList[n.data.slot.identifier] + '.svg';
//        console.log("state of a marker: ",n.state);
        switch (<string>n.state) {
          case 'delete':
            Markers.deleteMarker(n.data);
            break;
          case 'update':
            Markers.updateMarker(n.data);
            break;
          default:
            const m = Markers.updateMarker(n.data);
            //console.log('Subscribe to marker');
            m.marker.events.subscribe(e => {
              console.log('Drag king', e);
              if (e.type === 'drag') {
                this.msg.emit('moveonemarker', {
                  markerid: m.markerID,
                  e: e
                });
              }
            });
        }
      });
    });

    this.msg.planes.subscribe(data => {
      // console.log('Planes', data);
      data.forEach(plane => Planes.updatePlane(plane.data));
    });

    this.msg.satellites.subscribe(data => {
//      console.log('Satellites', data);
      data.forEach(satellite => {
        // console.log('Satellite', satellite);
        Object.values(satellite.data.mapBeam).forEach(beam => {
          // console.log('beam', beam);
          Sat.addBeam(beam, satellite.data);
        });
      });
    });
  }

}
