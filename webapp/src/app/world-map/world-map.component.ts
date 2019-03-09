import { Component, OnInit } from '@angular/core';
import { map } from './map';
import { Sat } from './satellites';
import { MessagingService } from '../messaging/messaging.service';
import { iconList } from "../ressources/iconConvertor";
import { Markers} from "./userMarkers";
import { Planes } from "./planes";

@Component({
  selector: 'app-world-map',
  templateUrl: './world-map.component.html',
  styleUrls: ['./world-map.component.css']
})
export class WorldMapComponent implements OnInit {

  constructor(public msg: MessagingService) { }

  ngOnInit() {
    map();
    Sat.init();
    Markers.init();
    Planes.init();
    this.msg.markers.subscribe( data => {
//      console.log('I receive markers', data);
      data.forEach(n => {
        n.icon = "assets/" + iconList[n.slot.identifier] + ".svg";
//        console.log("state of a marker: ",n.state);
        if (n.state == "delete"){
          Markers.deleteMarker(n);
        }
        else {
          Markers.updateMarker(n);
        }
      });
    });
    this.msg.planes.subscribe( data => {
      console.log('Planes', data);
      data.forEach(plane => Planes.updatePlane(plane));
    })
  }

}