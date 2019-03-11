import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {FormsModule} from '@angular/forms';

import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModuleModule} from './material-module/material-module.module';
import {LayoutModule} from '@angular/cdk/layout';
import {WorldMapComponent} from './world-map/world-map.component';
import {UserLoginComponent, LoginDialogComponent} from './user-login/user-login.component';
import {UserUiComponent} from './user-ui/user-ui.component';
import {SpinnerComponent} from './spinner/spinner.component';
import {UserListComponent} from './user-list/user-list.component';
import {MarkerButtonComponent, MarkerDialogComponent} from './marker-button/marker-button.component';
import {FooterComponent} from './footer/footer.component';
import {HomeButtonComponent} from './home-button/home-button.component';
import {DisasterComponent} from './disaster/disaster.component';

@NgModule({
  declarations: [
    AppComponent,
    WorldMapComponent,
    UserLoginComponent,
    LoginDialogComponent,
    UserUiComponent,
    SpinnerComponent,
    UserListComponent,
    MarkerButtonComponent,
    MarkerDialogComponent,
    FooterComponent,
    HomeButtonComponent,
    DisasterComponent,
  ],
  entryComponents: [
    MarkerDialogComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MaterialModuleModule,
    LayoutModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
