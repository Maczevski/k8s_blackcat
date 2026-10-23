import { Component, EventEmitter, inject, Input, Output, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { ProdutoService } from '../../../services/produto.service';
import { Produto } from '../../../models/produto';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { AlertService } from '../../../services/alert.service';

@Component({
  selector: 'app-produto-form',
  standalone: true,
  imports:[ReactiveFormsModule, MdbFormsModule, FormsModule],
  templateUrl: './produtos-form.component.html',
  styleUrls: ['./produtos-form.component.scss'],
})
export class ProdutosFormComponent implements OnChanges {
  @Input() produto!: Produto;
  @Output() retorno = new EventEmitter<Produto>();

  alertService = inject (AlertService);

  produtoForm!: FormGroup; 

  constructor(private fb: FormBuilder, private produtoService: ProdutoService) {
    this.createForm();
  }

  private createForm() {
    this.produtoForm = this.fb.group({
      id: [0],
      ativo: [true],
      nome: ['', [Validators.required, Validators.minLength(3)]],
      descricao: [''],
      preco: 0,
    });
  }

  ngOnChanges() {
    if (this.produto) {
      this.produtoForm.patchValue(this.produto);
    }
  }

  onSubmit() {
    if (this.produtoForm.valid) {
      if (this.produtoForm.value.id) {
        this.updateProduto();
      } else {
        this.saveProduto();
      }
    } else {
      Swal.fire({
        title: 'Erro!',
        text: 'Por favor, preencha todos os campos obrigatórios.',
        icon: 'error',
        confirmButtonText: 'Ok',
      });
    }
  }

  private saveProduto() {
    this.produtoService.saveProduto(this.produtoForm.value).subscribe({
      next: (mensagem) => {
        Swal.fire({
          title: 'Sucesso!',
          text: mensagem,
          icon: 'success',
          confirmButtonText: 'Ok',
        });
        this.retorno.emit(this.produtoForm.value);
        this.produtoForm.reset();
      },
      error: (erro) => {
        Swal.fire({
          title: 'Erro!',
          text: erro.error,
          icon: 'error',
          confirmButtonText: 'Ok',
        });
        this.alertService.showErrorToast(erro);;
      },
    });
  }

  private updateProduto() {

    console.log(this.produtoForm.value);

    const id = this.produtoForm.value.id;
    this.produtoService.updateProduto(id, this.produtoForm.value).subscribe({
      next: (mensagem) => {
        Swal.fire({
          title: 'Sucesso!',
          text: mensagem,
          icon: 'success',
          confirmButtonText: 'Ok',
        });
        this.retorno.emit(this.produtoForm.value);
      },
      error: (erro) => {
        console.log (erro);
        Swal.fire({
          title: 'Erro!',
          text: erro.error,
          icon: 'error',
          confirmButtonText: 'Ok',
        });
        this.retorno.emit();
      },
    });
  }
}
