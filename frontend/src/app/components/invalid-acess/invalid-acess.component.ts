import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../auth/login.service';
import { AlertService } from '../../services/alert.service';

@Component({
  selector: 'app-invalid-acess',
  standalone: true,
  imports: [],
  templateUrl: './invalid-acess.component.html',
  styleUrl: './invalid-acess.component.scss',
})
export class InvalidAcessComponent {
  router = inject(Router);
  alertService = inject(AlertService);
  loginService = inject(LoginService); //auth

  confirmLogout(event: Event) {
    event.preventDefault();

    this.alertService
      .showConfirmDialog(
        'Tem certeza que deseja sair?',
        'Voce ser redirecionando para tela de login',
        'Sim, quero sair',
        'warning'
      )
      .then((result) => {
        if (result.isConfirmed) {
          // Remover o papel do usuário do localStorage antes de redirecionar
          localStorage.removeItem('userRole');
          // Redirecionar para a página de login quando confirmado
          this.router.navigate(['/login']);
        }
      });
  }
}
