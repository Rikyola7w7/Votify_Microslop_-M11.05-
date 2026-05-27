# END_TIME_COMPETITION NOTIFICATION - DEBUGGING & VERIFICATION GUIDE

## IMMEDIATE DEBUGGING STEPS

### Step 1: Verify Scheduler is Running

**Check Application Logs:**
`
Look for these log messages starting when application starts:
- "Starting scheduled check for competitions closing soon"
- "Starting scheduled check for competitions that have ended"

If NOT present every 5 minutes, scheduler is not running.
`

**Add Temporary Log to Verify:**
In CompetitionNotificationScheduler.java, add at very start of method:
`java
@Scheduled(fixedRate = 300000)
@Transactional  // ADD THIS
public void checkAndNotifyCompetitionEndTime() {
    System.out.println("=== SCHEDULER RUNNING AT: " + LocalDateTime.now() + " ===");
    try {
        log.debug("Starting scheduled check for competitions that have ended");
        // ... rest of method
    }
}
`

---

### Step 2: Database Verification Queries

Run these SQL queries on the PostgreSQL database:

**Query 1: Check if end_notification_sent column exists**
`sql
SELECT column_name, data_type, column_default, is_nullable
FROM information_schema.columns
WHERE table_name='competition' 
  AND column_name='end_notification_sent';

-- Expected Result:
-- column_name: end_notification_sent
-- data_type: boolean
-- column_default: false
-- is_nullable: NO

-- If returns NO ROWS: Migration V8 didn't run!
`

**Query 2: Find VOTING_OPEN competitions with expired endDate**
`sql
SELECT 
    id, 
    name, 
    created_by, 
    end_date, 
    end_notification_sent, 
    status,
    CURRENT_TIMESTAMP AS now,
    (end_date < CURRENT_TIMESTAMP) AS is_expired
FROM competition
WHERE status = 'VOTING_OPEN'
  AND end_date IS NOT NULL
ORDER BY end_date DESC
LIMIT 20;

-- Look for:
-- - end_date < now (end_date in past)
-- - end_notification_sent = false (flag not set)
-- These should be processed by scheduler
`

**Query 3: Check if creators exist**
`sql
SELECT 
    c.id,
    c.name,
    c.created_by,
    u.id as user_id,
    u.username,
    CASE WHEN u.id IS NULL THEN 'MISSING' ELSE 'EXISTS' END as creator_status
FROM competition c
LEFT JOIN users u ON LOWER(u.username) = LOWER(c.created_by)
WHERE c.status = 'VOTING_OPEN' 
  AND c.end_date < CURRENT_TIMESTAMP
  AND c.end_notification_sent = false
LIMIT 20;

-- If creator_status = 'MISSING', that's the problem!
-- Scheduler cannot send notification to non-existent user
`

**Query 4: Check notifications table for END_TIME_COMPETITION**
`sql
SELECT 
    n.id,
    n.user_id,
    u.username,
    n.title,
    n.type,
    n.creation_date,
    n.is_read
FROM notifications n
LEFT JOIN users u ON n.user_id = u.id
WHERE n.type = 'END_TIME_COMPETITION'
ORDER BY n.creation_date DESC
LIMIT 20;

-- If returns 0 rows: No notifications created
-- If returns rows: Notifications were created
-- Check creation_date to see if recent
`

**Query 5: Find stuck competitions (old endDate, still VOTING_OPEN, flag false)**
`sql
SELECT 
    id,
    name,
    created_by,
    end_date,
    end_notification_sent,
    status,
    (CURRENT_TIMESTAMP - end_date) as hours_past_deadline
FROM competition
WHERE status = 'VOTING_OPEN'
  AND end_date < CURRENT_TIMESTAMP - INTERVAL '1 hour'
  AND end_notification_sent = false
ORDER BY end_date ASC;

-- If this returns many results, scheduler has not processed them
`

**Query 6: Transaction check**
`sql
-- Check for long-running transactions that might block scheduler
SELECT pid, usename, application_name, state, query_start, state_change
FROM pg_stat_activity
WHERE state != 'idle'
  AND query LIKE '%competition%'
LIMIT 10;

-- If results show stuck queries, might be blocking scheduler
`

---

### Step 3: Application Logs Analysis

**Enable Debug Logging:**
In application.properties:
`properties
logging.level.com.microslop.scheduled=DEBUG
logging.level.com.microslop.service=DEBUG
logging.level.com.microslop.repository=DEBUG
`

**What to Look For in Logs:**

`
1. Scheduler runs every 5 minutes:
   [TIMESTAMP] DEBUG ... - Starting scheduled check for competitions that have ended

2. Query returns results:
   [TIMESTAMP] DEBUG ... - Found N competitions in VOTING_OPEN status

3. Competition processed:
   [TIMESTAMP] INFO ... - Competition 123 has ended (endDate: 2024-01-15 14:00:00), notifying admin

4. Notification sent:
   [TIMESTAMP] INFO ... - Sent END_TIME_COMPETITION notification to admin for competition 123

5. Save completed:
   [TIMESTAMP] INFO ... - Saved competition 123 with status=CONCLUDED, endNotificationSent=true


If logs show:
- No scheduler messages: Scheduler not running
- Scheduler runs but no "has ended" messages: No expired competitions found
- "Notifying admin" but no "Sent notification": Notification creation failed
- No "Saved" message: Database save failed
`

