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
- Menú condicionado por rol:
  - Usuario: Dashboard, Movimientos, Transacciones, Mis Préstamos
  - Gerencia: Clientes, Empleados, Aprobación Créditos
- **Configuración removida** de UX actual.

## 5) Reglas de UX separada

### Usuario
- Ve y opera sus cuentas, transacciones y solicitudes de préstamo.
- No aprueba créditos ni administra personal.

### Gerente
- Gestiona cartera de clientes.
- Gestiona empleados.
- Revisa y decide solicitudes en Aprobación de Créditos.

### Visibilidad por rol (actual)
- Sidebar usuario visible para: `ROLE_CLIENTE`, `ROLE_CAJERO`, `ROLE_DEPENDIENTE`.
- Sidebar gerencia visible para: `ROLE_GERENTE_SUCURSAL`, `ROLE_GERENTE_GENERAL`.
- Si el usuario autenticado no coincide con las reglas anteriores, se aplica fallback visual de menú usuario.

## 6) Patrones de componentes

- Inputs/selects: 38px a 44px, radio 8px-10px.
- Fechas en filtros: `input[type="date"]`.
- Botón primario: fondo `--accent`, texto oscuro, peso 700.
- Toast: feedback visual para confirmaciones y errores del backend.
- Login:
  - Formulario principal de acceso usando DUI + contraseña.
  - Acceso a registro desde enlace "¿No tenés cuenta? Registrate" hacia pantalla dedicada (`/registro`).
  - Registro crea cliente + cuenta base + usuario con `ROLE_CLIENTE`.
- Modales CRUD conectados a backend para altas/ediciones/inactivaciones:
  - Clientes: `nombre`, `dui`, `salario`, `estado`
  - Empleados: `nombre`, `dui` (requerido), `rol` (select desde tabla `rol`), `password`, `confirmarPassword`, `estado`
- Badges estado:
  - verde = aprobado/activo
  - amarillo = en espera
  - rojo = rechazado
- Transferencias:
  - Búsqueda por DUI
  - Selección de cuenta real (origen)
  - Soporte de transferencia entre cuentas (origen -> destino)
  - Depósito/Retiro/Transferencia persiste `movimiento` y actualiza `saldo`
  - Debe mostrar empty state si no hay cliente, cuentas o movimientos
- Alta de cliente:
  - Al crear cliente se crea automáticamente una cuenta base de ahorro con saldo inicial 0.00

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
- [ ] Actualiza `design.md` si cambia el flujo.
