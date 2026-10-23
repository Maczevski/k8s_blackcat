import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { UsuarioService } from '../../../services/usuario.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from '../../../services/alert.service';
import { Usuario } from '../../../auth/usuario';
import { LoginService } from '../../../auth/login.service';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [MdbFormsModule, FormsModule],
  templateUrl: './usuario-form.component.html',
  styleUrls: ['./usuario-form.component.scss']
})
export class UsuarioFormComponent {

  @Input() usuario: Usuario = new Usuario();
  @Output() retorno = new EventEmitter<unknown>();
  router = inject(ActivatedRoute);
  router2 = inject(Router);
  usuarioService = inject(UsuarioService);
  alertService = inject(AlertService);

  loginService = inject(LoginService);

  constructor () { 
   const id = this.router.snapshot.params['id'];
    if (id) {
      this.findById(id);
    }
  }

  findById(id: number) {
    this.usuarioService.findById(id).subscribe({
      next: retorno => {
        this.usuario = retorno;
      },
      error: error => {
        this.alertService.showErrorToast(error);
        this.retorno.emit();
      },
    });
  }

  save() {
    // Verificar se o formulário é válido antes de enviar
    if (!this.isFormValid()) {
      this.showValidationAlerts();
      return;
    }

    if (this.usuario.id > 0) {
      this.usuarioService.updateUsuario(this.usuario, this.usuario.id).subscribe({
        next: response => {
          if(response.token){
            this.loginService.addToken(response.token);
          }
          this.alertService.showToast(response.message, 'success');
          this.retorno.emit(this.usuario);

        },
        error: erro => {
          this.alertService.showErrorToast(erro);
          this.retorno.emit();
        }
      });

    } else {
      this.usuarioService.saveUsuario(this.usuario).subscribe({
        next: mensagem => {
          this.alertService.showToast(mensagem, 'success');
          this.retorno.emit(this.usuario);
        },
        error: erro => { //nessa parte, o toast exibe escurecido por causa do modal
          console.log(erro);
          this.alertService.showErrorToast(erro);
        },
      });
    }
  }

  isFormValid() {
    return this.usuario.nome 
      && this.usuario.login && this.usuario.login.length >= 3 && this.usuario.login.length <= 20
      && this.usuario.senha && this.usuario.senha.length >= 8
      && this.usuario.role;
  }

  showValidationAlerts() {
    if (!this.usuario.nome) {
      this.alertService.showToast('Nome é obrigatório', 'warning');
    }
    if (!this.usuario.login) {
      this.alertService.showToast('Login é obrigatório', 'warning');
    } else if (this.usuario.login.length < 3) {
      this.alertService.showToast('Login deve ter no mínimo 3 caracteres', 'warning');
    } else if (this.usuario.login.length > 20) {
      this.alertService.showToast('Login deve ter no máximo 20 caracteres', 'warning');
    }
    if (!this.usuario.senha) {
      this.alertService.showToast('Senha é obrigatória', 'warning');
    } else if (this.usuario.senha.length < 8) {
      this.alertService.showToast('Senha deve ter no mínimo 8 caracteres', 'warning');
    }
    if (!this.usuario.role) {
      this.alertService.showToast('Role é obrigatória', 'warning');
    }
  }
}
