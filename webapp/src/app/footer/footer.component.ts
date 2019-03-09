import { Component, OnInit } from '@angular/core';
import {MessagingService} from "../messaging/messaging.service";
import {iconList} from "../ressources/iconConvertor";

@Component({
  selector: 'app-navigation',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  saDReMalog = [
    {
      "text": "vkjenj",
    },
    {
      "text": "vkjenj1",
    },
    {
      "text": "vkjenj2",
    },
    {
      "text": "vkjenj3",
    },
    {
      "text": "vkjenj3",
    },
    {
      "text": "vkjenj3",
    },
    {
      "text": "vkjenj3",
    },
    {
      "text": "vkjenj3",
    },
    {
      "text": "vkjenj3",
    }
  ];

  constructor(public msg: MessagingService) { }

  ngOnInit() {
    this.msg.users.subscribe( data => {
      console.log('I receive footer log', data);
      // this.saDReMalog = data;
    });
  }

}
