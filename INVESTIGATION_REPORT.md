# COMPREHENSIVE INVESTIGATION: END_TIME_COMPETITION NOTIFICATION SYSTEM

## CRITICAL ISSUES FOUND

### ISSUE #1: SCHEDULER NOT BEING SAVED WITHIN TRANSACTION [CRITICAL]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java

**Problem:** 
The checkAndNotifyCompetitionEndTime() method LACKS the @Transactional annotation. This means:
- Each repository call is executed in its own transaction
- When competition.conclude() is called (lines 108), it changes the status in memory
- When competition.setEndNotificationSent(true) is called (line 125), it's set in memory
- When competitionRepository.save(competition) is called (line 136), it attempts to save AFTER the notification is created
- However, if the notification creation fails OR if there's a timing issue, the save might not include both changes

**Recommended Fix:**
Add @Transactional annotation to ensure atomic operations:
`java
@Transactional
@Scheduled(fixedRate = 300000)
public void checkAndNotifyCompetitionEndTime() {
    // method body
}
`

---

### ISSUE #2: QUERY LOGIC - STATUS FILTER MIGHT BE INSUFFICIENT [HIGH]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\repository\CompetitionRepository.java

**Current Query:**
Line 56 & 93: List<Competition> openCompetitions = competitionRepository.findByStatus(CompetitionStatus.VOTING_OPEN);

**Problem:**
- This query ONLY retrieves competitions with status = VOTING_OPEN
- If a competition's endDate has passed but its status is still VOTING_OPEN in the database, it will be found
- However, THERE'S NO INDEX on the (status, endDate) columns for efficient filtering
- The query retrieves ALL VOTING_OPEN competitions into memory, then filters in Java

**Recommended Improvement:**
Add a custom query method to filter at database level:
`java
@Query("SELECT c FROM Competition c WHERE c.status = com.microslop.entity.CompetitionStatus.VOTING_OPEN AND c.endDate < CURRENT_TIMESTAMP AND c.endNotificationSent = false")
List<Competition> findVotingOpenCompetitionsWithExpiredEndDate();
`

---

### ISSUE #3: TIMEZONE MISMATCH POTENTIAL [HIGH]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java (Line 90)

**Problem:**
`java
LocalDateTime now = LocalDateTime.now();  // Uses system default timezone
`

**Issues:**
1. LocalDateTime.now() uses the JVM's system timezone
2. Database stores times in PostgreSQL (which may have different timezone)
3. If server is in UTC but user endDate was stored in a different timezone, comparison may fail
4. No explicit timezone handling anywhere in the codebase

**Recommended Fix:**
`java
LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
// OR explicitly handle timezone from config
`

And in database schema (verify):
- Ensure columns are TIMESTAMP WITHOUT TIME ZONE (current) or TIMESTAMP WITH TIME ZONE
- Be consistent throughout

---

### ISSUE #4: ENDDATE COMPARISON EDGE CASE [MEDIUM]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java (Line 96)

**Current Logic:**
`java
if (competition.getEndDate() != null && competition.getEndDate().isBefore(now)) {
`

**Problem:**
- The comparison uses isBefore(now)
- This checks if endDate < now (strictly before)
- If endDate equals now (same second), the notification won't be sent
- The scheduler runs every 5 minutes (300,000ms), so timing windows could be missed

**Recommended Fix:**
`java
if (competition.getEndDate() != null && !competition.getEndDate().isAfter(now)) {
    // This includes both isBefore AND equal times
}
`

---

### ISSUE #5: FLAG INITIALIZATION AND PERSISTENCE [MEDIUM]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\entity\Competition.java (Lines 89-90)

**Current State:**
`java
@Column(name = "end_notification_sent", columnDefinition = "boolean default false")
private boolean endNotificationSent = false;
`

**Potential Issues:**
1. The flag defaults to FALSE in database schema (good)
2. BUT: The database migration might not have run on all instances
3. If end_notification_sent column doesn't exist, the query will fail silently
4. No validation that the flag was actually persisted to database

**Check:**
Run this SQL to verify the column exists:
`sql
SELECT column_name FROM information_schema.columns 
WHERE table_name='competition' AND column_name='end_notification_sent';
`

---

### ISSUE #6: USER LOOKUP CAN RETURN EMPTY [MEDIUM]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java (Lines 112-113)

