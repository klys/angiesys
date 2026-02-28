# angiesys

Legacy Java Swing desktop ERP/invoicing system (NetBeans/Ant project) with SQLite storage and JasperReports-based document generation.

## Overview

This project appears to be a point-of-sale/admin desktop app with modules for:

- Facturación (invoices)
- Cotizaciones (quotes)
- Productos
- Proveedores
- Clientes
- Usuarios
- Configuraciones
- Reportes

Main app package: `src/angiesys`

## Tech Stack

- Java SE (Swing UI)
- NetBeans GUI forms (`.form` files)
- Ant build (`build.xml`)
- SQLite (`sqlite-jdbc-3.8.7.jar`, local `data`/`data.s3db`)
- JasperReports + iText + Apache POI libs for reporting/export

## Key Entry Points

- `src/angiesys/AngieSys.java`
  - launches login/session window (`sesionario`)
- `src/angiesys/principal.java`
  - main menu/dashboard and module navigation
- `src/angiesys/sqlite.java`
  - simple DB helper for CRUD operations
- `src/angiesys/conf.java`
  - configuration reader (`configuracion` table)

## Project Structure

- `src/angiesys/` — source code + form definitions
- `src/images/` — UI assets/icons
- `src/reportes/` — Jasper templates and generated report files
- `lib/` — bundled dependencies
- `build/` — compiled artifacts/classes
- `nbproject/` — NetBeans project metadata

## Build & Run

### NetBeans
1. Open project folder in NetBeans.
2. Build and run project.

### Ant (CLI)
```bash
ant clean
ant jar
```

Then run generated jar from `dist/` (if produced by local build config).

## Notes

- Repository includes compiled artifacts and binary dependencies committed in-tree.
- `sqlite.java` currently builds SQL with string concatenation (legacy pattern).

## Suggested Improvements

- Add explicit Java version requirement and tested OSes
- Migrate SQL operations to prepared statements
- Separate generated build outputs from source control
- Add a database schema doc and sample bootstrap credentials
- Add user guide for module workflows (invoice/quote lifecycle)
