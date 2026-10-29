import { Component, inject, signal, InjectionToken } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Subject, debounceTime, switchMap, catchError, of } from 'rxjs';
export const API_URL = new InjectionToken<string>('API_URL');
type Status = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
interface Category { id: number; name: string }
interface Task { id:number; title:string; description:string; status:Status; category:Category; createdAt:string; dueDate:string|null }
interface Page { content:Task[]; totalElements:number; totalPages:number; number:number; size:number }
@Component({selector:'app-root', standalone:true, imports:[FormsModule,DatePipe], templateUrl:'./app.html'})
export class AppComponent {
 private http=inject(HttpClient); private api=inject(API_URL);
 readonly labels:Record<Status,string>={PENDING:'Pendiente',IN_PROGRESS:'En proceso',COMPLETED:'Completada'};
 readonly statuses:Status[]=['PENDING','IN_PROGRESS','COMPLETED'];
 categories=signal<Category[]>([]); data=signal<Page|null>(null); error=signal(''); loading=signal(false); saving=signal(false);
 search=''; status=''; categoryId=''; order='createdAt,desc'; page=0; size=10;
 editing:number|null=null; formOpen=false; draft=this.empty();
 private queries=new Subject<void>();
 constructor() {
  this.queries.pipe(debounceTime(250),switchMap(()=>{
   this.loading.set(true); this.error.set('');
   const [sort,direction]=this.order.split(',');
   let params=new HttpParams().set('search',this.search).set('page',this.page).set('size',this.size).set('sort',sort).set('direction',direction);
   if(this.status) params=params.set('status',this.status);
   if(this.categoryId) params=params.set('categoryId',this.categoryId);
   return this.http.get<Page>(this.api+'/tasks',{params}).pipe(catchError(()=>{this.error.set('No se pudieron cargar las tareas. Intenta nuevamente.');return of(null);}));
  })).subscribe(result=>{
   this.loading.set(false); this.data.set(result);
   if(result && result.totalPages>0 && this.page>=result.totalPages){this.page=result.totalPages-1;this.refresh();}
  });
  this.http.get<Category[]>(this.api+'/categories').subscribe({next:c=>this.categories.set(c),error:()=>this.error.set('No se pudieron cargar las categorías. Recarga la página.')});
  this.refresh();
 }
 empty(){return {title:'',description:'',status:'PENDING' as Status,categoryId:0,dueDate:''};}
 refresh(reset=false){if(reset)this.page=0;this.queries.next();}
 move(delta:number){this.page+=delta;this.refresh();}
 open(task?:Task){this.editing=task?.id??null;this.draft=task?{title:task.title,description:task.description,status:task.status,categoryId:task.category.id,dueDate:task.dueDate??''}:this.empty();this.formOpen=true;}
 save(){
  if(!this.draft.title.trim()||!this.draft.categoryId||this.saving())return;
  this.saving.set(true);this.error.set('');
  const body={...this.draft,dueDate:this.draft.dueDate||null};
  const request=this.editing?this.http.put(this.api+'/tasks/'+this.editing,body):this.http.post(this.api+'/tasks',body);
  request.subscribe({next:()=>{this.saving.set(false);this.formOpen=false;this.refresh();},error:(e:HttpErrorResponse)=>{this.saving.set(false);this.error.set(e.error?.detail??'No se pudo guardar la tarea.');}});
 }
 remove(task:Task){if(!confirm('¿Eliminar “'+task.title+'”?'))return;
  this.http.delete(this.api+'/tasks/'+task.id).subscribe({next:()=>this.refresh(),error:()=>this.error.set('No se pudo eliminar la tarea.')});
 }
}
