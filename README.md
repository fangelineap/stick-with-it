# stick-with-it

Kotlin task tracker with:
- workspace management
- to-do tracking across all workspaces
- due-date alarm checks
- optional Supabase persistence

## Run locally

```bash
./gradlew test
./gradlew :app:run --args="add-workspace personal"
./gradlew :app:run --args="add-task <workspace-id> \"Pay bills\" 2026-07-01T08:00:00Z"
./gradlew :app:run --args="check-alarms"
```

## Use with Supabase

Set:
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`

Then create tables in Supabase:

```sql
create table if not exists workspaces (
  id text primary key,
  name text not null
);

create table if not exists tasks (
  id text primary key,
  workspace_id text not null references workspaces(id),
  title text not null,
  due_at timestamptz,
  completed boolean not null default false
);
```