---

### Step 4: Manual Scheduler Invocation for Testing

Create a test endpoint to manually trigger the scheduler:

`java
// Create file: src/main/java/com/microslop/controller/TestController.java

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private CompetitionNotificationScheduler scheduler;
    
    @PostMapping("/check-competition-end")
    public ResponseEntity<?> checkCompetitionEnd() {
        try {
            scheduler.checkAndNotifyCompetitionEndTime();
            return ResponseEntity.ok("Scheduler executed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
`

Then call manually:
`ash
curl -X POST http://localhost:8080/api/test/check-competition-end
`

Check logs immediately after for any errors.

---

## TEST CASE: MANUAL SETUP & VERIFICATION

### Create Test Data

**Step 1: Create a test user (admin)**
`sql
INSERT INTO users (username, name, email, password, creation_date, birth_date)
VALUES (
    'testadmin',
    'Test Admin',
    'testadmin@example.com',
    '$...bcrypted_password...',  -- BCrypt hash of "password123"
    CURRENT_TIMESTAMP,
    '1990-01-01'::timestamp
);

-- Get the ID
SELECT id FROM users WHERE username = 'testadmin';
-- Note the ID (let's say it's 1001)
`

**Step 2: Create a test competition that should have ended**
`sql
INSERT INTO competition (
    name,
    description,
    start_date,
    end_date,
    created_by,
    status,
    active,
    end_notification_sent,
    voter_type,
    auto_vote,
    max_votes_per_person,
    voting_strategy_type,
    ranking_strategy_type,
    comments_enabled,
    comments_required,
    max_votes
)
VALUES (
    'Test Competition - Should Notify',
    'This competition should trigger notification',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '1 hour',  -- Ended 1 hour ago
    'testadmin',  -- Must match username that exists in users table
    'VOTING_OPEN',  -- Must be in VOTING_OPEN state
    true,
    false,  -- Flag not yet set
    'ALL',
    false,
    1,
    'ALL',
    'AVERAGE',
    true,
    false,
    1
);

-- Get the ID
SELECT id FROM competition WHERE name = 'Test Competition - Should Notify';
-- Note the ID (let's say it's 5001)
`

**Step 3: Verify setup**
`sql
-- Verify competition exists with correct status and old endDate
SELECT id, name, created_by, end_date, status, end_notification_sent
FROM competition
WHERE id = 5001;

-- Verify creator exists
SELECT id, username
FROM users
WHERE username = 'testadmin';

-- Verify no notification sent yet
SELECT COUNT(*) as notification_count
FROM notifications
WHERE type = 'END_TIME_COMPETITION'
  AND user_id = (SELECT id FROM users WHERE username = 'testadmin');
`

### Run Scheduler & Verify Results

**Step 4: Trigger scheduler**
`ash
# Via API endpoint (if created)
curl -X POST http://localhost:8080/api/test/check-competition-end

# OR wait 5 minutes for scheduled execution
`

**Step 5: Check results**
`sql
-- Verify competition status changed
SELECT id, status, end_notification_sent
FROM competition
WHERE id = 5001;

-- Should show:
-- id: 5001
-- status: CONCLUDED
-- end_notification_sent: true

-- Verify notification was created
SELECT id, user_id, title, type, creation_date, is_read
FROM notifications
WHERE type = 'END_TIME_COMPETITION'
  AND user_id = (SELECT id FROM users WHERE username = 'testadmin')
ORDER BY creation_date DESC
LIMIT 1;

-- Should show:
-- title: "Competition Ended"
-- type: "END_TIME_COMPETITION"
-- is_read: false (or true, depending on user)
-- creation_date: recent timestamp
`

---

## TROUBLESHOOTING FLOWCHART

`
START: User reports no notification for ended competition

├─ CHECK 1: Is @EnableScheduling present?
│  └─ NO → Add to Application.java
│  └─ YES → Continue
│
├─ CHECK 2: Do logs show scheduler running every 5 minutes?
│  └─ NO → Check system clock, Spring context
│  └─ YES → Continue
│
├─ CHECK 3: Do logs show "competitions in VOTING_OPEN status"?
│  └─ NO → Check database for VOTING_OPEN competitions
│  └─ YES → Continue
│
├─ CHECK 4: Does database have VOTING_OPEN competitions with past endDate?
│  └─ NO → No data to process, expected behavior
│  └─ YES → Continue
│
├─ CHECK 5: Do logs show "Competition X has ended"?
│  └─ NO → Check endDate comparison logic, timezone issues
│  └─ YES → Continue
│
├─ CHECK 6: Do logs show "Sent END_TIME_COMPETITION notification"?
│  └─ NO → Continue (notification creation might have failed)
│  └─ YES → Go to CHECK 10
│
├─ CHECK 7: Does end_notification_sent column exist in database?
│  └─ NO → Run migration V8
│  └─ YES → Continue
│
├─ CHECK 8: Does competition.createdBy exist in users table?
│  └─ NO → Fix database data consistency
│  └─ YES → Continue
│
├─ CHECK 9: Are there errors in logs near "Error sending notification"?
│  └─ YES → Investigate specific error message
│  └─ NO → Check notification service code
│
└─ CHECK 10: Does notifications table have the record?
   └─ NO → Notification created but not persisted
   └─ YES → Notification system works! Check UI if not showing
`

