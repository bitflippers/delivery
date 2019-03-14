import {Component, OnInit} from '@angular/core';
import {MessagingService} from '../messaging/messaging.service';
import {iconList} from '../ressources/iconConvertor';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  userList = [];

  constructor(public msg: MessagingService) {
  }

  trackByFn(index: number, data) {
    return data.nickname;
  }

  ngOnInit() {
    this.msg.users.subscribe(data => {
      // console.log('I receive users', data);
      this.userList = data.data.map(n => {
        n.icon = iconList[n.slot.identifier];
        n.nickname = n.username;
        return n;
      }).sort((a, b) => a.nickname < b.nickname);
    });
  }

}
