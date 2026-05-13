# Specification Pattern Implementation - Visual Reference Guide

## 📋 Document Overview

```
.agents/specification/
├── 01_PLANNING_REQUEST.md          ← Original requirements
├── 02_IMPLEMENTATION_PLAN.md       ← MAIN PLAN (67.6 KB)
├── SUMMARY.md                      ← Quick reference
└── VISUAL_REFERENCE.md             ← This file
```

---

## 🏗️ Architecture at a Glance

```
┌─────────────────────────────────────────────────────────────┐
│                    VOTIFY ARCHITECTURE                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  VIEWS (Vaadin UI)                                          │
│         ↓                                                   │
│  COMMANDS (Command Pattern)                                 │
│  ├─ Uses Specifications for validation                      │
│         ↓                                                   │
│  SERVICES (Business Logic)                                  │
│  ├─ Uses Specifications for queries                         │
│         ↓                                                   │
│  ⭐ SPECIFICATIONS (NEW - Query Encapsulation)              │
│  ├─ CompetitionByStatusSpecification                        │
│  ├─ ProjectsByCompetitionSpecification                      │
│  ├─ VotesByUserSpecification                                │
│  ├─ Composable: spec1.and(spec2).or(spec3)                  │
│         ↓                                                   │
│  REPOSITORIES (Spring Data JPA)                             │
│  ├─ findAll(specification)                                  │
│         ↓                                                   │
│  DATABASE (PostgreSQL/H2)                                   │
│         ↓                                                   │
│  OBSERVERS (Observer Pattern)                               │
│  ├─ Uses Specifications to query affected entities          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Package Structure

```
src/main/java/com/microslop/specification/
│
├── AbstractSpecification.java
│   └─ Base class for all specifications
│      • Provides and(), or(), not() methods
│      • Implements Spring Data JPA Specification<T>
│      • Fully documented with JavaDoc
│
├── competition/
│   ├── CompetitionByStatusSpecification.java
│   │   └─ Filter by active/inactive status
│   │      Complexity: Very Low | Effort: 15 min
│   │
│   └── CompetitionByCreatorSpecification.java
│       └─ Filter by creator username (case-insensitive)
│          Complexity: Low | Effort: 20 min
│
├── project/
│   ├── ProjectsByCategorySpecification.java
│   │   └─ Filter by category (many-to-many JOIN)
│   │      Complexity: Medium | Effort: 25 min
│   │
│   └── ProjectsByCompetitionSpecification.java
│       └─ Filter by competition (foreign key)
│          Complexity: Low | Effort: 15 min
│
├── vote/
│   └── VotesByUserSpecification.java
│       └─ Filter votes by user
│          Complexity: Low | Effort: 15 min
│
├── judge/
│   └── JudgesByCompetitionSpecification.java
│       └─ Filter judges by competition
│          Complexity: Low | Effort: 15 min
│
└── user/
    └── UsersByRoleSpecification.java
        └─ Filter by role (judge/participant)
           Complexity: Medium | Effort: 25 min

src/test/java/com/microslop/specification/
│
├── TestDataBuilder.java
│   └─ Utility for creating test data
│
├── competition/
│   ├── CompetitionByStatusSpecificationTest.java
│   │   └─ Unit tests (with mocks)
│   │
│   └── CompetitionByStatusSpecificationIntegrationTest.java
│       └─ Integration tests (with database)
│
└── ... (similar for other specifications)
```

---

## 🎯 7 Concrete Specifications

### 1️⃣ CompetitionByStatusSpecification
```java
// Filter competitions by active/inactive status
Specification<Competition> spec = new CompetitionByStatusSpecification(true);
List<Competition> active = competitionRepository.findAll(spec);
```
- **Use Case:** Display only active competitions on dashboard
- **Complexity:** Very Low | **Effort:** 15 min
- **SQL:** `WHERE active = true`

### 2️⃣ CompetitionByCreatorSpecification
```java
// Filter competitions by creator username
Specification<Competition> spec = new CompetitionByCreatorSpecification("john_doe");
List<Competition> userComps = competitionRepository.findAll(spec);
```
- **Use Case:** Show competitions created by specific user
- **Complexity:** Low | **Effort:** 20 min
- **SQL:** `WHERE LOWER(created_by) = LOWER('john_doe')`

### 3️⃣ ProjectsByCategorySpecification
```java
// Filter projects by category (many-to-many)
Specification<Project> spec = new ProjectsByCategorySpecification(categoryId);
List<Project> projects = projectRepository.findAll(spec);
```
- **Use Case:** Show only "Innovation" category projects in voting view
- **Complexity:** Medium | **Effort:** 25 min
- **SQL:** `JOIN categories WHERE category.id = ?`

### 4️⃣ ProjectsByCompetitionSpecification
```java
// Filter projects by competition
Specification<Project> spec = new ProjectsByCompetitionSpecification(competitionId);
List<Project> projects = projectRepository.findAll(spec);
```
- **Use Case:** Get all projects submitted to a competition
- **Complexity:** Low | **Effort:** 15 min
- **SQL:** `WHERE competition_id = ?`

### 5️⃣ VotesByUserSpecification
```java
// Filter votes by user
Specification<Vote> spec = new VotesByUserSpecification(userId);
List<Vote> votes = voteRepository.findAll(spec);
```
- **Use Case:** Get all votes cast by a specific user
- **Complexity:** Low | **Effort:** 15 min
- **SQL:** `WHERE user_id = ?`

### 6️⃣ JudgesByCompetitionSpecification
```java
// Filter judges by competition
Specification<Judge> spec = new JudgesByCompetitionSpecification(competitionId);
List<Judge> judges = judgeRepository.findAll(spec);
```
- **Use Case:** Get all judges for a competition to apply vote multipliers
- **Complexity:** Low | **Effort:** 15 min
- **SQL:** `WHERE competition_id = ?`

### 7️⃣ UsersByRoleSpecification
```java
// Filter users by role (judge or participant)
Specification<User> spec = new UsersByRoleSpecification(UserRole.JUDGE);
List<User> judges = userRepository.findAll(spec);
```
- **Use Case:** Get all judges or all participants
- **Complexity:** Medium | **Effort:** 25 min
- **SQL:** `LEFT JOIN judges WHERE judge.id IS NOT NULL` (for judges)

---

## 🔄 Specification Composition

### Simple Composition
```java
// AND: Both conditions must be true
Specification<Competition> spec = 
    new CompetitionByStatusSpecification(true)
    .and(new CompetitionByCreatorSpecification("john_doe"));

