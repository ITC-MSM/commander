package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.scripting.text.TextReplacer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data representation of the optional Commander zone replacement. The engine
 * supplies it only for CR 903.9b, while the sealed SDK hierarchy lets it
 * compete in the ordinary CR 614–616 replacement pipeline.
 */
@SerialName("CommanderZoneReplacement")
@Serializable
data class CommanderZoneReplacement(
    override val appliesTo: EventPattern = EventPattern.ZoneChangeEvent()
) : ReplacementEffect {
    override val description: String = "Put this commander into the command zone instead"
    override val optional: Boolean get() = true
    override val priorityGroup: ReplacementPriorityGroup get() = ReplacementPriorityGroup.ANY
    override fun applyTextReplacement(replacer: TextReplacer): ReplacementEffect = this
}
