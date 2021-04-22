package uk.gov.di;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.glassfish.jersey.server.ServerProperties;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.configuration.OidcProviderConfiguration;
import uk.gov.di.resources.AuthorisationResource;
import uk.gov.di.resources.LoginResource;
import uk.gov.di.resources.RegistrationResource;
import uk.gov.di.resources.TokenResource;
import uk.gov.di.resources.UserInfoResource;
import uk.gov.di.services.AuthorizationCodeService;
import uk.gov.di.services.ClientConfigService;
import uk.gov.di.services.ClientService;
import uk.gov.di.services.PostgresService;
import uk.gov.di.services.TokenService;
import uk.gov.di.services.UserService;

public class OidcProviderApplication extends Application<OidcProviderConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(OidcProviderApplication.class);

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
        bootstrap.addBundle(new AssetsBundle("/js", "/js", null, "js"));
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
        var jdbiFactory = new JdbiFactory().build(env, configuration.getDatabase(), "postgresql");
        jdbiFactory.installPlugin(new PostgresPlugin());
        jdbiFactory.installPlugin(new Jackson2Plugin());
        var clientConfigService = new ClientConfigService(jdbiFactory);
        var authorizationCodeService = new AuthorizationCodeService();
        var clientService =
                new ClientService(clientConfigService.getClients(), authorizationCodeService);
        var userService = new UserService();
        var tokenService = new TokenService(configuration);

        env.jersey().register(new AuthorisationResource(clientService));
        env.jersey().register(new LoginResource(userService));
        env.jersey().register(new UserInfoResource(tokenService, userService));
        env.jersey().register(new TokenResource(tokenService, clientService, authorizationCodeService));
        env.jersey().register(new RegistrationResource(userService));
        env.jersey().property(ServerProperties.LOCATION_HEADER_RELATIVE_URI_RESOLUTION_DISABLED, true);
    }
}
