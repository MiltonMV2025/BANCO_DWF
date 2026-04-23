/* ==========================================================
   BANCO_DWF - SCRIPT BASE (DROP + CREATE)
   SQL Server (ejecutar manualmente en sqlcmd/SSMS)
   ========================================================== */

/* ---------- DROP TRIGGERS ---------- */
IF OBJECT_ID('dbo.TR_CUENTA_MAX_3', 'TR') IS NOT NULL
    DROP TRIGGER dbo.TR_CUENTA_MAX_3;
GO

/* ---------- DROP TABLES (orden por FK) ---------- */
IF OBJECT_ID('dbo.movimiento', 'U') IS NOT NULL
    DROP TABLE dbo.movimiento;
GO

IF OBJECT_ID('dbo.prestamo', 'U') IS NOT NULL
    DROP TABLE dbo.prestamo;
GO

IF OBJECT_ID('dbo.cuenta', 'U') IS NOT NULL
    DROP TABLE dbo.cuenta;
GO

IF OBJECT_ID('dbo.empleado', 'U') IS NOT NULL
    DROP TABLE dbo.empleado;
GO

IF OBJECT_ID('dbo.cliente', 'U') IS NOT NULL
    DROP TABLE dbo.cliente;
GO

IF OBJECT_ID('dbo.usuario_rol', 'U') IS NOT NULL
    DROP TABLE dbo.usuario_rol;
GO

IF OBJECT_ID('dbo.usuario', 'U') IS NOT NULL
    DROP TABLE dbo.usuario;
GO

IF OBJECT_ID('dbo.rol', 'U') IS NOT NULL
    DROP TABLE dbo.rol;
GO

/* ---------- SEGURIDAD (LOGIN + ROLES) ---------- */
CREATE TABLE dbo.rol (
    id_rol INT IDENTITY(1,1) NOT NULL,
    codigo VARCHAR(40) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    estado CHAR(1) NOT NULL CONSTRAINT DF_ROL_ESTADO DEFAULT 'A',
    CONSTRAINT PK_ROL PRIMARY KEY (id_rol),
    CONSTRAINT UQ_ROL_CODIGO UNIQUE (codigo),
    CONSTRAINT CK_ROL_ESTADO CHECK (estado IN ('A','I'))
);
GO

CREATE TABLE dbo.usuario (
    id_usuario INT IDENTITY(1,1) NOT NULL,
    username VARCHAR(60) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    estado CHAR(1) NOT NULL CONSTRAINT DF_USUARIO_ESTADO DEFAULT 'A',
    CONSTRAINT PK_USUARIO PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_USUARIO_USERNAME UNIQUE (username),
    CONSTRAINT CK_USUARIO_ESTADO CHECK (estado IN ('A','I'))
);
GO

CREATE TABLE dbo.usuario_rol (
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    CONSTRAINT PK_USUARIO_ROL PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT FK_USUARIO_ROL_USUARIO FOREIGN KEY (id_usuario) REFERENCES dbo.usuario (id_usuario),
    CONSTRAINT FK_USUARIO_ROL_ROL FOREIGN KEY (id_rol) REFERENCES dbo.rol (id_rol)
);
GO

