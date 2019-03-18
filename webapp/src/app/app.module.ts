import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModuleModule } from './material-module/material-module.module';
import { LayoutModule } from '@angular/cdk/layout';
import { WorldMapComponent } from './world-map/world-map.component';
import { UserUiComponent } from './user-ui/user-ui.component';
import { UserLoginComponent, LoginDialogComponent } from './user-login/user-login.component';
import { UserListComponent } from './user-list/user-list.component';
import { SpinnerComponent } from './spinner/spinner.component';
import { MarkerButtonComponent, MarkerDialogComponent } from './marker-button/marker-button.component';
import { HomeButtonComponent } from './home-button/home-button.component';
import { FooterComponent } from './footer/footer.component';
import { DisasterComponent } from './disaster/disaster.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { environment } from '../environments/environment';

@NgModule({
  declarations: [
    AppComponent,
    WorldMapComponent,
    UserUiComponent,
    UserLoginComponent,
    LoginDialogComponent,
    UserListComponent,
    SpinnerComponent,
    MarkerButtonComponent,
    MarkerDialogComponent,
    HomeButtonComponent,
    FooterComponent,
    DisasterComponent
  ],
  entryComponents: [
    MarkerDialogComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModuleModule,
    DragDropModule,
    LayoutModule,
    FormsModule,
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
