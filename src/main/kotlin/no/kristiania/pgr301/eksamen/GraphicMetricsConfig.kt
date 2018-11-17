package no.kristiania.pgr301.eksamen

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.graphite.Graphite
import com.codahale.metrics.graphite.GraphiteReporter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

@Configuration
class GraphicMetricsConfig {

    @Bean
    fun getRegistry(): MetricRegistry {
        return MetricRegistry()
    }

    @Bean
    fun getReporter(registry: MetricRegistry): GraphiteReporter {
        val graphite = Graphite(InetSocketAddress(System.getenv("GRAPHITE_HOST"), 2003))
        val graphiteReporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith(System.getenv("HOSTEDGRAPHITE_KEY"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite)

        graphiteReporter.start(1, TimeUnit.SECONDS)
        return graphiteReporter
    }
}