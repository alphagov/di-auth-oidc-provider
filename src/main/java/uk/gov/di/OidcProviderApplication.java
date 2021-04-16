package uk.gov.di;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.entity.Client;
import uk.gov.di.resources.AuthorisationResource;
import uk.gov.di.resources.LoginResource;
import uk.gov.di.resources.TokenResource;
import uk.gov.di.resources.UserInfoResource;
import uk.gov.di.services.ClientConfigService;
import uk.gov.di.services.ClientService;
import uk.gov.di.services.PostgresService;
import uk.gov.di.services.TokenService;
import uk.gov.di.services.UserValidationService;

import java.util.List;

public class OidcProviderApplication extends Application<OidcProviderConfiguration> {
    public static void main(String[] args) throws Exception {
        new OidcProviderApplication().run(args);
    }

    @Override
    public String getName() {
        return "oidc-provider";
    }

    @Override
    public void initialize(Bootstrap<OidcProviderConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>());
        bootstrap.addBundle(new AssetsBundle("/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/scripts", "/scripts", null, "js"));
        bootstrap.addBundle(new AssetsBundle("/assets/fonts", "/assets/fonts", null, "fonts"));
        bootstrap.addBundle(new AssetsBundle("/assets/images", "/assets/images", null, "images"));
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(OidcProviderConfiguration configuration, Environment env) {
        PostgresService postgresService = new PostgresService(configuration);
        configuration.getDatabase().setUrl(postgresService.getUri());
        var jdbiFactory = new JdbiFactory().build(env, configuration.getDatabase(), "postgresql");
        var clientConfigService = new ClientConfigService(jdbiFactory);
        var clientService =
                new ClientService(
                        List.of(
                                new Client(
                                        "some_client_id",
                                        "password",
                                        List.of("openid", "profile", "email"),
                                        List.of("code"),
                                        List.of(
                                                "https://di-auth-stub-relying-party.london.cloudapps.digital/oidc/callback",
                                                "http://localhost:8081/oidc/callback"))));
        env.jersey().register(new AuthorisationResource(clientService));
        env.jersey().register(new LoginResource(new UserValidationService()));
        env.jersey().register(new UserInfoResource());
        env.jersey().register(new TokenResource(new TokenService(configuration), clientService));
    }
}
