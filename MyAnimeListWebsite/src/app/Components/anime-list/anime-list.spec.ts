import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AnimeList } from './anime-list';

describe('AnimeList', () => {
  let component: AnimeList;
  let fixture: ComponentFixture<AnimeList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnimeList, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnimeList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
