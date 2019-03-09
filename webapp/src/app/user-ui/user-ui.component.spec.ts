import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserUiComponent } from './user-ui.component';

describe('UserUiComponent', () => {
  let component: UserUiComponent;
  let fixture: ComponentFixture<UserUiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserUiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserUiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
