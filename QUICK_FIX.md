# ⚡ Quick Fix Checklist

## The Error You're Seeing
```
Error navigating to: /competition/1001
Root cause: org.postgresql.util.PSQLException: ERROR: column v1_0.username does not exist
```

## Step-by-Step Fix (5 minutes)

### Step 1: Get the SQL Fix
- File location: `src/main/resources/fix-schema-quick.sql`
- This file contains all necessary SQL commands

### Step 2: Execute SQL Against Your Database
Choose your database tool:

**Using pgAdmin (GUI):**
1. Open pgAdmin
2. Connect to your database
3. Go to "Votify_Microslop" database → Query Tool
4. Copy-paste the contents of `fix-schema-quick.sql`
5. Click "Execute" button

**Using psql (Command Line):**
```bash
psql -U your_username -d votify_database -f src/main/resources/fix-schema-quick.sql
```

**Using DBeaver:**
1. Right-click on your database
2. SQL → SQL Script
3. Paste contents of `fix-schema-quick.sql`
4. Execute

### Step 3: Verify the Fix
In your database client, run:
```sql
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name='vote';
```

You should see: `username` (character varying, NOT NULL)

### Step 4: Restart Your Application
- Stop the Spring Boot application
- Start it again
- Navigate to `/competition/1001`
- It should work now!

## Troubleshooting

**Error: "user_project" table doesn't exist**
→ The fix will create it automatically

**Error: Permission denied**
→ You may need to run the SQL with a database admin user

**Still getting the same error after fix?**
→ Make sure you executed ALL the SQL commands
→ Check that column exists: `\d vote` (in psql)

## Related Files

- **SCHEMA_FIX_GUIDE.md** - Detailed explanation of the problem
- **LONG_TERM_IMPROVEMENT.md** - Optional: Better database design (for future)
- **fix-schema-quick.sql** - The actual SQL to execute
- **schema-migration.sql** - Comprehensive migration with extra checks

## Prevention for Future

To prevent similar issues, add to `application.properties`:
```properties
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.generate_statistics=true
```

This helps catch schema mismatches early.

---
**Once you apply the fix, the issue should be resolved!** ✅
