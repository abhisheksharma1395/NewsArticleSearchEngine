import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Service } from './service';
import { Data } from './data.model';
import { ViewportScroller } from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  public searchForm: any;
  private searchTypeLucene: boolean = true;
  public loading: boolean = false;
  public flag: boolean = true;
  private skip:number = 0;
  public news: Array<any> = [];
  public showNext: boolean = false;
  public showPrev: boolean = false;
  public hasResult: boolean = false;
  public loadPage: boolean = true;
  private pageYoffset: any;

  @HostListener('window:scroll', ['$event']) onScroll(event: any){
    this.pageYoffset = window.pageYOffset;
 }

  constructor(
    private formBuilder: FormBuilder, private service: Service, private scroll: ViewportScroller){}

  ngOnInit(): void {
    //throw new Error('Method not implemented.');
    this.searchForm = this.formBuilder.group({
      searchInput: ['', Validators.required],
      searchType: ['lucene', Validators.required]
    });
  }
  title = 'News Article Search Engine';

  changeSearchType(event: any) {
    this.searchTypeLucene = event.target.value === 'lucene' ? true : false;
    if (this.searchTypeLucene){
      console.log(`Search type Lucene is selected`);
    } else {
      console.log(`Search type Hadoop is selected`);
    }
  }

  onSubmit() {
    if (this.searchForm.invalid) {
      return;
    }
    this.loading = true;
    this.loadPage = false;
    if (this.searchType.value == 'lucene') {
      this.fetchByLucene(this.searchInput.value, this.skip);
    } else {
      this.fetchByHadoop(this.searchInput.value, this.skip);
    }
    this.loading = false;
    this.flag = false;
    // this.news = [ 
    //   new Data(1, 5, "Test Covid", "Test Covid Body","https://ntimes.com"),
    //   new Data(2, 5, "Test Covid", "Test Covid Body","https://ntimes.com"),
    //   new Data(2, 5, "Test Covid", "Test Covid Body","https://ntimes.com"),
    // ]
  }

  next() {
    this.skip = this.skip + 10;
    this.showPrev = (this.skip >= 10)?true:false;
    if (this.searchTypeLucene) {
      this.fetchByLucene(this.searchInput.value, this.skip);
    } else {
      this.fetchByHadoop(this.searchInput.value, this.skip);
    }
  }
  
  previous() {
    this.skip = this.skip - 10;
    this.showPrev = (this.skip >= 10)?true:false;
    if (this.searchTypeLucene) {
      this.fetchByLucene(this.searchInput.value, this.skip);
    } else {
      this.fetchByHadoop(this.searchInput.value, this.skip);
    }
  }

  scrollToTop(){
    this.scroll.scrollToPosition([0,0]);
  }

  private fetchByLucene(query: string, skip: number) {
    this.service.fetchbylucene(query, skip).subscribe(
      (result) => {
        this.news = result;
        if (this.news.length == 0) {
          this.hasResult = false;
          this.showNext = false;
          this.showPrev = false;
          this.loadPage = true;
          return;
        } else {
          this.hasResult = true;
        }
        this.showNext = (this.news.length == 10)?true:false;
        this.loadPage = true;
        this.scrollToTop();
      }, (error) => {
        console.log(`Error occured ${error}`);
      } 
    );
  }

  private fetchByHadoop(query: string, skip: number) {
    this.service.fetchbyhadoop(query, skip).subscribe(
      (result) => {
        this.news = result;
        if (this.news.length == 0) {
          this.hasResult = false;
          this.showNext = false;
          this.showPrev = false;
          this.loadPage = true;
          return;
        } else {
          this.hasResult = true;
        }
        this.showNext = (this.news.length == 10)?true:false;
        this.loadPage = true;
        this.scrollToTop();
      }, (error) => {
        console.log(`Error occured ${error}`);
      } 
    );
  }

  get searchInput() {
    return this.searchForm.get('searchInput');
  }

  get searchType() {
    return this.searchForm.get('searchType');
  }
}
