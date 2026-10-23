import { Routes } from '@angular/router';
import { LoginComponent } from './components/layout/login/login.component';
import { PrincipalComponent } from './components/layout/principal/principal.component';
import { DashboardComponent } from './components/layout/dashboard/dashboard.component';
import { UsuarioFormComponent } from './components/usuarios/usuario-form/usuario-form.component';
import { UsuarioListComponent } from './components/usuarios/usuario-list/usuario-list.component';
import { ProdutosListComponent } from './components/produtos/produtos-list/produtos-list.component';
import { VendaComponent } from './components/vendas/venda/venda.component';
import { loginGuard } from './auth/login.guard';
import { InvalidAcessComponent } from './components/invalid-acess/invalid-acess.component';
import { ProdutosFormComponent } from './components/produtos/produtos-form/produtos-form.component';
import { DashboardFuncionarioComponent } from './components/layout/dashboard-funcionario/dashboard-funcionario.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    component: LoginComponent,
    data: { title: 'Blackcat - Login' },
  },
  {
    path: 'invalid-access',
    component: InvalidAcessComponent,
    data: { title: 'Acesso Inválido - Blackcat' },
  },

  {
    path: 'blackcat',
    component: PrincipalComponent,
    canActivate: [loginGuard],
    children: [
      {
        path: 'funcionario',
        component: DashboardFuncionarioComponent,
        data: { title: 'Blackcat - Dashboard Funcionario' }
      },
      
      { path: 'dashboard', component: DashboardComponent, data: { title: 'Blackcat - Home' } },
     
      {
        path: 'usuarios',
        data: { title: 'Blackcat - Usuários' },
        children: [
          {
            path: '',
            component: UsuarioListComponent,
            data: { title: 'Blackcat - Usuários' },
          },
          {
            path: 'new',
            component: UsuarioFormComponent,
            data: { title: 'Blackcat - Novo Usuário' },
          },
          {
            path: 'edit/:id',
            component: UsuarioFormComponent,
            data: { title: 'Blackcat - Editar Usuário' },
          },
        ],
      },

      {
        path: 'produtos',
        data: { title: 'Blackcat - Produtos' },
        children: [
          {
            path: '',
            component: ProdutosListComponent,
            data: { title: 'Blackcat - Produtos' },
          },
          {
            path: 'new',
            component: ProdutosFormComponent,
            data: { title: 'Blackcat - Novo Produto' },
          },
          {
            path: 'edit/:id',
            component: ProdutosFormComponent,
            data: { title: 'Blackcat - Editar Produto' },
          },
        ],
      },


      { path: 'venda', component: VendaComponent, data: { title: 'Blackcat - Vendas' } },
    ],
  },

  
];
