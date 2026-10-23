import { Component, ViewChild, TemplateRef, Input, inject, Output, EventEmitter, SimpleChanges, OnChanges } from '@angular/core';
import { Produto } from '../../../models/produto';
import { ProdutoService } from '../../../services/produto.service';
import { Router } from '@angular/router';
import { MdbModalModule, MdbModalRef, MdbModalService } from 'mdb-angular-ui-kit/modal';
import { ProdutosFormComponent } from '../produtos-form/produtos-form.component';
import { AlertService } from '../../../services/alert.service';


@Component({
  selector: 'app-produtos-list',
  standalone: true,
  imports:[MdbModalModule, ProdutosFormComponent],
  templateUrl: './produtos-list.component.html',
  styleUrls: ['./produtos-list.component.scss'],
})
export class ProdutosListComponent implements OnChanges {

  @Input() nomePesquisa = '';
  @Input() modoAddProduct  = false;
  @Output() retorno = new EventEmitter();

  alertService = inject(AlertService);

  lista: Produto[] = [];
  produtoEdit: Produto = new Produto(); 
  ativo = true;
  @ViewChild("modalProdutoDetalhe") modalProdutoDetalhe!: TemplateRef<unknown>;
  modalRef!: MdbModalRef<unknown>;
  produtoService = inject(ProdutoService);
  modalService = inject(MdbModalService);
  router = inject(Router);
  constructor() {
      this.findAllAtivos();
      this.findAll();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['nomePesquisa']) {
      if (this.nomePesquisa) {
        this.findByNome();
      } else {
        this.findAllAtivos();
      }
    }
  }
  findByNome() {
    console.log(this.nomePesquisa)
    this.produtoService.findByNome(this.nomePesquisa).subscribe({
      next: lista => {
        this.lista = lista;
        this.ordenarProdutosPorId();
      },
      error: erro => {
        this.alertService.showErrorToast(erro);
      },
    });
  }
  findAll(ativo = true) {
    this.produtoService.findAll(ativo).subscribe({
      next: lista => {
        this.lista = lista;
      },
      error: erro => {
        this.alertService.showErrorToast(erro);
      },
    });
  }
  findAllAtivos() {
    this.produtoService.findAll(true).subscribe({
      next: lista => {
        this.lista = lista;
        this.ordenarProdutosPorId();
      },
      error: erro => {
        this.alertService.showErrorToast(erro);
      },
    });
  }
  ativarProduto(produto: Produto) {
    this.alertService.showConfirmDialog ("Atenção", `Tem certeza que deseja ativar o produto ${produto.nome}?`,"Sim", "warning").then((result) => {
      if (result.isConfirmed) {
        this.produtoService.enableProduto(produto.id).subscribe({
          next: mensagem => {
            this.alertService.showToast(mensagem, "success");
            this.findAll(false);
          },
          error: erro => {
            this.alertService.showErrorToast(erro);
          },
        });
      }
    });
  }
  ordenarProdutosPorId() {
    this.lista.sort((a, b) => b.id - a.id);
  }

  new() {
    this.produtoEdit = new Produto(); 
    this.modalRef = this.modalService.open(this.modalProdutoDetalhe);
  }
  
  edit(produto: Produto) {
    this.produtoEdit = Object.assign({}, produto); 
    this.modalRef = this.modalService.open(this.modalProdutoDetalhe);
  }
  
  retornoDetalhe() {
    this.findAllAtivos();
    this.modalRef.close();
  }
  
  desativarProduto(produto: Produto) {
    this.alertService.showConfirmDialog("Atenção!", `Tem certeza que deseja desativar o produto ${produto.nome}?`, "Sim", "warning").then((result) => {
      if (result.isConfirmed) {
        this.produtoService.disableProduto(produto.id).subscribe({
          next: (mensagem) => {
            this.alertService.showToast(mensagem, "success");
            this.findAllAtivos();
          },
          error: (erro) => {
            this.alertService.showErrorToast(erro);
          },
        });
      }
    });
  }
  retornarProduto(produto : Produto){
    this.retorno.emit(produto);
  }
}


