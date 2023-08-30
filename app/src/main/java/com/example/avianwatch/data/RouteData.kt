package com.example.avianwatch.data

import java.util.UUID

data class RouteData(
    val routeId: UUID,
    val startLocationLatitude: Double,
    val startLocationLongitude: Double,
    val endLocationLatitude: Double,
    val endLocationLongitude: Double,
    val routeSteps: List<StepData>
)