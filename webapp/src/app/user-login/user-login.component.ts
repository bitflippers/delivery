import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';

export interface DialogData {
  username: string;
  password: string;
}

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit {

  constructor(public dialog: MatDialog) { }

  ngOnInit() {
  }

  openLogin() {
    const dialogRef = this.dialog.open(LoginDialogComponent, {
      width: '250px',
      data: <DialogData>{ username: 'User', password: 'Pass' }
    });
    dialogRef.afterClosed().subscribe(r => {
      console.log('dialog closed', r);
    });
  }

}

@Component({
  selector: 'app-dialog-login',
  templateUrl: 'dialog-login.component.html',
  styleUrls: [ './user-login.component.css']
})
export class LoginDialogComponent {
  constructor(public dialogRef: MatDialogRef<LoginDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  close() {
    this.dialogRef.close();
  }
}
