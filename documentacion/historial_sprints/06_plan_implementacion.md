# Análisis y Plan de Implementación: Flujo Jerárquico de Tareas

## Análisis de la Situación Actual

He revisado tu [schema.prisma](file:///c:/chaosGate_FCT/cg-clockwork/prisma/schema.prisma) y los archivos JSON exportados de tu base de datos Supabase ([policies.json](file:///c:/chaosGate_FCT/cg-clockwork/prisma/policies.json), [views.json](file:///c:/chaosGate_FCT/cg-clockwork/prisma/views.json), [funciones.json](file:///c:/chaosGate_FCT/cg-clockwork/prisma/funciones.json)).

### 1. Políticas RLS Actuales ([policies.json](file:///c:/chaosGate_FCT/cg-clockwork/prisma/policies.json))
Tus políticas actuales para la tabla `tasks` son bastante sólidas, pero **no contemplan un flujo de escalado**:
- **read_tasks_policy**: Pueden ver la tarea el `admin`, los miembros de la unidad de la tarea (`unit_members`), y el líder de esa unidad (`units.leader_id`).
- **tasks: actualización por asignado y liderazgo**: Pueden actualizar la tarea el asignado (`assignee_id`), el líder de la unidad, o roles `admin`/`hr`.

**El Bloqueo:** Según estas políticas, un `Lead` que lidera a un `Coordinator` **no puede editar/revisar** una tarea de la unidad del coordinador, a menos que el `Lead` esté añadido explícitamente a `unit_members` de esa unidad inferior (lo cual rompe la idea de una jerarquía limpia donde el Lead ve automáticamente lo de sus subordinados).

### 2. Vistas ([views.json](file:///c:/chaosGate_FCT/cg-clockwork/prisma/views.json))
La vista `tasks_with_details` une la tarea con el assignee y el coordinador (que extrae de `u.leader_id`). No hay soporte en la vista para extraer quién es el `Lead` o el `Director` vinculado a ese `leader_id`, porque en el schema actual las unidades (`units`) tienen un solo `leader_id` y **no hay una tabla que defina la jerarquía de usuarios** (quién es el jefe de quién).

### 3. Funciones ([funciones.json](file:///c:/chaosGate_FCT/cg-clockwork/prisma/funciones.json))
Tienes funciones como `fn_validate_task_closure_evidence` que se aseguran de que haya actividad al cerrar una tarea. Esta función ya usa una lógica de "salto" de reglas si eres `admin`, `lead`, o `coordinator`:
```sql
IF NOT v_has_evidence AND NOT EXISTS (
    SELECT 1 FROM public.profiles 
    WHERE id = auth.uid() AND rol IN ('admin', 'lead', 'coordinator')
) THEN ...
```

---

## El Problema Principal: Falta un Árbol Jerárquico

Para aplicar la regla: *"El Lead puede ver/editar las tareas de los Coordinators que dirige"*, la base de datos **necesita saber a quién dirige ese Lead**.

En tu schema actual:
- Un `user` pertenece a un `unit_members` y la `unit` tiene un `leader_id` (Que actúa de Coordinator).
- **Pero no hay ninguna relación en el schema que diga "El Coordinator X reporta al Lead Y, y el Lead Y reporta al Director Z".**

Sin esto, no podemos crear una política RLS que diga: "Permite el UPDATE si el usuario actual es el Lead asignado a este Coordinator".

---

## Plan de Implementación Propuesto

Para hacer viable el control de revisiones jerárquico, necesitamos 3 pasos:

### Paso 1: Establecer la Cadena de Mando en el Schema

Debemos añadir una forma de saber quién manda sobre quién. La forma más sencilla y estándar es añadir un `manager_id` (o `reports_to`) en la tabla `profiles`.

**Cambio en [schema.prisma](file:///c:/chaosGate_FCT/cg-clockwork/prisma/schema.prisma):**
```prisma
model profiles {
  // ... campos actuales ...
  
  // Relación de jerarquía (Quién es el jefe directo de este usuario)
  manager_id       String?    @db.Uuid
  manager          profiles?  @relation("EmployeeManager", fields: [manager_id], references: [id])
  subordinates     profiles[] @relation("EmployeeManager")
}
```

### Paso 2: Ampliar los Estados de Tarea y Trazadores

Como comentamos antes, para poder gestionar el flujo de aprobación, la tabla `tasks` y el enum deben reflejar las fases de revisión, indicando quién validó cada paso.

**Cambio en [schema.prisma](file:///c:/chaosGate_FCT/cg-clockwork/prisma/schema.prisma):**
```prisma
enum objective_status {
  pending
  in_process
  blocked
  // Nuevos estados de revisión
  review_coordinator
  review_lead
  review_director
  fulfilled
  not_fulfilled
}

model tasks {
  // ... campos actuales ...
  status            objective_status?   @default(pending)
  
  // Rastrear quién aprobó (Opcional pero recomendado para UI y auditoría)
  coord_reviewer_id String?   @db.Uuid 
  lead_reviewer_id  String?   @db.Uuid 
  dir_reviewer_id   String?   @db.Uuid 
  
  coord_approved_at DateTime? @db.Timestamptz(6)
  lead_approved_at  DateTime? @db.Timestamptz(6)
  // completed_at ya sirve para la aprobación final del director
}
```

### Paso 3: Actualizar las Políticas RLS de Tareas (SQL)

Una vez que la base de datos sabe quién es el jefe de quién (`manager_id`), podemos crear una **función recursiva** o directa en SQL que verifique si el usuario actual (`auth.uid()`) está en la cadena de mando por encima del creador de la tarea.

Se modificarán las políticas `read_tasks_policy` y `tasks: actualización por asignado y liderazgo` para incluir esta función.

Por ejemplo, la lógica de actualización permitiría editar la tarea si:
1. Eres el `assignee_id`.
2. O eres el `leader_id` de la `unit`.
3. O eres el `manager_id` del `leader_id` (Lead).
4. O eres el `manager_id` del `manager_id` del `leader_id` (Director).
5. O eres `admin`.

## User Review Required

> [!WARNING]
> Tu base de datos actual no tiene una estructura que relacione a los líderes/gerentes entre sí. Para que el sistema sepa qué Lead manda sobre qué Coordinador, debemos añadir un campo `manager_id` a `profiles`.
> ¿Estás de acuerdo con añadir esta relación jerárquica a la tabla `profiles` y los nuevos estados a `objective_status`?

## Proposed Changes

### Database Schema (Prisma)
- Modificación del enum `objective_status` para añadir `review_coordinator`, `review_lead`, `review_director`.
- Modificación de `profiles` para añadir auto-referencia `manager_id`.
- Modificación opcional de `tasks` para llevar registro en DB de `coord_approved_at` y `lead_approved_at`.

### Supabase Policies & Functions (SQL)
- Creación de una función SQL `is_in_management_chain(user_id, manager_to_check)` para resolver la jerarquía.
- Actualización de las políticas RLS `UPDATE` y `SELECT` de la tabla `tasks` para incorporar la función de cadena de mando.
