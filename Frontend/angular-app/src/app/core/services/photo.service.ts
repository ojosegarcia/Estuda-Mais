import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {
  private api = `${environment.apiUrl}/api/usuarios`;

  constructor(private http: HttpClient) {}

  uploadPhoto(usuarioId: number, file: File): Observable<any> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post(`${this.api}/${usuarioId}/foto`, form);
  }
}