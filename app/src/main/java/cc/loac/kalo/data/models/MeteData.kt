package cc.loac.kalo.data.models

data class MetaData(
    val name: String,
    val finalizers: List<String>,
    val labels: Map<String, String>,
    val annotations: Map<String, String>,
    val version: Int,
    val creationTimestamp: String
)