package uk.gov.di;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.di.configuration.OidcProviderConfiguration;
import io.dropwizard.Application;
import uk.gov.di.resources.HelloWorldResource;

public class OidcProviderApplication extends Application<OidcProviderConfiguration>{
    public static void main(String[] args) throws Exception {
        new OidcProviderApplication().run(args);
    }

    @Override
    public String getName() {
        return "oidc-provider";
    }

    @Override
    public void initialize(Bootstrap<OidcProviderConfiguration> bootstrap) {}

    @Override
    public void run(OidcProviderConfiguration configuration, Environment env) {
        env.jersey().register(new HelloWorldResource());
    }
}
