import { Produto } from "./produto";
import { Venda } from "./venda";

export class ProdutoVenda {

    id!: number;
    quantidade!: number;

    produto!: Produto;
    venda!: Venda;
}
