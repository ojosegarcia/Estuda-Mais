import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrls: ['./app.css'],   // Corrigido de styleUrl para styleUrls
  standalone: true,
  imports: [RouterOutlet]      // Para usar <router-outlet> no template
})
export class App {
  title = 'angular-app';
}
