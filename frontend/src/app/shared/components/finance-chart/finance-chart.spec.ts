import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FinanceChart } from './finance-chart';

describe('FinanceChart', () => {
  let component: FinanceChart;
  let fixture: ComponentFixture<FinanceChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinanceChart],
    }).compileComponents();

    fixture = TestBed.createComponent(FinanceChart);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
