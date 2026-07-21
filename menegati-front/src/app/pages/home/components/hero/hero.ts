import { Component } from '@angular/core';
import { PresentationArticle } from '../presentation-article/presentation-article';
import { PresentationVideo } from '../presentation-video/presentation-video';

@Component({
  selector: 'app-hero',
  imports: [PresentationArticle, PresentationVideo],
  templateUrl: './hero.html',
  styleUrl: './hero.css',
})
export class Hero {}
