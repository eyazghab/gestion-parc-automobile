import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminMissionComponent } from './admin-mission.component';

describe('AdminMissionComponent', () => {
  let component: AdminMissionComponent;
  let fixture: ComponentFixture<AdminMissionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminMissionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminMissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
