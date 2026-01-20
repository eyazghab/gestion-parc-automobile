import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MesCarburantsComponent } from './mes-carburants.component';

describe('MesCarburantsComponent', () => {
  let component: MesCarburantsComponent;
  let fixture: ComponentFixture<MesCarburantsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MesCarburantsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MesCarburantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
