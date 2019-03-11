import {Component, OnInit} from '@angular/core';
import {SpinnerServiceService} from '../spinner-service/spinner-service.service';

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.css']
})
export class SpinnerComponent implements OnInit {

  constructor(public status: SpinnerServiceService) {
  }

  ngOnInit() {
  }
}
