import {Component, OnInit, ViewChild} from '@angular/core';
import {MessagingService} from '../messaging/messaging.service';
import {SpinnerServiceService} from '../spinner-service/spinner-service.service';
import {iconList} from '../ressources/iconConvertor';


const colorMap = {
  SYSTEM: 'gray',
  SADREMA: 'red',
  EVENTS: 'blue'
};

@Component({
  selector: 'app-navigation',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  saDReMalog = [];
  saDReMaFullLog = [];
  lastMsg = '';
  paused = false;
  pausedBuff = [];

  bufferLines = 200;

  showSystem = true;
  showEvents = true;
  showSadrema = true;

  @ViewChild('scrollEl') scrEl;

  constructor(public msg: MessagingService, public spinner: SpinnerServiceService) {
  }

  trackByData(index: number, data) {
    return data;
  }

  togglePause() {
    this.paused = !this.paused;
    this.toggleFilter();
  }

  toggleSystem() {
    this.showSystem = !this.showSystem;
    this.toggleFilter();
  }

  toggleEvents() {
    this.showEvents = !this.showEvents;
    this.toggleFilter();
  }

  toggleSADREMA() {
    this.showSadrema = !this.showSadrema;
    this.toggleFilter();
  }

  toggleFilter() {
    if (!this.paused) {
      this.saDReMaFullLog = this.saDReMaFullLog.concat(this.pausedBuff).splice(-this.bufferLines);
      this.scrEl.nativeElement.scrollTop = this.scrEl.nativeElement.scrollHeight;
    }
    this.saDReMalog = this.saDReMaFullLog
      .filter(n => this.showSystem || (!n.text.match(/\[SYSTEM\]/)))
      .filter(n => this.showEvents || (!n.text.match(/\[EVENTS\]/)))
      .filter(n => this.showSadrema || (!n.text.match(/\[SADREMA\]/)));
    if (this.saDReMalog.length > 0) {
      this.lastMsg = this.saDReMalog[this.saDReMalog.length - 1].text;
    }
  }

  ngOnInit() {
    const r = /\[/;
    this.msg.messages.subscribe(data => {
      // console.log('Messages', data);
      for (const line of data.filter(n => r.test(n)).map(n => ({ text: n, color: colorMap[n.match(/\[(\w+)\]/)[1]] }))) {
        if (this.saDReMaFullLog.concat(this.pausedBuff).filter(n => n.text === line.text).length > 0) {
          continue;
        }
        this.pausedBuff.push(line);
        if (this.pausedBuff.length > this.bufferLines) {
          this.pausedBuff = this.pausedBuff.slice(-this.bufferLines);
        }
      }
      this.toggleFilter(); // Check on it
    });
  }

}