**Problem:**
`java
var creator = userRepository.findByUsernameIgnoreCase(competition.getCreatedBy());
if (creator.isPresent()) {
    // sends notification
} 
// BUT if creator is NOT present, notification is NOT sent
// AND competition is still marked as concluded
`

**Issues:**
1. If competition.createdBy is NULL or doesn't exist in the database, no notification is sent
2. BUT the competition status is STILL changed to CONCLUDED at line 108
3. The endNotificationSent flag is only set if creator exists (line 125)
4. This means if creator doesn't exist, on next run, notification will be attempted again (infinite loop!)

**Check Database:**
Verify:
1. Does every competition have a createdBy field populated?
2. Does the createdBy username exist in the users table?

---

### ISSUE #7: STATE TRANSITION MIGHT FAIL [MEDIUM]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\state\VotingOpenCompetitionState.java (Line 18-20)

**Problem:**
`java
@Override
public void conclude(Competition competition) {
    competition.setStatus(CompetitionStatus.CONCLUDED);
}
`

**However:**
- VotingOpenCompetitionState CAN transition to CONCLUDED (line 18-20 exists)
- But what if another code path changes status first?
- No error handling if state transition fails

**Verified States That Support conclude():**
- ✓ ActiveCompetitionState (line 24-26)
- ✓ VotingOpenCompetitionState (line 18-20)
- ✓ PausedCompetitionState (line 19-21)
- ✗ DraftCompetitionState (throws IllegalStateException)
- ✗ ConcludedCompetitionState (throws IllegalStateException)
- ✗ ArchivedCompetitionState (throws IllegalStateException)

---

### ISSUE #8: NOTIFICATION MIGHT NOT BE CREATED [MEDIUM]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java (Lines 118-122)

**Current Code:**
`java
notificationService.createNotification(
        creator.get(),
        title,
        message,
        NotificationType.END_TIME_COMPETITION.getCode());
`

**Problem:**
1. The method creates a 4-parameter notification (no competition context)
2. Looks at NotificationServiceImpl, line 115-125 - this method works fine
3. However, the notification is created BEFORE setting endNotificationSent=true
4. If createNotification() throws exception, the flag never gets set, and scheduler tries again infinitely

**Issue in Exception Handling (Line 130):**
`java
} catch (Exception e) {
    log.error("Error sending END_TIME_COMPETITION notification...");
}
// Falls through to line 136 save() which will still save with incomplete state
`

**Better Pattern:**
`java
try {
    // notification creation
    competition.setEndNotificationSent(true);
} catch (Exception e) {
    log.error("Error...");
    // Don't set flag, so retry happens
}
// Save only if successful
competitionRepository.save(competition);
`

---

### ISSUE #9: LOGGING GAPS [MEDIUM]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java

**Missing Logs:**
1. No log when notification is successfully created (only at line 127)
2. No log showing the comparison result (endDate vs now)
3. No log for when creator not found
4. No log when endNotificationSent is already true (line 99 is debug level, should be info)
5. No log after competitive.save() completes

**Recommendation:**
Add logging at critical points:
`java
log.info("Processing {} competitions with expired endDate", openCompetitions.size());
log.debug("Competition {} endDate {} vs now {}", id, endDate, now);
log.warn("Creator not found for competition {}", competitionId);
log.info("Saved competition {} with endNotificationSent=true", id);
`

---

### ISSUE #10: NO RETRY MECHANISM [MEDIUM]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java

**Problem:**
- If notification creation fails (exception in try block lines 111-133)
- The outer catch at line 140 logs error
- But the endNotificationSent flag is NEVER set (it's inside the inner try)
- So on next 5-minute run, scheduler tries again
- If it's a persistent error (user doesn't exist, etc.), it will loop forever

**Better Approach:**
Separate notification creation from status update:
`java
boolean notificationSuccessful = sendNotification(...);
if (notificationSuccessful) {
    competition.setEndNotificationSent(true);
} else {
    // Don't set flag, retry on next run
    log.warn("Will retry notification for competition {}", id);
}
competitionRepository.save(competition);
`

---

### ISSUE #11: NULL CHECKS MISSING [LOW]
**File:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java

**Missing Checks:**
1. Line 93: competitionRepository.findByStatus() could return null (though unlikely with Spring)
2. Line 95: No null check on competition itself
3. Line 96: Check for null endDate exists, but...
4. Line 112: No null check on competition.getCreatedBy() before passing to findByUsernameIgnoreCase()

