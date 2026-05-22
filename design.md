# Design System Base - BANCO_DWF

> Base oficial para frontend del proyecto.  
> Se trabaja con UX separada por perfil: **Usuario** y **Gerente**.

## 1) Stack UI permitido

- Thymeleaf (SSR)
- CSS vanilla
- JavaScript vanilla
- Bootstrap Icons (CDN)

## 2) Fuente de verdad actual

- Shared:
  - `templates/layout/fragments.html`
  - `static/css/app-shell.css`
  - `static/js/app-shell.js`
- Usuario:
  - `pages/dashboard.html` + `css/dashboard.css` + `js/dashboard.js`
  - `pages/movimientos.html` + `css/movimientos.css` + `js/movimientos.js`
  - `pages/transferencias.html` + `css/transferencias.css` + `js/transferencias.js`
  - `pages/prestamos.html` + `css/prestamos.css` + `js/prestamos.js`
- Gerente:
  - `pages/clientes.html` + `css/clientes.css` + `js/clientes.js`
  - `pages/empleados.html` + `css/empleados.css` + `js/empleados.js`
  - `pages/aprobacion-creditos.html` + `css/aprobacion-creditos.css` + `js/aprobacion-creditos.js`

## 3) Tokens base

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

## 4) Shell y navegación

- Header y sidebar siempre compartidos.
- Menú unificado visible para todos en esta fase:
  - Dashboard
  - Movimientos
  - Transacciones
  - Mis Préstamos
  - Clientes
  - Empleados
  - Aprobación Créditos
- **Configuración removida** de UX actual.
- Nota: en un refactor posterior se aplicará visibilidad por rol.

## 5) Reglas de UX separada

### Usuario
- Ve y opera sus cuentas, transacciones y solicitudes de préstamo.
- No aprueba créditos ni administra personal.

### Gerente
- Gestiona cartera de clientes.
- Gestiona empleados.
- Revisa y decide solicitudes en Aprobación de Créditos.

### Visibilidad temporal
- En esta fase TODAS las rutas se muestran en el sidebar.
- En refactor posterior se aplicará visibilidad por rol/permisos.

## 6) Patrones de componentes

- Inputs/selects: 38px a 44px, radio 8px-10px.
- Fechas en filtros: `input[type="date"]`.
- Botón primario: fondo `--accent`, texto oscuro, peso 700.
- Toast: feedback visual sin backend.
- Modales CRUD frontend (sin backend) para altas/ediciones/eliminaciones:
  - Clientes: `nombre`, `dui`, `salario`, `estado`
  - Empleados: `nombre`, `rol`, `estado` (+ sucursal visual para la UI)
- Badges estado:
  - verde = aprobado/activo
  - amarillo = en espera
  - rojo = rechazado

## 7) Rutas frontend

### Usuario
- `/dashboard`
- `/movimientos`
- `/transferencias`
- `/prestamos`

### Gerente
- `/gerencia/clientes`
- `/gerencia/empleados`
- `/gerencia/aprobacion-creditos`

### Legacy
- `/pagos` redirige a `/prestamos`
- `/configuracion` redirige a `/gerencia/clientes`

## 8) Arquitectura por pantalla (obligatorio)

- No monolito de vista/CSS/JS.
- Shared en shell.
- Cada pantalla con su propio HTML/CSS/JS.

## 9) Checklist por iteración

- [ ] Respeta tokens y tipografía.
- [ ] Mantiene separación usuario vs gerente.
- [ ] Mantiene pantalla separada por archivo.
- [ ] Actualiza `design.md` y `FRONTEND_TODO.md` si cambia el flujo.
