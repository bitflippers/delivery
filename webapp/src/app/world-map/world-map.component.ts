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
        n.icon = 'assets/' + iconList[n.slot.identifier] + '.svg';
//        console.log("state of a marker: ",n.state);
        if (n.state === 'delete') {
          Markers.deleteMarker(n);
        } else {
          Markers.updateMarker(n);
        }
      });
    });

    this.msg.planes.subscribe(data => {
      // console.log('Planes', data);
      data.forEach(plane => Planes.updatePlane(plane));
    });

    this.msg.satellites.subscribe(data => {
//      console.log('Satellites', data);
      data.forEach(satellite => {
        console.log('Satellite', satellite);
        Object.values(satellite.mapBeam).forEach(beam => {
          console.log('beam', beam);
          Sat.addBeam(beam);
        });
      });
    });
  }

}
