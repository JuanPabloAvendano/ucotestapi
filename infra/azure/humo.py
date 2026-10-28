"""Prueba de humo de Azure (H5.2), copia de la demo de la ESPEC (herramientas/demo_e2e.py) con verificación final.

Recorre la demo como el navegador por el WAF: login OIDC con PKCE de carlos en Keycloak y registro de 4.35 a Ana.
Sin nota previa espera 201 y luego 422 R06; si Ana ya tenía nota (despliegue repetido), 422 R06 las dos veces.
Uso: python3 humo.py <URL pública>
"""
import base64, hashlib, html, http.cookiejar, json, os, re, sys, urllib.error, urllib.parse, urllib.request

BASE = sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8080'
USUARIO, CLAVE = 'carlos', 'ucotest'
REDIRECT = BASE + '/'


class SinRedireccion(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *a, **k):
        return None


class PoliticaDeNavegador(http.cookiejar.DefaultCookiePolicy):
    # Como Chrome y Firefox: http://localhost es contexto seguro y recibe cookies Secure.
    def return_ok_secure(self, cookie, request):
        return True


tarro = http.cookiejar.CookieJar(PoliticaDeNavegador())
abrir = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(tarro), SinRedireccion()).open


def pedir(url, datos=None, cabeceras=None, metodo=None):
    req = urllib.request.Request(url, data=datos, headers=cabeceras or {}, method=metodo)
    try:
        r = abrir(req)
        return r.status, dict(r.headers), r.read().decode()
    except urllib.error.HTTPError as e:
        return e.code, dict(e.headers), e.read().decode()


verificador = base64.urlsafe_b64encode(os.urandom(32)).rstrip(b'=').decode()
reto = base64.urlsafe_b64encode(hashlib.sha256(verificador.encode()).digest()).rstrip(b'=').decode()
auth = BASE + '/auth/realms/ucotest/protocol/openid-connect/auth?' + urllib.parse.urlencode({
    'client_id': 'ucotest-frontend', 'redirect_uri': REDIRECT, 'response_type': 'code', 'scope': 'openid',
    'code_challenge': reto, 'code_challenge_method': 'S256', 'state': 'demo'})
estado, _, pagina = pedir(auth)
assert estado == 200, f'login page {estado}'
accion = html.unescape(re.search(r'<form[^>]*id="kc-form-login"[^>]*action="([^"]+)"', pagina).group(1))
estado, cab, cuerpo = pedir(accion, urllib.parse.urlencode({'username': USUARIO, 'password': CLAVE, 'credentialId': ''}).encode(),
                            {'Content-Type': 'application/x-www-form-urlencoded'})
assert estado == 302, f'login POST {estado}: {cuerpo[:300]}'
codigo = urllib.parse.parse_qs(urllib.parse.urlparse(cab['Location']).query)['code'][0]
estado, _, cuerpo = pedir(BASE + '/auth/realms/ucotest/protocol/openid-connect/token', urllib.parse.urlencode({
    'grant_type': 'authorization_code', 'code': codigo, 'redirect_uri': REDIRECT, 'client_id': 'ucotest-frontend',
    'code_verifier': verificador}).encode(), {'Content-Type': 'application/x-www-form-urlencoded'})
assert estado == 200, f'token {estado}: {cuerpo[:300]}'
token = json.loads(cuerpo)['access_token']
carga = json.loads(base64.urlsafe_b64decode(token.split('.')[1] + '=='))
print('login OK · iss', carga['iss'], '· sub', carga['sub'], '· roles', carga.get('realm_access', {}).get('roles'))

A = {'Authorization': 'Bearer ' + token}
_, _, g = pedir(BASE + '/api/v1/docentes/yo/grupos', cabeceras=A)
grupo = json.loads(g)['contenido'][0]
_, _, n = pedir(BASE + f"/api/v1/grupos/{grupo['id']}/notas", cabeceras=A)
evaluacion = next(e for e in json.loads(n)['contenido'] if e['descripcion'] == 'Parcial 1')
_, _, e = pedir(BASE + f"/api/v1/grupos/{grupo['id']}/estudiantes?notaId={evaluacion['id']}", cabeceras=A)
ana = next(x for x in json.loads(e)['contenido'] if x['nombre'].startswith('Ana'))
print('grupo', grupo['nombre'], '· evaluación', evaluacion['descripcion'], '· estudiante', ana['nombre'], '· tieneNota', ana['tieneNota'])

url = BASE + f"/api/v1/grupos/{grupo['id']}/notas/{evaluacion['id']}/calificaciones"
cuerpo = json.dumps({'estudianteId': ana['id'], 'centesimas': 435}).encode()
esperados = [422, 422] if ana['tieneNota'] else [201, 422]
for intento, esperado in enumerate(esperados, 1):
    estado, cab, resp = pedir(url, cuerpo, dict(A, **{'Content-Type': 'application/json'}), 'POST')
    print(f'intento {intento}:', estado, cab.get('Content-Type'), cab.get('Location', ''), resp)
    assert estado == esperado, f'intento {intento}: se esperaba {esperado} y llegó {estado}'
    if estado == 422:
        assert 'R06' in resp, f'intento {intento}: el 422 no es R06: {resp[:300]}'
print('humo OK')
