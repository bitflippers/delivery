import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DisasterComponent } from './disaster.component';

describe('DisasterComponent', () => {
  let component: DisasterComponent;
  let fixture: ComponentFixture<DisasterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DisasterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DisasterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