---

## HYPOTHESIS TESTING

### Hypothesis 1: Scheduler Not Running
**Test:**
- Add System.out.println to checkAndNotifyCompetitionEndTime()
- Run application for 5 minutes
- Check console output

**Expected:** Message appears every 5 minutes
**If Not:** Scheduler disabled, check @EnableScheduling, check application startup

---

### Hypothesis 2: No Expired Competitions Found
**Test:**
`sql
SELECT COUNT(*) FROM competition 
WHERE status = 'VOTING_OPEN' AND end_date < CURRENT_TIMESTAMP;
`

**Expected:** Number > 0
**If 0:** Create test data with past endDate

---

### Hypothesis 3: Migration Not Run
**Test:**
`sql
\d competition
-- Look for end_notification_sent column
`

**Expected:** Column exists with type boolean
**If Not:** Run migration or manually:
`sql
ALTER TABLE competition ADD COLUMN IF NOT EXISTS end_notification_sent boolean DEFAULT false;
`

---

### Hypothesis 4: Creator Not Found
**Test:**
`sql
SELECT c.created_by, u.id
FROM competition c
LEFT JOIN users u ON LOWER(u.username) = LOWER(c.created_by)
WHERE c.status = 'VOTING_OPEN' 
  AND c.end_date < CURRENT_TIMESTAMP
  AND u.id IS NULL;
`

**Expected:** 0 rows
**If > 0:** Data integrity issue, fix competition.created_by values

---

### Hypothesis 5: Timezone Issue
**Test:**
- Set JVM timezone different from database timezone
- Create competition with endDate that's ambiguous at timezone boundary
- Run scheduler

**Expected:** Still processes correctly
**If Not:** Add explicit timezone handling

`java
LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
`

---

## PERMANENT FIXES CHECKLIST

Before declaring the issue resolved:

### Code Changes Required
- [ ] Add @Transactional to checkAndNotifyCompetitionEndTime()
- [ ] Add explicit timezone handling (ZoneId.of("UTC"))
- [ ] Add null checks for createdBy
- [ ] Improve exception handling for notification creation
- [ ] Add enhanced logging at critical points
- [ ] Add try-catch around state.conclude()
- [ ] Consider database-level query optimization

### Database Changes Required
- [ ] Verify V8 migration ran (end_notification_sent column exists)
- [ ] Fix any data consistency issues (missing creators)
- [ ] Add indexes on (status, endDate, endNotificationSent)

### Testing Required
- [ ] Create unit test for scheduler method
- [ ] Create integration test with test data
- [ ] Test timezone edge cases
- [ ] Test with missing creators
- [ ] Test with null endDate

### Monitoring Required
- [ ] Add metrics for notifications sent
- [ ] Monitor error rate from scheduler
- [ ] Alert if scheduler doesn't run for > 10 minutes
- [ ] Monitor notification delivery success rate

---

## RECOVERY FROM FAILURES

### If Notifications Didn't Send (Historical Data)

**Option 1: Manual Retry**
`sql
-- Reset failed competitions
UPDATE competition
SET end_notification_sent = false
WHERE id IN (1, 2, 3, ...)  -- List of competition IDs
  AND status = 'CONCLUDED';

-- Scheduler will retry next run
`

**Option 2: Bulk Retry via Code**
`java
// Create temporary service method
@Service
public class CompetitionNotificationRecoveryService {
    
    @Transactional
    public void retrySendEndNotifications(List<Long> competitionIds) {
        for (Long id : competitionIds) {
            Competition competition = competitionRepository.findById(id).orElse(null);
            if (competition != null && competition.getStatus() == CompetitionStatus.CONCLUDED) {
                var creator = userRepository.findByUsernameIgnoreCase(competition.getCreatedBy());
                if (creator.isPresent()) {
                    notificationService.createNotification(
                        creator.get(),
                        "Competition Ended",
                        "The competition '" + competition.getName() + "' has ended.",
                        NotificationType.END_TIME_COMPETITION.getCode());
                    
                    competition.setEndNotificationSent(true);
                    competitionRepository.save(competition);
                }
            }
        }
    }
}
`

### If Status Changed But Notification Not Sent

**Identify affected competitions:**
`sql
SELECT id, name, status, end_notification_sent
FROM competition
WHERE status = 'CONCLUDED' 
  AND end_notification_sent = false;
`

**Manually send notifications:**
Use the recovery service method above with the list of affected competition IDs.

