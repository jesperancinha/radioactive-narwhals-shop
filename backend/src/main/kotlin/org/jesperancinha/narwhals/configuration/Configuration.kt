package org.jesperancinha.narwhals.configuration

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
internal class NarwhalWebShopConfiguration {
    @Bean
    fun hazelcastNarwhalsInstance(): HazelcastInstance =
        Config().apply { clusterName = "radioactiveNarwhalsCluser" }.let { Hazelcast.newHazelcastInstance(it) }
}
