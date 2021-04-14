package uk.gov.di;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import uk.gov.di.configuration.OidcProviderConfiguration;
import io.dropwizard.Application;
import uk.gov.di.resources.AuthorisationResource;
import uk.gov.di.resources.LoginResource;
import uk.gov.di.resources.TokenResource;
import uk.gov.di.resources.UserInfoResource;
import uk.gov.di.services.TokenService;
import uk.gov.di.services.UserValidationService;

public class OidcProviderApplication extends Application<OidcProviderConfiguration>{
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
    }

    @Override
    public void run(OidcProviderConfiguration configuration, Environment env) {
        env.jersey().register(new AuthorisationResource());
        env.jersey().register(new LoginResource(new UserValidationService()));
        env.jersey().register(new UserInfoResource());
        env.jersey().register(new TokenResource(new TokenService(configuration)));
    }
}
