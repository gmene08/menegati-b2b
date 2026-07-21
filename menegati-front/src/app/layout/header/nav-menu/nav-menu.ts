import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NAV_CATEGORIES } from '../nav-categories';

@Component({
  selector: 'app-nav-menu',
  imports: [RouterLink],
  templateUrl: './nav-menu.html',
  styleUrl: './nav-menu.css',
})
export class NavMenu {
  protected readonly categories = NAV_CATEGORIES;
}
