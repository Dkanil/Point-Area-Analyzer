import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private getApiUrl(): string {
    return window.location.hostname === 'frontend' ? 'http://backend:8080' : 'http://localhost:8080';
  }

  constructor(private http: HttpClient) {
  }

  authenticate(username: string, password: string, isRegistration: boolean): Observable<any> {
    const url = `${this.getApiUrl()}/auth/sign-${(isRegistration ? 'up' : 'in')}`;
    return this.http.post(url, {username, password});
  }
}
