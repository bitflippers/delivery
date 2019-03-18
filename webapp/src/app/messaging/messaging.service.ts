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
  moveonemarker: Observable<any>;
  private url = 'http://51.38.113.39:3000';

  constructor(public spinner: SpinnerServiceService) {
    // console.log('Messaging instantiated');
    this.spinner.show('join');
    this.socket = io(this.url); // Lets connect back to the url
    this.socket.on('connect', data => {
      // console.log('Socket connected', data);
      this.spinner.hide('join');
    });
    this.socket.on('error', err => {
      console.log('Socket error', err);
    });
    this.users = new Observable(usersObs => this.socket.on('users', msg => usersObs.next(msg)));
    this.markers = new Observable(markersObs => this.socket.on('markers', msg => markersObs.next(msg)));
    this.beams = new Observable(beamsObs => this.socket.on('beams', msg => beamsObs.next(msg)));
    this.login = new Observable(loginObs => this.socket.on('login', msg => loginObs.next(msg)));
    this.messages = new Observable(msgObs => this.socket.on('messages', msg => msgObs.next(msg)));
    this.planes = new Observable(planesObs => this.socket.on('planes', msg => planesObs.next(msg)));
    this.satellites = new Observable(satObs => this.socket.on('satellites', msg => satObs.next(msg)));
    this.moveonemarker = new Observable(mmObs => this.socket.on('moveonemarker', msg => mmObs.next(msg)));
  }

  emit(topic, data?: { e: any; markerid: any }) {
    // console.log('emit', topic, data);
    // this.socket.emit(topic, data);
    this.socket.emit(topic, data);
  }
}
