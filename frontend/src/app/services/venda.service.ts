import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Venda } from '../models/venda';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VendaService {

  http = inject(HttpClient);
  api = environment.SERVIDOR+"/venda";

  save(venda : Venda) : Observable<string>{
    return this.http.post<string>(this.api+"/save", venda, {responseType: "text" as "json"});
  }
  update(venda : Venda, id : number) : Observable<string>{
    return this.http.put<string>(this.api+`/update/${id}`, venda, {responseType: "text" as "json"});
  }
  delete(id : number) : Observable<string>{
    return this.http.put<string>(this.api+`/delete/${id}`, {responseType: "text" as "json"});
  }

  findAll() : Observable<Venda[]>{
    return this.http.get<Venda[]>(this.api+"/findAll");
  }
  
  
  findById(id : number) : Observable<Venda>{
    return this.http.get<Venda>(`${this.api}/findById/${id}`);
  }

  findByDate(dataInicio : string, dataFim : string) : Observable<Venda[]>{
    const datas = new HttpParams().set("startDate", dataInicio).set("endDate", dataFim);
    return this.http.get<Venda[]>(this.api+"/findByData", {params: datas});
  }

  findByMonth(mes: number, ano: number): Observable<Venda[]> {
    return this.http.get<Venda[]>(`${this.api}/findByMonth/${mes}/${ano}`);
}


  findByUsuario(id : number) : Observable<Venda[]>{
    return this.http.get<Venda[]>(`${this.api}/findByUsuario/${id}`);
  }
  
  // Método para vendas mensais
  getVendasMensais(ano: number, mes: number): Observable<number> {
    return this.http.get<number>(`${this.api}/mensal/${ano}/${mes}`);
  }

  // Método para vendas semanais
  getVendasSemanais(): Observable<number> {
    return this.http.get<number>(`${this.api}/semanal`);
  }

  // Método para vendas anuais
  getVendasAnuais(ano: number): Observable<number> {
    return this.http.get<number>(`${this.api}/anual/${ano}`);
  }
  getNumeroVendasDia(usuarioId: number): Observable<number> {
    return this.http.get<number>(`${this.api}/dia/quantidade?usuarioId=${usuarioId}`);
  }
  
}
