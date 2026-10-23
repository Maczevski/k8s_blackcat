import { Component, inject, TemplateRef, ViewChild, } from '@angular/core';
import { MdbFormsModule } from 'mdb-angular-ui-kit/forms';
import { Venda } from '../../../models/venda';
import { ProdutosListComponent } from '../../produtos/produtos-list/produtos-list.component';
import { MdbModalModule, MdbModalRef, MdbModalService} from 'mdb-angular-ui-kit/modal';
import { VendaService } from '../../../services/venda.service';
import { FormsModule } from '@angular/forms';
import { Produto } from '../../../models/produto';
import { ProdutoVenda } from '../../../models/produto-venda';
import { AlertService } from '../../../services/alert.service';
import { MdbDropdownModule } from 'mdb-angular-ui-kit/dropdown';
import { CommonModule } from '@angular/common';
import { LoginService } from '../../../auth/login.service';

@Component({
  selector: 'app-venda',
  standalone: true,
  imports: [
    MdbDropdownModule,
    MdbFormsModule,
    ProdutosListComponent,
    MdbModalModule,
    FormsModule,
    CommonModule
  ],
  templateUrl: './venda.component.html',
  styleUrl: './venda.component.scss',
})
export class VendaComponent {
  vendaValida = false;

  modalService = inject(MdbModalService);
  @ViewChild('userList') userList!: TemplateRef<unknown>;
  modalRef!: MdbModalRef<unknown>;

  alertService = inject(AlertService);

  loginService = inject(LoginService);

  venda: Venda = new Venda();

  vendaService = inject(VendaService);

  nomeSearch = '';

  showResult = false;

  totalVendaText = 'R$ 0,00';

  userName = 'Nenhum usuário';

  constructor() {
    this.venda.produtosVenda = [];
    this.venda.desconto = 0;
    this.venda.usuario = this.loginService.getUsuarioLogado();
  }

  onSearch() {
    this.showResult = this.nomeSearch.length > 0;
  }

  adicionarProduto() {
    this.showResult = true;
  }

  save() {
    this.vendaService.save(this.venda).subscribe({
      next: msg => {
        this.venda = new Venda();
        this.venda.produtosVenda = [];
        this.venda.desconto = 0;
        this.alertService.showToast(msg, 'success');
        this.setFormaPagamento("");
        this.totalVendaText = "R$ 0,00"
      },
      error: erro => {
        console.log(erro);
        this.alertService.showErrorToast(erro);
      },
    });
  }

  closeList() {
    this.showResult = false;
    this.nomeSearch = '';
  }

  addProduct(produto: Produto) {
    if (produto) {
      let encontrado = false;
      for (const prodVenda of this.venda.produtosVenda) {
        if (prodVenda.produto.id === produto.id) {
          encontrado = true;
        }
      }
      if (!encontrado) {
        const prodVenda = new ProdutoVenda();
        prodVenda.produto = produto;
        prodVenda.quantidade = 1;
        this.venda.produtosVenda.push(prodVenda);
        this.calcularTotal();
      } else {
        this.alertService.showToast(`O produto ${produto.nome} já está presente na venda`, "warning")
      }
    } else {
      this.alertService.showAlert("Produto nulo", "error")
    }
    this.showResult = false;
    this.verificarVenda();
  }

  removerProdVenda(prodVenda: ProdutoVenda) {
    const index = this.venda.produtosVenda.findIndex((x) => {
      return x.id == prodVenda.id;
    });
    this.venda.produtosVenda.splice(index, 1);
    this.verificarVenda();
  }

  calcularTotal() {
    if (this.venda.desconto <= 100) {
      let total = 0;
      const prodsVenda = this.venda.produtosVenda;
      if (prodsVenda.length > 0) {
        for (const prodVenda of prodsVenda) {
          total += prodVenda.produto.preco * prodVenda.quantidade;
        }
        if (this.venda.desconto > 0) {
          total -= total * (this.venda.desconto / 100);
        }
        this.totalVendaText = `R$ ${total.toFixed(2)}`;
      }
      this.verificarVenda();
    } else {
      this.alertService.showToast("O desconto deve ser menor que 100%", "warning");
      this.verificarVenda();
    }
  }

  verificarVenda() {
    this.vendaValida = Boolean(
      this.venda.usuario != null &&
      this.venda.produtosVenda &&
      this.venda.produtosVenda.length > 0 &&
      this.venda.formaPagamento &&
      this.venda.formaPagamento !== 'Forma de pagamento' &&
      this.venda.desconto <= 100
    );
  }

  setFormaPagamento(formaPagamento: string) {
    this.venda.formaPagamento = formaPagamento;
    this.verificarVenda();
  }

  addDesconto() {
    if (this.venda.desconto < 100) {
      this.venda.desconto++;
      this.calcularTotal();
    }
  }

  subtrairDesconto() {
    if (this.venda.desconto > 0) {
      this.venda.desconto--;
      this.calcularTotal();
    }
  }

  addQuantidade(prodVenda: ProdutoVenda) {
    prodVenda.quantidade++;
    this.calcularTotal();
    this.verificarVenda();
  }

  subtrairQuantidade(prodVenda: ProdutoVenda) {
    if (prodVenda.quantidade > 0) {
      prodVenda.quantidade--;
      this.calcularTotal();
      this.verificarVenda();
    }
    if (prodVenda.quantidade == 0) {
      this.removerProdVenda(prodVenda);
    }
  }
}
