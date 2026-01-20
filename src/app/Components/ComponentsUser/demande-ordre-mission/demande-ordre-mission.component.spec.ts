import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DemandeOrdreMissionComponent } from './demande-ordre-mission.component';

describe('DemandeOrdreMissionComponent', () => {
  let component: DemandeOrdreMissionComponent;
  let fixture: ComponentFixture<DemandeOrdreMissionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandeOrdreMissionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DemandeOrdreMissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
