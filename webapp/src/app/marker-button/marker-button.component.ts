import {Component, Inject, OnInit} from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {iconList} from "../ressources/iconConvertor";

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

  constructor(public dialog: MatDialog) { }

  ngOnInit() {
  }

  openLogin() {
    const dialogRef = this.dialog.open(MarkerDialogComponent, {
      width: '40%',
      height: '65%',
      data: <DialogData>{ username: 'User', password: 'Pass' }
    });
    dialogRef.afterClosed().subscribe(r => {
      console.log('dialog closed', r);
    });
  }

}

@Component({
  selector: 'app-dialog-login',
  templateUrl: './dialog-login.component.html',
  styleUrls: [ './marker-button.component.css']
})
export class MarkerDialogComponent {
  constructor(public dialogRef: MatDialogRef<MarkerDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }

  close() {
    this.dialogRef.close();
  }
}
