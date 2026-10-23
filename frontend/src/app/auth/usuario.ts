import { Venda } from "../models/venda";

export class Usuario {
    id!: number;
    nome!: string;
    login!: string;
    senha!: string;
    role!: 'GESTOR' | 'FUNCIONARIO';
    ativo!: boolean;
    vendas!: Venda[];
}