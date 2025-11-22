package jakoboeu.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import kotlin.math.pow
import kotlin.math.round

const val NUMBER_OF_DECIMALS = 3

data class Stats(
    @param:JsonProperty("mean")
    val mean: Double,
    @param:JsonProperty("median")
    val median: Double,
    @param:JsonProperty("mode")
    val mode: Double,
    @param:JsonProperty("standard deviation")
    val sd: Double,
    @param:JsonProperty("lower 0.25 quartile")
    val lowerIqr: Double,
    @param:JsonProperty("upper 0.75 quartile")
    val higherIqr: Double,
    @JsonIgnore
    val n: Int,
) {
    companion object {
        fun from(dataSet: List<Double>): Stats {
            val ds = DescriptiveStatistics()
            dataSet.forEach { ds.addValue(it) }

            val mean = ds.mean
            val median = ds.getPercentile(50.0)
            val sd = ds.standardDeviation

            // IQR = Q3 - Q1; but you want lower & higher quartiles
            val q1 = ds.getPercentile(25.0)
            val q3 = ds.getPercentile(75.0)

            // No built-in mode in DescriptiveStatistics, but there is a StatUtils helper
            val mode = org.apache.commons.math3.stat.StatUtils.mode(dataSet.toDoubleArray())
                .firstOrNull() ?: Double.NaN

            return Stats(
                mean = mean.round(NUMBER_OF_DECIMALS),
                median = median.round(NUMBER_OF_DECIMALS),
                mode = mode.round(NUMBER_OF_DECIMALS),
                sd = sd.round(NUMBER_OF_DECIMALS),
                lowerIqr = q1.round(NUMBER_OF_DECIMALS),
                higherIqr = q3.round(NUMBER_OF_DECIMALS),
                n = dataSet.size
            )
        }
    }
}

fun Double.round(decimals: Int = 2): Double {
    require(decimals >= 0)
    val factor = 10.0.pow(decimals.toDouble())
    return round(this * factor) / factor
}
