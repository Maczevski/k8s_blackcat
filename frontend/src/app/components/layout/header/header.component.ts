import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MdbCollapseModule } from 'mdb-angular-ui-kit/collapse';
import { AlertService } from '../../../services/alert.service';
import { LoginService } from '../../../auth/login.service';
import { Usuario } from '../../../auth/usuario';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MdbCollapseModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  router = inject(Router);
  alertService = inject(AlertService);
  loginService = inject(LoginService); //auth 
  currentRoute = this.router.url;

  usuario!: Usuario;

  constructor() {
    this.usuario = this.loginService.getUsuarioLogado();
  }


  // Método para verificar se o usuário é um admin
  isAdmin(): boolean {
    return localStorage.getItem('userRole') === 'admin';
  }
  isFuncionario(): boolean {
    return localStorage.getItem('userRole') === 'funcionario';
  }

  // Método que retorna os itens do menu
  getMenuItems() {
    const menuItems = [
      { name: 'Home', link: 'blackcat/dashboard' },
      { name: 'Produtos', link: 'blackcat/produtos' },
      { name: 'Venda', link: 'blackcat/venda' },
    ];

    // Adiciona o item "Usuários" se o usuário for admin
    if (this.isAdmin()) {
      menuItems.push({ name: 'Usuários', link: 'blackcat/usuarios' });
    }

     // Adiciona o item "Funcionário" se o usuário tiver o papel adequado
  if (this.isFuncionario()) {
    menuItems.push({ name: 'Funcionário', link: 'funcionario' });
  }

    return menuItems;
  }
  
 
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
