# Performance Test

| Item | Value |
| --- | --- |
| Backend | Render Free tier |
| Database | Neon Free tier (PostgreSQL) |
| Region | Singapore |
| Load testing | Grafana Cloud k6, Singapore |
| Endpoint | `GET https://spotifi-vtps.onrender.com/track/all` |
| Tested commit | `0694ee1d101039b5bc640a792a2cbafba8dc64a0` |

## Published Free Tier Limits (2026-10-04)

| Resource                  | Render Free Web Service                  | Neon Free PostgreSQL           |
|---------------------------|------------------------------------------|--------------------------------|
| CPU                       | 0.1 CPU                                  | 0.25 - 2 CU                    |
| RAM                       | 512 MB                                   | N/A                            |
| Storage                   | Ephemeral filesystem; no persistent disk | 1 GB per project               |
| Monthly compute allowance | 750 instance-hours per workspace         | 100 CU-hours per project       |
| Idle timeout              | Spins down after 15 minutes              | Scales to zero after 5 minutes |

Neon compute size during the test: not recorded.

References: [Render compute](https://render.com/docs/compute-plans), [Render Free limits](https://render.com/docs/free), [Neon Free plan update](https://neon.com/blog/neon-free-plan-1-gb-per-project), [Neon compute and idle behavior](https://neon.com/docs/manage/endpoints/).
