import { Component } from '@angular/core';
import { Top } from './top/top';
import { LogoBar } from './logo-bar/logo-bar';
import { NavMenu } from './nav-menu/nav-menu';

@Component({
  selector: 'app-header',
  imports: [Top, LogoBar, NavMenu],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {}