/* ---------- DOMINIO BANCARIO ---------- */
CREATE TABLE dbo.cliente (
    id INT IDENTITY(1,1) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    dui VARCHAR(10) NOT NULL,
    salario DECIMAL(18,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT PK_CLIENTE PRIMARY KEY (id),
    CONSTRAINT UQ_CLIENTE_DUI UNIQUE (dui),
    CONSTRAINT CK_CLIENTE_SALARIO_NONNEG CHECK (salario >= 0)
);
GO

CREATE TABLE dbo.empleado (
    id_empleado INT IDENTITY(1,1) NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT PK_EMPLEADO PRIMARY KEY (id_empleado)
);
GO

CREATE TABLE dbo.cuenta (
    id_cuenta INT IDENTITY(1,1) NOT NULL,
    id_cliente INT NOT NULL,
    numero_cuenta VARCHAR(20) NULL,
    saldo DECIMAL(18,2) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    fecha_creacion DATE NOT NULL,
    CONSTRAINT PK_CUENTA PRIMARY KEY (id_cuenta),
    CONSTRAINT FK_CUENTA_CLIENTE FOREIGN KEY (id_cliente) REFERENCES dbo.cliente (id),
    CONSTRAINT CK_CUENTA_SALDO_NONNEG CHECK (saldo >= 0)
);
GO

CREATE TABLE dbo.prestamo (
    id_prestamo INT IDENTITY(1,1) NOT NULL,
    id_cliente INT NOT NULL,
    id_empleado INT NOT NULL,
    monto DECIMAL(18,2) NOT NULL,
    interes DECIMAL(9,4) NOT NULL,
    plazo_meses INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    cuota_mensual DECIMAL(18,2) NULL,
    anios_pago DECIMAL(6,2) NULL,
    CONSTRAINT PK_PRESTAMO PRIMARY KEY (id_prestamo),
    CONSTRAINT FK_PRESTAMO_CLIENTE FOREIGN KEY (id_cliente) REFERENCES dbo.cliente (id),
    CONSTRAINT FK_PRESTAMO_EMPLEADO FOREIGN KEY (id_empleado) REFERENCES dbo.empleado (id_empleado),
    CONSTRAINT CK_PRESTAMO_MONTO_POSITIVE CHECK (monto > 0),
    CONSTRAINT CK_PRESTAMO_INTERES_NONNEG CHECK (interes >= 0),
    CONSTRAINT CK_PRESTAMO_PLAZO_POSITIVE CHECK (plazo_meses > 0)
);
GO

CREATE TABLE dbo.movimiento (
    id_movimiento INT IDENTITY(1,1) NOT NULL,
    id_cliente INT NOT NULL,
    id_cuenta INT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    monto DECIMAL(18,2) NOT NULL,
    fecha DATE NOT NULL,
    CONSTRAINT PK_MOVIMIENTO PRIMARY KEY (id_movimiento),
    CONSTRAINT FK_MOVIMIENTO_CLIENTE FOREIGN KEY (id_cliente) REFERENCES dbo.cliente (id),
    CONSTRAINT FK_MOVIMIENTO_CUENTA FOREIGN KEY (id_cuenta) REFERENCES dbo.cuenta (id_cuenta),
    CONSTRAINT CK_MOVIMIENTO_MONTO_POSITIVE CHECK (monto > 0)
);
GO

/* ---------- INDEXES ---------- */
CREATE INDEX IX_CLIENTE_ESTADO ON dbo.cliente (estado);
CREATE INDEX IX_EMPLEADO_ESTADO ON dbo.empleado (estado);
CREATE INDEX IX_CUENTA_ID_CLIENTE ON dbo.cuenta (id_cliente);
CREATE INDEX IX_CUENTA_TIPO ON dbo.cuenta (tipo);
CREATE UNIQUE INDEX IXU_CUENTA_NUMERO_CUENTA ON dbo.cuenta (numero_cuenta) WHERE numero_cuenta IS NOT NULL;
CREATE INDEX IX_PRESTAMO_ID_CLIENTE ON dbo.prestamo (id_cliente);
CREATE INDEX IX_PRESTAMO_ID_EMPLEADO ON dbo.prestamo (id_empleado);
CREATE INDEX IX_PRESTAMO_ESTADO ON dbo.prestamo (estado);
CREATE INDEX IX_MOVIMIENTO_ID_CLIENTE ON dbo.movimiento (id_cliente);
CREATE INDEX IX_MOVIMIENTO_ID_CUENTA ON dbo.movimiento (id_cuenta);
CREATE INDEX IX_MOVIMIENTO_FECHA ON dbo.movimiento (fecha);
GO

/* ---------- REGLA: MAX 3 CUENTAS ---------- */
CREATE OR ALTER TRIGGER dbo.TR_CUENTA_MAX_3
ON dbo.cuenta
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM (
            SELECT c.id_cliente, COUNT(*) AS total_cuentas
            FROM dbo.cuenta c
            INNER JOIN (SELECT DISTINCT id_cliente FROM inserted) i ON i.id_cliente = c.id_cliente
            GROUP BY c.id_cliente
        ) x
        WHERE x.total_cuentas > 3
    )
    BEGIN
        RAISERROR('Un cliente no puede tener mas de 3 cuentas.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END;
GO

/* ---------- DATOS INICIALES AUTH ---------- */
INSERT INTO dbo.rol (codigo, nombre, estado) VALUES
('ROLE_CLIENTE', 'Cliente', 'A'),
('ROLE_DEPENDIENTE', 'Dependiente', 'A'),
('ROLE_CAJERO', 'Cajero', 'A'),
('ROLE_GERENTE_SUCURSAL', 'Gerente Sucursal', 'A'),
('ROLE_GERENTE_GENERAL', 'Gerente General', 'A');
GO

/* Usuario demo:
   username: admin
   password (BCrypt): password
*/
INSERT INTO dbo.usuario (username, password_hash, estado)
VALUES ('admin', '$2a$10$yAcTKQhLmqSx2s8mPwh7veuWocdFL2uodTQzzkPVghzMGKRicrqIW', 'A');
GO

INSERT INTO dbo.usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM dbo.usuario u
INNER JOIN dbo.rol r ON r.codigo = 'ROLE_GERENTE_GENERAL'
WHERE u.username = 'admin';
GO
