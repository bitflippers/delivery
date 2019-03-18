import {Component, OnInit} from '@angular/core';
import {MessagingService} from '../messaging/messaging.service';
import {iconList} from '../ressources/iconConvertor';
import {SpinnerServiceService} from "../spinner-service/spinner-service.service";

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  userList = [];

  constructor(public msg: MessagingService, public spinner:SpinnerServiceService) {
  }

  trackByFn(index: number, data) {
    return data.nickname;
  }

  ngOnInit() {
    this.spinner.show('MsgUsers');
    this.msg.users.subscribe(data => {
      this.spinner.hide('MsgUsers');
      //console.log('I receive users', data);
      this.userList = data.map(n => {
        n.data.icon = iconList[n.data.slot.identifier];
        n.data.nickname = n.data.username;
        return n;
      }).sort((a, b) => a.data.nickname < b.data.nickname);
    });
  }

}
