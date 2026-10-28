import { createApp } from 'vue'
import App from './App.vue'
import { iniciarSesion } from './autenticacion'
import './estilos.css'

// Sin sesión no hay nada que mostrar: keycloak-js redirige al login antes de montar la app (Q-7).
iniciarSesion()
  .then(() => createApp(App).mount('#app'))
  .catch(() => {
    document.getElementById('app')!.textContent = 'No pudimos iniciar sesión. Recarga la página.'
  })
