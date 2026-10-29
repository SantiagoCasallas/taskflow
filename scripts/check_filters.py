"""Verifica filtros, orden y dos páginas; elimina sus tareas temporales."""
import uuid
from smoke_api import request

prefix='check-'+uuid.uuid4().hex
ids=[]
try:
    _, categories=request('GET','/categories')
    for date,status,category in [('2026-12-03','PENDING',categories[0]['id']),('2026-12-01','IN_PROGRESS',categories[1]['id']),('2026-12-02','COMPLETED',categories[0]['id'])]:
        _,task=request('POST','/tasks',dict(title=prefix,description='Integración temporal',status=status,categoryId=category,dueDate=date))
        ids.append(task['id'])
    _, first=request('GET',f'/tasks?search={prefix}&size=2&sort=dueDate&direction=asc')
    _, second=request('GET',f'/tasks?search={prefix}&size=2&page=1&sort=dueDate&direction=asc')
    assert first['totalElements']==3 and first['totalPages']==2
    assert [t['id'] for t in first['content']+second['content']]==[ids[1],ids[2],ids[0]]
    _, descending=request('GET',f'/tasks?search={prefix}&sort=dueDate&direction=desc')
    assert [t['id'] for t in descending['content']]==[ids[0],ids[2],ids[1]]
    _, filtered=request('GET',f'/tasks?search={prefix}&categoryId={categories[1]["id"]}&status=IN_PROGRESS')
    assert filtered['totalElements']==1 and filtered['content'][0]['id']==ids[1]
    print('OK: tres estados, filtro por categoría, orden ascendente/descendente y dos páginas sin duplicados')
finally:
    for task_id in ids:
        request('DELETE',f'/tasks/{task_id}')