**Best Practice:**
`java
if (openCompetitions == null || openCompetitions.isEmpty()) {
    log.debug("No competitions found");
    return;
}

for (Competition competition : openCompetitions) {
    if (competition == null) continue;
    if (competition.getEndDate() == null) continue;
    if (competition.getCreatedBy() == null || competition.getCreatedBy().isBlank()) {
        log.warn("Competition {} has no creator", competition.getId());
        continue;
    }
    // ... rest of logic
}
`

---

## SUMMARY OF CRITICAL PATHS

### Path to NotificationNotBeingSent:

1. **Scheduler Execution:** ✓ Correctly configured with @EnableScheduling at Application level
   - File: C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\Application.java (Line 12)

2. **Scheduled Method Finding:** ✓ Found with correct @Scheduled(fixedRate=300000)
   - File: CompetitionNotificationScheduler.java (Line 85-86)

3. **Query Execution:** ✓ Query logic is correct (findByStatus)
   - File: CompetitionRepository.java (Line 20)
   - POTENTIAL: May need database-level filtering for performance

4. **End Date Comparison:** ✓ Logic is correct BUT has edge cases
   - Uses isBefore(now) which might miss equal times
   - Timezone not explicitly handled

5. **Flag Check:** ✓ endNotificationSent flag exists and defaults to false
   - Database column created in V8__Add_end_notification_sent_flag.sql
   - POTENTIAL: Migration might not have run

6. **User Lookup:** POTENTIAL ISSUE - If creator not found, notification not sent
   - Line 112-113: findByUsernameIgnoreCase with isPresent() check
   - If fails, flag never set, infinite retry loop

7. **Notification Creation:** ✓ Method exists and works
   - NotificationServiceImpl.createNotification() works correctly
   - POTENTIAL: Exception handling doesn't prevent flag setting

8. **Save Operation:** ✓ Save is called correctly
   - Line 136: competitionRepository.save(competition)
   - BUT: Missing @Transactional annotation on method

---

## DATABASE VERIFICATION CHECKLIST

Run these SQL queries to verify the system state:

`sql
-- Check if end_notification_sent column exists
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name='competition' AND column_name='end_notification_sent';

-- Check VOTING_OPEN competitions with passed endDate
SELECT id, name, created_by, end_date, end_notification_sent, status
FROM competition
WHERE status = 'VOTING_OPEN'
  AND end_date < CURRENT_TIMESTAMP
ORDER BY end_date DESC;

-- Check if creators exist
SELECT DISTINCT c.created_by, u.username, u.id
FROM competition c
LEFT JOIN users u ON LOWER(u.username) = LOWER(c.created_by)
WHERE c.status = 'VOTING_OPEN' AND c.end_date < CURRENT_TIMESTAMP;

-- Check notifications that were sent
SELECT id, user_id, title, type, creation_date
FROM notifications
WHERE type = 'END_TIME_COMPETITION'
ORDER BY creation_date DESC
LIMIT 10;

-- Check for competitions stuck in VOTING_OPEN with old endDate
SELECT id, name, end_date, end_notification_sent
FROM competition
WHERE status = 'VOTING_OPEN' 
  AND end_date < NOW() - INTERVAL '1 hour'
LIMIT 20;
`

---

## RECOMMENDED FIX PRIORITY

### P0 (Critical - Fix Immediately):
1. Add @Transactional to checkAndNotifyCompetitionEndTime()
2. Verify end_notification_sent column exists in database
3. Add explicit timezone handling (use ZoneId.of("UTC"))

### P1 (High - Fix Soon):
4. Fix edge case in endDate comparison (use isAfter instead of isBefore)
5. Add robust null checks for createdBy
6. Improve exception handling for notification creation

### P2 (Medium - Fix This Sprint):
7. Add database-level query optimization
8. Enhance logging for debugging
9. Add retry mechanism with maximum attempts

### P3 (Low - Nice to Have):
10. Add monitoring/alerting for failed notifications
11. Add unit tests for scheduler
12. Document timezone assumptions

---

## CODE LOCATIONS REFERENCE

- **Scheduler:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\scheduled\CompetitionNotificationScheduler.java
- **Entity:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\entity\Competition.java
- **Repository:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\repository\CompetitionRepository.java
- **NotificationService:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\java\com\microslop\service\impl\NotificationServiceImpl.java
- **Application Config:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\Application.java
- **Database Migration:** C:\Users\User\Documentos\GitHub\Votify_Microslop\src\main\resources\db\migration\V8__Add_end_notification_sent_flag.sql
