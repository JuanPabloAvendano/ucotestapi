/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** URL pública detrás del WAF (Q-5); si falta, se usa el origen de la página. */
  readonly VITE_URL_PUBLICA?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const componente: DefineComponent<object, object, unknown>
  export default componente
}
