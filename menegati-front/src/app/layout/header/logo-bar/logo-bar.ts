import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NAV_CATEGORIES } from '../nav-categories';

@Component({
  selector: 'app-logo-bar',
  imports: [RouterLink],
  templateUrl: './logo-bar.html',
  styleUrl: './logo-bar.css',
})
export class LogoBar {
  protected readonly categories = NAV_CATEGORIES;
  protected readonly menuOpen = signal(false);

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }
}
