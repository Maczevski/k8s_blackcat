import { Component, EventEmitter, inject, Input, Output, TemplateRef, ViewChild } from '@angular/core';
import { UsuarioService } from '../../../services/usuario.service';
import { Router } from '@angular/router';
import { MdbModalModule, MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { MdbModalService } from 'mdb-angular-ui-kit/modal';
import { UsuarioFormComponent } from '../usuario-form/usuario-form.component';
import { AlertService } from '../../../services/alert.service';
import { Usuario } from '../../../auth/usuario';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [MdbModalModule, UsuarioFormComponent],
  templateUrl: './usuario-list.component.html',
  styleUrls: ['./usuario-list.component.scss'],
})
export class UsuarioListComponent {

  @Input() modoAddUser = false;
  @Output() retorno = new EventEmitter<unknown>;

  //VARIAVEIS DE CONTEXTO
  lista: Usuario[] = [];
  usuarioEdit: Usuario = new Usuario();
  ativo = true;

  //SERVICES
  usuarioService = inject(UsuarioService);
  router = inject(Router);

  //ELEMENTOS DE MODAL
  modalService = inject(MdbModalService);
  @ViewChild("modalUsuarioForm") modalUsuarioForm!: TemplateRef<unknown>;
  modalRef!: MdbModalRef<unknown>;
  

  constructor( private alertService: AlertService) {
    this.findAll();
  }


  findAll(ativo = true) {
    this.usuarioService.findAll(ativo).subscribe({
      next: (lista) => {
        this.lista = lista;
      },
      error: (erro) => {
        this.alertService.showErrorToast(erro); // Alerta de erro
      },
    });
  }


  findById(id: number) {
    this.usuarioService.findById(id).subscribe({
      next: (usuario) => {
        this.lista = [usuario];
      },
      error: (erro) => {
        this.alertService.showErrorToast(erro); // Alerta de erro
      },
    });
  }


  desativarUsuario(usuario: Usuario) {
    this.alertService.showConfirmDialog(
      'Atenção',
      `Tem certeza que deseja desativar o usuário ${usuario.login}?`,
      'Sim',
      'warning'
    ).then((result) => {
      if (result.isConfirmed) {
        this.usuarioService.desativarUsuario(usuario.id).subscribe({
          next: mensagem => {
            this.alertService.showToast(mensagem, 'success'); // Toast de sucesso
            this.findAll();
          },
          error: erro => {
            this.alertService.showErrorToast(erro); // Alerta de erro
          },
        });
      }
    });
  }
  

  ativarUsuario(usuario: Usuario) {
    this.alertService.showConfirmDialog(
      'Atenção',
      `Tem certeza que deseja ativar o usuário ${usuario.login}?`,
      'Sim',
      'warning'
    ).then((result) => {
      if (result.isConfirmed) {
        this.usuarioService.ativarUsuario(usuario.id).subscribe({
          next: mensagem => {
            this.alertService.showToast(mensagem, 'success'); // Toast de sucesso
            this.findAll(false);
          },
          error: erro => {
            this.alertService.showErrorToast(erro); // Alerta de erro
          },
        });
      }
    });
  }


  new() {
    this.usuarioEdit = new Usuario();
    this.modalRef = this.modalService.open(this.modalUsuarioForm);
  } 
  
  edit( usuario: Usuario) {
    this.usuarioEdit = Object.assign ({}, usuario); //clonando pra evitar ref de obj
    this.modalRef = this.modalService.open(this.modalUsuarioForm);
  }

  retornoForm() {
    this.modalRef.close();
    this.findAll();
  }

  retornarUsuario(user : Usuario){
    this.retorno.emit(user);
  }

}
