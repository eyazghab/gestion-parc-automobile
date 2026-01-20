import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaintenanceDetailComponentComponent } from './maintenance-detail-component.component';

describe('MaintenanceDetailComponentComponent', () => {
  let component: MaintenanceDetailComponentComponent;
  let fixture: ComponentFixture<MaintenanceDetailComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MaintenanceDetailComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MaintenanceDetailComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
