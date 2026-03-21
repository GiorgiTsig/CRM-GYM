Based on the codebase created during the previous module, implement follow REST API
(as a RestController):

# Gym CRM API

## 1. Trainee Registration
**Method:** `POST`

### Request
- First Name *(required)*
- Last Name *(required)*
- Date of Birth *(optional)*
- Address *(optional)*

### Response
- Username
- Password

---

## 2. Trainer Registration
**Method:** `POST`

### Request
- First Name *(required)*
- Last Name *(required)*
- Specialization *(required)* — Training Type reference

### Response
- Username
- Password

---

## 3. Login
**Method:** `GET`

### Request
- Username *(required)*
- Password *(required)*

### Response
- `200 OK`

---

## 4. Change Login
**Method:** `PUT`

### Request
- Username *(required)*
- Old Password *(required)*
- New Password *(required)*

### Response
- `200 OK`

---

## 5. Get Trainee Profile
**Method:** `GET`

### Request
- Username *(required)*

### Response
- First Name
- Last Name
- Date of Birth
- Address
- Is Active
- Trainers List
    - Trainer Username
    - Trainer First Name
    - Trainer Last Name
    - Trainer Specialization *(Training Type reference)*

---

## 6. Update Trainee Profile
**Method:** `PUT`

### Request
- Username *(required)*
- First Name *(required)*
- Last Name *(required)*
- Date of Birth *(optional)*
- Address *(optional)*
- Is Active *(required)*

### Response
- Username
- First Name
- Last Name
- Date of Birth
- Address
- Is Active
- Trainers List
    - Trainer Username
    - Trainer First Name
    - Trainer Last Name
    - Trainer Specialization *(Training Type reference)*

---

## 7. Delete Trainee Profile
**Method:** `DELETE`

### Request
- Username *(required)*

### Response
- `200 OK`

---

## 8. Get Trainer Profile
**Method:** `GET`

### Request
- Username *(required)*

### Response
- First Name
- Last Name
- Specialization *(Training Type reference)*
- Is Active
- Trainees List
    - Trainee Username
    - Trainee First Name
    - Trainee Last Name

---

## 9. Update Trainer Profile
**Method:** `PUT`

### Request
- Username *(required)*
- First Name *(required)*
- Last Name *(required)*
- Specialization *(read-only)* — Training Type reference
- Is Active *(required)*

### Response
- Username
- First Name
- Last Name
- Specialization *(Training Type reference)*
- Is Active
- Trainees List
    - Trainee Username
    - Trainee First Name
    - Trainee Last Name

---

## 10. Get Active Trainers Not Assigned to Trainee
**Method:** `GET`

### Request
- Username *(required)*

### Response
- Trainer Username
- Trainer First Name
- Trainer Last Name
- Trainer Specialization *(Training Type reference)*

---

## 11. Update Trainee's Trainer List
**Method:** `PUT`

### Request
- Trainee Username
- Trainers List *(required)*
    - Trainer Username *(required)*

### Response
- Trainers List
    - Trainer Username
    - Trainer First Name
    - Trainer Last Name
    - Trainer Specialization *(Training Type reference)*

---

## 12. Get Trainee Trainings List
**Method:** `GET`

### Request
- Username *(required)*
- Period From *(optional)*
- Period To *(optional)*
- Trainer Name *(optional)*
- Training Type *(optional)*

### Response
- Training Name
- Training Date
- Training Type
- Training Duration
- Trainer Name

---

## 13. Get Trainer Trainings List
**Method:** `GET`

### Request
- Username *(required)*
- Period From *(optional)*
- Period To *(optional)*
- Trainee Name *(optional)*

### Response
- Training Name
- Training Date
- Training Type
- Training Duration
- Trainee Name

---

## 14. Add Training
**Method:** `POST`

### Request
- Trainee Username *(required)*
- Trainer Username *(required)*
- Training Name *(required)*
- Training Date *(required)*
- Training Duration *(required)*

### Response
- `200 OK`

---

## 15. Activate / Deactivate Trainee
**Method:** `PATCH`

### Request
- Username *(required)*
- Is Active *(required)*

### Response
- `200 OK`

---

## 16. Activate / Deactivate Trainer
**Method:** `PATCH`

### Request
- Username *(required)*
- Is Active *(required)*

### Response
- `200 OK`

---

## 17. Get Training Types
**Method:** `GET`

### Request
- No data required

### Response
- Training Types
    - Training Type
    - Training Type ID

# Note
1. During Create Trainer/Trainee profile username and password should be generated as described in previous modules
2. Not possible to register as a trainer and trainee both
3. All functions except Create Trainer/Trainee profile. Should be executed only after
   Trainee/Trainer authentication (on this step should be checked username and password
   matching).
4. Implement required validation for each endpoint.
5. Users Table has parent-child (one to one) relation with Trainer and Trainee tables.
6. Training functionality does not include delete/update possibility via REST
7. Username cannot be changed.
8. Trainees and Trainers have many to many relations.
9. Activate/De-activate Trainee/Trainer profile not idempotent action.
10. Delete Trainee profile is hard deleting action and bring the cascade deletion of relevant
    trainings.
11. Training duration have a number type.
12. Training Date, Trainee Date of Birth have Date type.
13. Is Active field in Trainee/Trainer profile has Boolean type.
14. Training Types table include constant list of values and could not be updated from the
    application.
15. Implement error handling for all endpoints.
16. Cover code with unit tests.
17. Two levels of logging should be implemented:
    1. Transaction level (generate transactionId by which you can track all operations
       for this transaction the same transactionId can later be passed to downstream
       services)
    2. Specific rest call details (which endpoint was called, which request came and the
       service response - 200 or error and response message)
18. Implement error handling.
19. Document methods in RestController file(s) using Swagger 2 annotations.