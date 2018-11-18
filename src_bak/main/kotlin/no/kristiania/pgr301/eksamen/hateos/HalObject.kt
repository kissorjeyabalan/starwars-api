package no.kristiania.pgr301.eksamen.hateos

import io.swagger.annotations.ApiModelProperty

open class HalObject (
        @ApiModelProperty("HAL Links")
        var _links: MutableMap<String, HalLink> = mutableMapOf()
)