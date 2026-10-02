package me.dizzykitty3.androidtoolkitty.utils

import kotlin.math.roundToInt

internal const val CUSTOM_VOLUME_SLOT_COUNT = 3

enum class VolumeSaveResult { SAVED, INVALID, DUPLICATE, FAILED }

internal fun volumeSlotStep(percent: Int?, maxVolume: Int): Int? {
    if (percent == null || percent !in 1..100 || maxVolume <= 0) return null
    return (percent / 100.0 * maxVolume).roundToInt().takeIf { it in 1..maxVolume }
}

// A changed audio route can make previously distinct percentages collide.
// Keep the first usable slot; conflicting slots must be edited before use.
internal fun effectiveVolumeSlots(slots: List<Int?>, maxVolume: Int): List<Int?> {
    val usedSteps = mutableSetOf<Int>()
    return List(CUSTOM_VOLUME_SLOT_COUNT) { index ->
        val percent = slots.getOrNull(index)
        val step = volumeSlotStep(percent, maxVolume)
        percent.takeIf { step != null && usedSteps.add(step) }
    }
}

internal fun validateVolumeSlot(
    slot: Int,
    percent: Int,
    maxVolume: Int,
    slots: List<Int?>
): VolumeSaveResult {
    if (slot !in 0 until CUSTOM_VOLUME_SLOT_COUNT) return VolumeSaveResult.INVALID
    val step = volumeSlotStep(percent, maxVolume) ?: return VolumeSaveResult.INVALID
    return if (slots.take(CUSTOM_VOLUME_SLOT_COUNT).withIndex().any {
            it.index != slot && volumeSlotStep(it.value, maxVolume) == step
        }) VolumeSaveResult.DUPLICATE else VolumeSaveResult.SAVED
}
