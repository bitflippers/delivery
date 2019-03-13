import {Component, OnInit} from '@angular/core';
import {MessagingService} from "../messaging/messaging.service";
import {SpinnerServiceService} from "../spinner-service/spinner-service.service";
import {iconList} from "../ressources/iconConvertor";

@Component({
  selector: 'app-navigation',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  saDReMalog = [];
  lastMsg = '';

  constructor(public msg: MessagingService, public spinner: SpinnerServiceService) {
  }

  ngOnInit() {
    const r = /\[/;
    this.msg.messages.subscribe(data => {
      //console.log('Messages', data);
      this.saDReMalog = this.saDReMalog.concat(data.filter(n => r.test(n)).map(n => ({ text: n, color: 'red' }))).splice(-15);
      this.lastMsg = this.saDReMalog[this.saDReMalog.length - 1].text;
    });
  }

}
