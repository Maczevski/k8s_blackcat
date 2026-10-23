import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AlertService } from '../services/alert.service'; 
import { catchError, throwError } from 'rxjs';

export const meuhttpInterceptor: HttpInterceptorFn = (request, next) => {
  const router = inject(Router);
  const alertService = inject(AlertService);

  const token = localStorage.getItem('token');
  if (token && !router.url.includes('/login')) {
    request = request.clone({
      setHeaders: { Authorization: 'Bearer ' + token },
    });
  }

  return next(request).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err instanceof HttpErrorResponse) {
        if (err.status === 401) {
          alertService.showAlert('Sessão Expirada', 'error'); 
          router.navigate(['/login']);
        } else if (err.status === 403) {
          alertService.showAlert('Acesso Negado', 'error'); // Tratando o erro 403
          router.navigate(['/login']);
        } else {
          alertService.showErrorToast(err); // Tratando outros erros
        }
      } else {
        console.error('An error occurred:', err);
      }

      return throwError(() => err);
    })
  );
};
