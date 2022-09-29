package com.github.serivesmejia.deltadrive.utils.task

import com.github.serivesmejia.deltamath.geometry.Rot2d

class Markers<T>(private val task: Task<T>) {

    // GENERAL MARKERS
    private val timeMarkers = mutableMapOf<Marker<T>, Double>()
    val timeMarkersCount get() = timeMarkers.size

    private val distanceMarkers = mutableMapOf<Marker<T>, Double>()
    val distanceMarkersCount get() = distanceMarkers.size

    private val rotationMarkers = mutableMapOf<Marker<T>, Rot2d>()
    val rotationMarkersCount get() = rotationMarkers.size

    fun timeMarker(timeSecs: Double, marker: Marker<T>) {
        timeMarkers[marker] = timeSecs
    }

    fun runTimeMarkers(elapsedSeconds: Double) {
        // time markers
        for((marker, time) in timeMarkers.entries.toTypedArray()) {
            if(elapsedSeconds >= time) {
                marker.run(task)
                timeMarkers.remove(marker)
            }
        }
    }

    fun distanceMarker(distance: Double, marker: Marker<T>) {
        distanceMarkers[marker] = distance
    }

    fun runDistanceMarkers(currentInches: Double) {
        // pose markers
        for((marker, distance) in distanceMarkers.entries.toTypedArray()) {
            if(distance >= currentInches) {
                marker.run(task)
                timeMarkers.remove(marker)
            }
        }
    }

    fun rotationMarker(rotation: Rot2d, marker: Marker<T>) {
        rotationMarkers[marker] = rotation
    }

    fun runRotationMarkers(currentRot: Rot2d) {
        // rot2d markers
        for((marker, rot) in rotationMarkers.entries.toTypedArray()) {
            if(rot.radians >= currentRot.radians) {
                marker.run(task)
                timeMarkers.remove(marker)
            }
        }
    }

    operator fun invoke(block: Markers<T>.() -> Unit): Task<T> {
        block()
        return task
    }

}