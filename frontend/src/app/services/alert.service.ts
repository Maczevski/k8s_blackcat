import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Swal, { SweetAlertIcon } from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  showConfirmDialog(
    message: string,
    txt: string,
    confirmButtonText: string,
    icon: SweetAlertIcon
  ) {
    return Swal.fire({
      title: message,
      text: txt,
      showCancelButton: true,
      confirmButtonColor: '#84aadc',
      cancelButtonColor: '#d33',
      confirmButtonText: confirmButtonText,
      cancelButtonText: 'Cancelar',
      icon: icon,
    });
  }
  showConfirmDialogWithImg(
    message: string,
    txt: string,
    confirmButtonText: string) {
    return Swal.fire({
      imageUrl: 'https://unsplash.it/400/200',
      imageWidth: 400,
      imageHeight: 200,
      title: message,
      text: txt,
      showCancelButton: true,
      confirmButtonColor: '#84aadc',
      cancelButtonColor: '#d33',
      confirmButtonText: confirmButtonText,
      cancelButtonText: 'Cancelar',
    });
  }

  showAlert(message: string, icon: SweetAlertIcon) {
    Swal.fire(message, '', icon);
  }

  showToast(message: string, icon: SweetAlertIcon) {
    const Toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true,
      didOpen: (toast) => {
        toast.onmouseenter = Swal.stopTimer;
        toast.onmouseleave = Swal.resumeTimer;
  
        // Adiciona evento para fechar ao clicar fora
        const clickListener = (event: MouseEvent) => {
          if (!toast.contains(event.target as Node)) {
            Toast.close();
            document.removeEventListener('click', clickListener); // Remove o evento após fechar
          }
        };
        document.addEventListener('click', clickListener);
      },
    });
  
    Toast.fire({
      icon: icon,
      title: message,
    });
  }
  

  showErrorToast(erro: HttpErrorResponse) {
    this.showToast(this.tratarErro(erro), 'error');
  }
  showErrorAlert(erro: HttpErrorResponse) {
    this.showAlert(this.tratarErro(erro), 'error');
  }

  tratarErro(erro: HttpErrorResponse): string {
    let msg;

    if (erro.status === 0) {
      msg = 'Não foi possível se conectar ao servidor. Verifique sua conexão com a internet ou tente novamente mais tarde.';
    }else{
      try {
        const parsedError =
          typeof erro.error === 'string' ? JSON.parse(erro.error) : erro.error;
        if (typeof parsedError === 'object' && parsedError !== null) {
          msg = Object.values(parsedError).join(' | ');
        }
      } catch {
        msg = erro.error || 'Erro desconhecido';
      }
    }


    return msg;
  }
}
