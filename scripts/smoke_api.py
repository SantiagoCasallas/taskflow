"""Recorrido de integración local. Crea y elimina su propia tarea de prueba."""
import json
import os
import uuid
from urllib.error import HTTPError
from urllib.request import Request, urlopen

BASE = os.environ.get('TASKFLOW_API_URL', 'http://localhost:8080/api')


def request(method, path, body=None):
    data = json.dumps(body).encode() if body is not None else None
    req = Request(BASE + path, data=data, method=method,
                  headers={'Content-Type': 'application/json'})
    with urlopen(req, timeout=30) as response:
        raw = response.read()
        return response.status, json.loads(raw) if raw else None


def main():
    _, categories = request('GET', '/categories')
    assert categories, 'Se requiere al menos una categoría'
    title = 'smoke-' + uuid.uuid4().hex
    payload = {'title': title, 'description': 'Prueba de integración',
               'status': 'PENDING', 'categoryId': categories[0]['id'],
               'dueDate': '2026-12-01'}
    task_id = None
    try:
        status, created = request('POST', '/tasks', payload)
        assert status == 201
        task_id = created['id']
        _, page = request('GET', '/tasks?search=' + title + '&status=PENDING&size=1')
        assert page['totalElements'] == 1 and page['size'] == 1
        assert page['content'][0]['id'] == task_id
        payload['status'] = 'COMPLETED'
        payload['dueDate'] = None
        _, updated = request('PUT', f'/tasks/{task_id}', payload)
        assert updated['status'] == 'COMPLETED' and updated['dueDate'] is None
        _, page = request('GET', '/tasks?search=' + title + '&status=PENDING')
        assert page['totalElements'] == 0
        try:
            request('GET', '/tasks?size=101')
        except HTTPError as error:
            assert error.code == 400
        else:
            raise AssertionError('La API debe rechazar size=101')
        status, _ = request('DELETE', f'/tasks/{task_id}')
        assert status == 204
        task_id = None
        _, page = request('GET', '/tasks?search=' + title)
        assert page['totalElements'] == 0
        print('OK: creación, búsqueda, paginación, edición, validación y eliminación')
    finally:
        if task_id is not None:
            request('DELETE', f'/tasks/{task_id}')


if __name__ == '__main__':
    main()
