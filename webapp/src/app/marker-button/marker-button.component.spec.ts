import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MarkerButtonComponent } from './marker-button.component';

describe('MarkerButtonComponent', () => {
  let component: MarkerButtonComponent;
  let fixture: ComponentFixture<MarkerButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MarkerButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarkerButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
