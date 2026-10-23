import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component'; // Ajuste o caminho conforme necessário
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-principal',
  standalone: true,
  imports: [HeaderComponent, RouterModule],
  templateUrl: './principal.component.html',
  styleUrls: ['./principal.component.scss']
})
export class PrincipalComponent {}
