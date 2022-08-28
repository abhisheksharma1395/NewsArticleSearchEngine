import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { throwError } from 'rxjs';
import { Data } from './data.model';

@Injectable({
    providedIn: 'root',
})
export class Service {

    private baseUrl: string = "http://localhost:8080/"
    constructor(private http: HttpClient) { }

    public fetchbylucene(query: string, skip: number) {
        let url: string = this.baseUrl + 'lucene/fetch'
        const params = new HttpParams()
        .set('query', ''+query)
        .set('skip', ''+skip);

        return this.http.get<Data[]>(url, {params})
            .pipe(map((response: Data[]) => {
                return response;
            }), catchError(this.handleError)
            );
    }

    public fetchbyhadoop(query: string, skip: number) {
        let url: string = this.baseUrl + 'hadoop/fetch'
        const params = new HttpParams()
        .set('query', ''+query)
        .set('skip', ''+skip);

        return this.http.get<Data[]>(url, {params})
            .pipe(map((response: Data[]) => {
                return response;
            }), catchError(this.handleError)
            );
    }

    private handleError(error: HttpErrorResponse) {
        console.log(`Error ${error.message}`);
        return throwError(`A data error occurred, please try again`);
    }

}