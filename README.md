## Housing Management System
An Android app for landlords to record tenants, see total rent, water bills and electricity bills  and call tenants.

## Download
Latest APK: see **Releases** on the right of this page.

## Features
- Register and log in
- Dashboard with number of tenants and total rent
- Add tenants and view all tenants (saved with Room)
- Call a tenant, options menu and navigation drawer

## Architecture
- `ui/` Activities, Fragments and TenantViewModel (UI layer)
- `data/` Tenant, TenantDao, TenantDatabase, TenantRepository (data layer)

Screens → TenantViewModel (LiveData) → TenantRepository → Room

## Team
| Stage | Member | Admission No. | GitHub Username | Pull Request |
| :--- | :--- | :--- | :--- | :--- |
| 1 Lead | Alexia Natasha | 191651 | @Alexia191651 | Direct push to main |
| 2 ViewModel | Amanda Atamba | 184167 | @amanda-atamba | [PR Link](paste-pr-url-here) |
| 3 Room | Kevin Kamunya | 183508 | @MALAWIA | [PR Link](paste-pr-url-here) |
| 4 Screens | Amanda Atamba | 184167 | @amanda-atamba | [PR Link](paste-pr-url-here) |
| 5 Data integration | Vivian Mbocu | 189724 | @Mbocu | [PR Link](paste-pr-url-here) |
| 6 Release | Alexia Natasha | 191651 | @Alexia191651 | [PR Link](paste-pr-url-here) |