List<Competition> results = competitionRepository.findAll(spec);
// SQL: WHERE active = true AND created_by = 'john_doe'
```

### OR Composition
```java
// OR: Either condition can be true
Specification<Competition> spec = 
    new CompetitionByStatusSpecification(true)
    .or(new CompetitionByStatusSpecification(false));

List<Competition> results = competitionRepository.findAll(spec);
// SQL: WHERE active = true OR active = false
```

### NOT Composition
```java
// NOT: Negate the condition
Specification<Competition> spec = 
    new CompetitionByStatusSpecification(true).not();

List<Competition> results = competitionRepository.findAll(spec);
// SQL: WHERE NOT (active = true)
```

### Complex Composition
```java
// Complex: Combine multiple operators
Specification<Competition> spec = 
    new CompetitionByStatusSpecification(true)
    .and(new CompetitionByCreatorSpecification("john_doe"))
    .or(new CompetitionByCreatorSpecification("jane_smith"));

List<Competition> results = competitionRepository.findAll(spec);
// SQL: WHERE (active = true AND created_by = 'john_doe') 
//      OR created_by = 'jane_smith'
```

---

## 📊 Implementation Timeline

```
PHASE 1: Foundation (2-3 days)
├─ Create AbstractSpecification base class
├─ Create package structure
├─ Verify repository compatibility
└─ Create test infrastructure
   └─ TestDataBuilder utility

PHASE 2: Core Specifications (3-4 days)
├─ CompetitionByStatusSpecification
├─ CompetitionByCreatorSpecification
├─ ProjectsByCompetitionSpecification
├─ ProjectsByCategorySpecification
├─ VotesByUserSpecification
├─ JudgesByCompetitionSpecification
└─ UsersByRoleSpecification

PHASE 3: Service Integration (3-4 days)
├─ Integrate into CompetitionServiceImpl
├─ Integrate into ProjectServiceImpl
├─ Integrate into VoteServiceImpl
├─ Integrate into JudgeServiceImpl
└─ Integrate into UserServiceImpl

PHASE 4: Command & Observer (2-3 days)
├─ Update Commands to use Specifications
├─ Update Observers to use Specifications
└─ Integration testing

PHASE 5: Documentation (1-2 days)
├─ Update CONTEXT.md
├─ Create developer guide
├─ Code review & refactoring
└─ Final testing

TOTAL: 11-16 days
```

---

## 🧪 Testing Strategy

### Unit Testing (No Database)
```java
@ExtendWith(MockitoExtension.class)
class CompetitionByStatusSpecificationTest {
    @Mock private Root<Competition> root;
    @Mock private CriteriaQuery<?> query;
    @Mock private CriteriaBuilder cb;
    
    @Test
    void testActiveSpecification() {
        // Test with mocks - no database needed
        CompetitionByStatusSpecification spec = 
            new CompetitionByStatusSpecification(true);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNotNull(result);
    }
}
```

### Integration Testing (With Database)
```java
@DataJpaTest
class CompetitionByStatusSpecificationIntegrationTest {
    @Autowired private CompetitionRepository competitionRepository;
    
