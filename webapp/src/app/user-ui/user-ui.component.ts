import { Component, OnInit } from '@angular/core';
import { SpinnerServiceService } from '../spinner-service/spinner-service.service';

@Component({
  selector: 'app-user-ui',
  templateUrl: './user-ui.component.html',
  styleUrls: ['./user-ui.component.css']
})
export class UserUiComponent implements OnInit {

  constructor(public spinner: SpinnerServiceService) { }

  ngOnInit() {
    this.spinner.show('User');
    setTimeout(() => this.spinner.hide('User'), 5000);
  }

}
