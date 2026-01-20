import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiculesAComponent } from './vehicules-a.component';

describe('VehiculesAComponent', () => {
  let component: VehiculesAComponent;
  let fixture: ComponentFixture<VehiculesAComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiculesAComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiculesAComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
