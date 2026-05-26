import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Point} from './point';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private getApiUrl(): string {
    return window.location.hostname === 'frontend' ? 'http://backend:8080' : 'http://localhost:8080';
  }

  constructor(private http: HttpClient) {
  }

  submit(x: number, y: number, r: number): Observable<any> {
    return this.http.post(`${this.getApiUrl()}/home/submit`, {x, y, r});
  }

  getAllPoints(): Observable<any> {
    return this.http.get<Point[]>(`${this.getApiUrl()}/home/points`);
  }
}
