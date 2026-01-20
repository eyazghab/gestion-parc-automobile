import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuivisComponent } from './suivis.component';

describe('SuivisComponent', () => {
  let component: SuivisComponent;
  let fixture: ComponentFixture<SuivisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuivisComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuivisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
