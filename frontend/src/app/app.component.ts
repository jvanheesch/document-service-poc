import {Component, NgZone} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject} from "rxjs";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    loading = false;
    status = new BehaviorSubject<string>('');

    constructor(
        private http: HttpClient,
        private _ngZone: NgZone,
    ) {
    }

    async fileChange(event: any) {
        let fileList: FileList = event.target.files;
        if (fileList.length > 0) {
            this.loading = true;
            let file: File = fileList[0];
            let formData: FormData = new FormData();
            formData.append('file', file, file.name);
            this.http
                .post('/documents', formData)
                .subscribe(document => {
                    this.loading = false;
                    console.log(document);
                });
        }
    }
}
