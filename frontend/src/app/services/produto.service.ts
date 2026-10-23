import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Produto } from '../models/produto';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProdutoService {
  API = environment.SERVIDOR+'/produto';

  constructor(private http: HttpClient) {}

  // CRUD - Create - save
  saveProduto(produto: Produto): Observable<string> {
    return this.http.post<string>(`${this.API}/save`, produto, {
      responseType: 'text' as 'json',
    });
  }

  // CRUD - Read - findAll
  findAll(ativo: boolean): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.API}/findAll?ativo=${ativo}`);
  }

  // CRUD - Read - findAll
  findByNome(nome: string): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.API}/findByNome?nome=${nome}`);
  }

  // CRUD - Read - findById
  findById(id: number): Observable<Produto> {
    return this.http.get<Produto>(`${this.API}/findById/${id}`);
  }

  // CRUD - Update - update
  updateProduto(id: number, produto: Produto): Observable<string> {
    return this.http.put<string>(`${this.API}/update/${id}`, produto, {
      responseType: 'text' as 'json',
    });
  }

  // CRUD - Delete - delete
  deleteProduto(id: number): Observable<string> {
    return this.http.delete<string>(`${this.API}/delete/${id}`, {
      responseType: 'text' as 'json',
    });
  }

  // CRUD - Desativar - disable
  disableProduto(id: number): Observable<string> {
    return this.http.put<string>(`${this.API}/disable/${id}`, {}, {
      responseType: 'text' as 'json',
    });
  }

  // CRUD - Ativar - enable
  enableProduto(id: number): Observable<string> {
    return this.http.put<string>(`${this.API}/enable/${id}`, {}, {
      responseType: 'text' as 'json',
    });
  }
}
