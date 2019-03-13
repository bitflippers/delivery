import {Injectable} from '@angular/core';
import {SpinnerServiceService} from '../spinner-service/spinner-service.service';
import {Observable} from 'rxjs';
import * as io from 'socket.io-client/dist/socket.io';

@Injectable({
  providedIn: 'root'
})
export class MessagingService {

  public socket: any;
  users: Observable<any>;
  markers: Observable<any>;
  beams: Observable<any>;
  login: Observable<any>;
  messages: Observable<any>;
  planes: Observable<any>;
  satellites: Observable<any>;
  private url = 'http://51.38.113.39:3000';

  constructor(public spinner: SpinnerServiceService) {
    // console.log('Messaging instantiated');
    this.spinner.show('join');
    const socket = io(this.url); // Lets connect back to the url
    this.socket = socket;
    socket.on('connect', data => {
      // console.log('Socket connected', data);
      this.spinner.hide('join');
    });
    socket.on('error', err => {
      console.log('Socket error', err);
    });
    this.users = new Observable(usersObs => socket.on('users', msg => usersObs.next(msg)));
    this.markers = new Observable(markersObs => socket.on('markers', msg => markersObs.next(msg)));
    this.beams = new Observable(beamsObs => socket.on('beams', msg => beamsObs.next(msg)));
    this.login = new Observable(loginObs => socket.on('login', msg => loginObs.next(msg)));
    this.messages = new Observable(msgObs => socket.on('messages', msg => msgObs.next(msg)));
    this.planes = new Observable(planesObs => socket.on('planes', msg => planesObs.next(msg)));
    this.satellites = new Observable(satObs => socket.on('satellites', msg => satObs.next(msg)));
  }

  emit(topic, data?: { e: any; markerid: any }) {
    this.socket.emit(topic, data);
  }
}
