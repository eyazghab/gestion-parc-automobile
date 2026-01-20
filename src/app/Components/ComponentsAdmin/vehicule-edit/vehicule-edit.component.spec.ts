import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiculeEditComponent } from './vehicule-edit.component';

describe('VehiculeEditComponent', () => {
  let component: VehiculeEditComponent;
  let fixture: ComponentFixture<VehiculeEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiculeEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiculeEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
