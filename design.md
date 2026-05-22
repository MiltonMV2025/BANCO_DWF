# Design System Base - BANCO_DWF

> Esta es la base oficial del frontend.  
> Todo cambio visual nuevo debe respetar este documento.

## 1) Stack UI permitido (actual)

- Thymeleaf para vistas server-side.
- CSS vanilla.
- JavaScript vanilla.
- Bootstrap Icons (CDN) para iconografía consistente.
- Sin frameworks CSS externos por ahora.

## 2) Fuente de verdad actual

- `src/main/resources/static/css/login.css`
- `src/main/resources/templates/pages/login.html`
- `src/main/resources/static/css/app-shell.css`
- `src/main/resources/static/css/dashboard.css`
- `src/main/resources/static/css/movimientos.css`
- `src/main/resources/static/css/transferencias.css`
- `src/main/resources/static/css/pagos.css`
- `src/main/resources/static/css/configuracion.css`
- `src/main/resources/templates/layout/fragments.html`
- `src/main/resources/templates/pages/dashboard.html`
- `src/main/resources/templates/pages/movimientos.html`
- `src/main/resources/templates/pages/transferencias.html`
- `src/main/resources/templates/pages/pagos.html`
- `src/main/resources/templates/pages/configuracion.html`

## 3) Tokens visuales

```css
:root {
  --panel-bg: #070b11;
  --panel-bg-soft: #15171e;
  --accent: #f5c400;
  --text-main: #111827;
  --text-muted: #6b7280;
  --text-light: #e5e7eb;
  --border-color: #d1d5db;
  --surface: #ffffff;
  --page-bg: #efefef;
  --danger: #b91c1c;
  --success: #3ba96c;
}
```

### Colores de soporte

- Topbar: `#000000`
- Fondo sidebar: `#13151b` a `#171922`
- Hover sidebar: `#20242d`
- Fondo KPI card: `#ffffff`

## 4) Tipografia

- Unica familia permitida: `"Segoe UI", Tahoma, Geneva, Verdana, sans-serif`.
- No introducir otra familia sin decision arquitectonica.

## 5) Layouts aprobados

### 5.1 Login

- Grid 2 columnas `minmax(320px, 42%) 1fr`.
- Breakpoint principal: `max-width: 920px`.

### 5.2 App interna (Dashboard/Movimientos/etc.)

- Shell de 3 zonas:
  - Topbar superior
  - Sidebar izquierdo
  - Content principal
- Sidebar con estado activo en amarillo (`--accent`).
- Modulos actuales:
  - Dashboard
  - Movimientos
  - Transferencias
  - Pagos
  - Configuracion

## 6) Componentes base

### 6.1 Inputs y Selects

- Altura: `38px` (filtros) o `44px` (formularios generales).
- Radio: `8px` a `10px`.
- Borde: `1px solid var(--border-color)`.
- Focus:
  - `border-color: #c7a008`
  - `box-shadow: 0 0 0 3px rgba(245, 196, 0, 0.2)`
- Para fechas en filtros, usar `input[type="date"]` (no `text`) para mostrar calendario nativo.

### 6.2 Botones

- Primario (`btn-accent`):
  - Fondo: `var(--accent)`
  - Texto: `#111`
  - Peso: `700`
  - Altura: `38px` a `46px`
  - Radio: `8px` a `10px`
- Secundario (outline):
  - Fondo blanco
  - Borde oscuro
  - Mismas alturas segun contexto

### 6.3 Tabla de Movimientos

- Cabecera sobria con borde inferior.
- Filas cebra en grises suaves.
- Columna monto alineada a la derecha.
- Montos positivos en `--success`.
- Montos negativos en tono rojo suave.

### 6.4 Paginacion

- Centrada abajo de la tabla.
- Boton activo con fondo `--accent`.
- Flechas laterales para anterior/siguiente.

### 6.5 Tarjetas KPI (Dashboard)

- Fondo blanco (`--surface`).
- Borde general `1px solid var(--border-color)`.
- Borde izquierdo de acento amarillo.
- Radio `11px`.
- Ícono de KPI al extremo derecho usando Bootstrap Icons.

### 6.6 Toast de feedback visual

- Fondo oscuro `#111827`.
- Texto blanco.
- Radio `10px`.
- Uso en acciones frontend sin backend.

### 6.7 Header y sesión

- El header debe incluir:
  - Avatar de usuario.
  - Botón de logout visible.
- Logout debe ejecutar acción real contra `/logout` usando formulario `POST`.

### 6.8 Dashboard operacional (cajero)

- Encabezado de bienvenida con nombre y sucursal.
- Tabla de actividad reciente con badges de estado:
  - `Completado` (verde)
  - `En espera` (amarillo)
- Bloque de acciones inferiores en dos columnas:
  - Izquierda: botón outline fuerte.
  - Derecha: botón acento amarillo.

## 7) Reglas de flujo frontend (fase actual)

- No consumir endpoints reales todavia.
- Navegacion por rutas dedicadas por pantalla:
  - `/dashboard`
  - `/movimientos`
  - `/transferencias`
  - `/pagos`
  - `/configuracion`
- Filtros, botones y paginacion en modo visual.
- Se permite data mock para validar UX.

## 8) Arquitectura frontend por pantalla (OBLIGATORIO)

- No centralizar todas las vistas en una sola plantilla.
- No centralizar toda la logica en un solo JS.
- No centralizar todos los estilos en un solo CSS.

Estructura aprobada:

- **Compartido**
  - `templates/layout/fragments.html` (topbar/sidebar)
  - `css/app-shell.css` (layout y tokens comunes)
  - `js/app-shell.js` (utilidades comunes como toast)
- **Por pantalla**
  - `pages/<pantalla>.html`
  - `css/<pantalla>.css`
  - `js/<pantalla>.js`

## 9) Convenciones de estructura

- CSS: `src/main/resources/static/css/`
- JS: `src/main/resources/static/js/`
- Vistas: `src/main/resources/templates/pages/`
- Layouts compartidos: `src/main/resources/templates/layout/`

## 10) Regla de evolucion del documento

1. Si agregas un patron visual nuevo, actualiza `design.md` en el mismo cambio.
2. Si agregas token nuevo, justificar su necesidad.
3. Mantener consistencia de spacing, radios, focus y contraste.
4. Evitar introducir librerias UI sin decision formal.

## 11) Checklist obligatorio antes de cerrar una pantalla

- [ ] Use los tokens oficiales.
- [ ] Mantuve tipografia oficial.
- [ ] Inputs/selects/botones respetan alturas y radios definidos.
- [ ] Estados hover/focus son visibles.
- [ ] Responsive funcional en tablet/mobile.
- [ ] Pantalla separada en su propio HTML/CSS/JS.
- [ ] Documente en este archivo cualquier patron nuevo.
