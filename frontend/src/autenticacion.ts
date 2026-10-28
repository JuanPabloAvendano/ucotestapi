import Keycloak from 'keycloak-js'

const urlPublica = import.meta.env.VITE_URL_PUBLICA ?? window.location.origin

/** Cliente público con PKCE S256 (A-17, Q-7): el navegador nunca guarda un secreto. */
export const keycloak = new Keycloak({
  url: `${urlPublica}/auth`,
  realm: 'ucotest',
  clientId: 'ucotest-frontend',
})

export async function iniciarSesion(): Promise<void> {
  await keycloak.init({ onLoad: 'login-required', pkceMethod: 'S256', checkLoginIframe: false })
}

/** Renueva el token si le quedan menos de 30 s, para no mandar uno vencido. */
export async function tokenVigente(): Promise<string> {
  await keycloak.updateToken(30)
  if (!keycloak.token) {
    throw new Error('Sin token de sesión')
  }
  return keycloak.token
}

export function nombreDelUsuario(): string {
  const datos = keycloak.tokenParsed as { name?: string; preferred_username?: string } | undefined
  return datos?.name ?? datos?.preferred_username ?? ''
}

export function cerrarSesion(): void {
  void keycloak.logout({ redirectUri: window.location.origin })
}
