import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BonCarburantComponent } from './bon-carburant.component';

describe('BonCarburantComponent', () => {
  let component: BonCarburantComponent;
  let fixture: ComponentFixture<BonCarburantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BonCarburantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BonCarburantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
