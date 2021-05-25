# di-auth-oidc-provider

A Dropwizard-based OpenID Connect provider

# Database Migrations

- Database schema scripts are stored in `sql/` using Flyway naming conventions (e.g. `V*_<description>.sql` or `R_<description>.sql`)
- Scripts are automatically applied when started locally in Docker Compose.
- To apply to a PaaS environment execute `cf conduit <service-name> -- ./db-migrate.sh`

# Outcome
Based on discussions over the future of digital identity authentication we have decided to archive this repository and carry on development in [di-authentication-api](https://github.com/alphagov/di-authentication-api) and [di-authentication-frontend](https://github.com/alphagov/di-authentication-frontend) GitHub repositories.