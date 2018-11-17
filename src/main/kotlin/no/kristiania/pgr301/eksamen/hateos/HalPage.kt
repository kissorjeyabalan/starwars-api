package no.kristiania.pgr301.eksamen.hateos
import com.fasterxml.jackson.annotation.JsonIgnore

open class HalPage<T>(
        var data: MutableList<T> = mutableListOf(),
        var pages: Int = 0,
        var count: Long = 0,
        next: HalLink? = null,
        previous: HalLink? = null,
        _self: HalLink? = null
) : HalObject() {
    @get:JsonIgnore
    var next: HalLink?
        set(value) {
            if (value != null) {
                _links["next"] = value
            } else {
                _links.remove("next")
            }
        }
        get() = _links["next"]
    @get:JsonIgnore
    var previous: HalLink?
        set(value) {
            if (value != null) {
                _links["previous"] = value
            } else {
                _links.remove("previous")
            }
        }
        get() = _links["previous"]
    @get:JsonIgnore
    var _self: HalLink?
        set(value) {
            if (value != null) {
                _links["self"] = value
            } else {
                _links.remove("self")
            }
        }
        get() = _links["self"]
    init {
        this.next = next
        this.previous = previous
        this._self = _self
    }
}