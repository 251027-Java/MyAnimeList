import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Catch } from './catch';

describe('Catch', () => {
  let component: Catch;
  let fixture: ComponentFixture<Catch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Catch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Catch);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
