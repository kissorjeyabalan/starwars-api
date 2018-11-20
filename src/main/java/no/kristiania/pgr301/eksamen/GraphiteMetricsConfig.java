package no.kristiania.pgr301.eksamen;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Configuration
public class GraphiteMetricsConfig {
    @Bean
    public MetricRegistry getRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public GraphiteReporter getReporter(MetricRegistry registry) {
        String host = System.getenv("GRAPHITE_HOST");
        System.out.println("HOST IS " + host);
        String apiKey = System.getenv("HOSTEDGRAPHITE_KEY");
        System.out.println("API KEY IS " + apiKey);

        if (host == null || apiKey == null) {
            return null;
        }

        Graphite graphite = new Graphite(new InetSocketAddress(host, 2003));
        GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith(apiKey)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);

        reporter.start(1, TimeUnit.SECONDS);
        return reporter;
    }
}
