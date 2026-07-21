import { Component, inject, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { Header } from './layout/header/header';
import { Footer } from './layout/footer/footer';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  // Injetamos o Router para saber em que página estamos
  private router = inject(Router);

  // Variável que vai controlar se mostramos a "moldura" do site B2C
  mostrarHeaderPadrao = true;

  ngOnInit() {
    // Ficamos à escuta de cada vez que o utilizador muda de página
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        const urlAtual = event.urlAfterRedirects || event.url;

        // Se a URL incluir '/dashboard' (ou o nome que deu à sua rota), escondemos o Header e o Footer!
        // Se a sua rota se chamar '/painel', basta trocar a palavra abaixo.
        if (urlAtual.includes('/painel-revendedora')) {
          this.mostrarHeaderPadrao = false;
        } else {
          this.mostrarHeaderPadrao = true;
        }
      });
  }
}
