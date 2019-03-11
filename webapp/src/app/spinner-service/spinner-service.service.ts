import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SpinnerServiceService {

  public status = false;

  private sliders = {};

  constructor() {
  }

  show(name) {
    this.sliders[name] = true;
    this.status = true;
  }

  hide(name) {
    delete (this.sliders[name]);
    if (Object.keys(this.sliders).length < 1) {
      this.status = false;
    }
  }
}