    @Test
    void testFindActiveCompetitions() {
        // Test with real database
        Specification<Competition> spec = 
            new CompetitionByStatusSpecification(true);
        List<Competition> results = competitionRepository.findAll(spec);
        assertEquals(2, results.size());
    }
}
```

---

## 🚀 Integration Examples

### In Services
```java
// Before: Direct repository call
List<Competition> active = competitionRepository.findByActiveTrue();

// After: Using specification
Specification<Competition> spec = new CompetitionByStatusSpecification(true);
List<Competition> active = competitionRepository.findAll(spec);
```

### In Commands
```java
// Validate using specification
Specification<Vote> duplicateSpec = 
    new VotesByUserAndProjectSpecification(userId, projectId);
if (voteRepository.count(duplicateSpec) > 0) {
    throw new IllegalStateException("Vote already exists");
}
```

### In Observers
```java
// Query using specification
Specification<Project> spec = 
    new ProjectsByCompetitionSpecification(event.getCompetitionId());
List<Project> projects = projectRepository.findAll(spec);
// Recalculate rankings
```

---

## ⚠️ 8 Challenges & Mitigations

| # | Challenge | Mitigation |
|---|-----------|-----------|
| 1 | **Performance** | Use FETCH joins, add indexes, paginate results |
| 2 | **Query Complexity** | Create intermediate specs, limit nesting to 3 levels |
| 3 | **Maintenance** | Consistent naming, clear package structure, JavaDoc |
| 4 | **Documentation** | Comprehensive JavaDoc, developer guide, examples |
| 5 | **Edge Cases** | Validate inputs, handle nulls, add unit tests |
| 6 | **Backward Compatibility** | Keep old methods, gradual migration, deprecation warnings |
| 7 | **Testing** | Provide test utilities, comprehensive examples |
| 8 | **Pattern Integration** | Stateless design, dependency injection, clear docs |

---

## 📈 Effort Breakdown

```
AbstractSpecification base class:           30 min
CompetitionByStatusSpecification:           15 min
CompetitionByCreatorSpecification:          20 min
ProjectsByCategorySpecification:            25 min
ProjectsByCompetitionSpecification:         15 min
VotesByUserSpecification:                   15 min
JudgesByCompetitionSpecification:           15 min
UsersByRoleSpecification:                   25 min
─────────────────────────────────────────────────
Specifications Total:                      2 hours 40 min

Test Infrastructure:                        1 hour
Unit Tests (7 specs × 30 min):             3.5 hours
Integration Tests (7 specs × 30 min):      3.5 hours
─────────────────────────────────────────────────
Testing Total:                             8 hours

Service Integration:                        3 hours
Command Integration:                        1.5 hours
Observer Integration:                       1.5 hours
─────────────────────────────────────────────────
Integration Total:                         6 hours

Documentation & Review:                     2 hours
─────────────────────────────────────────────────
GRAND TOTAL:                               19 hours 10 min
                                           (11-16 days with other work)
```

---

## ✅ Success Criteria

- [ ] All 7 specifications implemented and tested
- [ ] All services updated to use specifications
- [ ] All commands updated to use specifications
- [ ] All observers updated to use specifications
- [ ] 100% test coverage for specifications
- [ ] No breaking changes to existing code
- [ ] CONTEXT.md updated with new pattern
- [ ] Developer guide created
- [ ] All tests passing
- [ ] Code review completed

---

## 🎓 Key Learnings

### Why Specification Pattern?
1. **Encapsulation** - Business rules in dedicated classes
2. **Reusability** - Specifications can be combined and reused
3. **Testability** - Easy to test without database
4. **Maintainability** - Clear separation of concerns
5. **Composability** - Build complex queries from simple specs

### Best Practices
1. Keep specifications stateless and immutable
2. Validate inputs in constructors
3. Use descriptive names following convention
4. Document with JavaDoc and examples
5. Test both unit and integration
6. Compose specs for complex queries
7. Handle null values explicitly
8. Use pagination for large result sets

---

## 📚 References

- **Spring Data JPA Specification:** `org.springframework.data.jpa.domain.Specification<T>`
- **JPA Criteria API:** `jakarta.persistence.criteria.*`
- **Project Context:** `CONTEXT.md` in project root
- **Planning Request:** `01_PLANNING_REQUEST.md`
- **Implementation Plan:** `02_IMPLEMENTATION_PLAN.md`

---

## 🔗 Quick Links

| Document | Purpose |
|----------|---------|
| `02_IMPLEMENTATION_PLAN.md` | Complete implementation guide (67.6 KB) |
| `SUMMARY.md` | Executive summary and quick reference |
| `VISUAL_REFERENCE.md` | This file - visual overview |

---

**Status:** ✅ Ready for Implementation  
**Created:** May 13, 2026  
**Version:** 1.0
