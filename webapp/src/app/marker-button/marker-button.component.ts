import {Component, Inject, OnInit} from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {iconList} from '../ressources/iconConvertor';

export interface DialogData {
  username: string;
  password: string;
}

@Component({
  selector: 'app-marker-button',
  templateUrl: './marker-button.component.html',
  styleUrls: ['./marker-button.component.css']
})
export class MarkerButtonComponent implements OnInit {

  constructor(public dialog: MatDialog) {
  }

  ngOnInit() {
  }

  openLogin() {
    const dialogRef = this.dialog.open(MarkerDialogComponent, {
      width: '40vw',
      height: '65vw',
      minWidth: '370pt',
      minHeight: '340pt',
      data: {username: 'User', password: 'Pass'} as DialogData
    });
    dialogRef.afterClosed().subscribe(r => {
      // console.log('dialog closed', r);
    });
  }

}

@Component({
  selector: 'app-dialog-login',
  templateUrl: './dialog-login.component.html',
  styleUrls: ['./marker-button.component.css']
})
export class MarkerDialogComponent {

  iconList = [
    'contact_support', 'favorite', 'arrow_downward', 'assistant_photo', 'flash_on', 'favorite_border',
    'location_on', 'local_pizza', 'not_listed_location', 'where_to_vote', 'beenhere', 'person_pin_circle'
  ];

  constructor(public dialogRef: MatDialogRef<MarkerDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }

  selectIcon(name) {
    // console.log('Icon selected', name);
    this.dialogRef.close();
  }

  close() {
    this.dialogRef.close();
  }
}
