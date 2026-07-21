import { Component } from '@angular/core';
import { Hero } from './components/hero/hero';
import { CollectionsTeaser } from './components/collections-teaser/collections-teaser';

@Component({
  selector: 'app-home',
  imports: [Hero, CollectionsTeaser],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
