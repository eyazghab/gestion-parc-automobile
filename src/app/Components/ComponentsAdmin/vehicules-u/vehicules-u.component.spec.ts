import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiculesUComponent } from './vehicules-u.component';

describe('VehiculesUComponent', () => {
  let component: VehiculesUComponent;
  let fixture: ComponentFixture<VehiculesUComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiculesUComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiculesUComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
