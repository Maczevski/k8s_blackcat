import { Usuario } from "../auth/usuario";
import { ProdutoVenda } from "./produto-venda";

export class Venda{

    id!: number;
    total!: number;
    data!: Date;
    desconto!: number;
    formaPagamento!: string;

    usuario!: Usuario;

	produtosVenda!: ProdutoVenda[];


  
}