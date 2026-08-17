package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.serialization.CardSerialization
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Whether an ability's typed rules data is bound to the individual player that caused it to
 * trigger. This is deliberately structural rather than a search of generated rules text: shared
 * team turns need this distinction for CR 805.4d, while descriptions are only presentation.
 */
val TriggeredAbility.referencesTriggeringPlayer: Boolean
    get() = referencesTriggeringPlayer(CardSerialization.compactJson.encodeToJsonElement(TriggeredAbility.serializer(), this))

private val playerReferenceFields = setOf(
    "player", "players", "payer", "chooser", "attacker", "defender", "participant",
    "participants", "eligiblePlayers", "affected", "sacrificedBy", "placedBy", "tapper",
    "attachmentController", "controller", "owner", "recipient", "targetPlayer",
)

private val triggeringPlayerTypeMarkers = setOf(
    "TriggeringPlayer", "TriggeringPlayerIs", "ControlledByTriggeringPlayer", "OwnedByTriggeringPlayer",
)

private val presentationFields = setOf("description", "descriptionOverride", "oracleText")

private fun referencesTriggeringPlayer(element: JsonElement, fieldName: String? = null): Boolean = when (element) {
    is JsonArray -> element.any { referencesTriggeringPlayer(it) }
    is JsonObject -> element.any { (key, value) ->
        // Human text is not rules data and must never decide trigger multiplicity.
        key !in presentationFields &&
            referencesTriggeringPlayer(value, key)
    }
    is JsonPrimitive -> element.isString && when (fieldName) {
        in playerReferenceFields -> element.content == "TriggeringPlayer"
        "type" -> element.content in triggeringPlayerTypeMarkers
        else -> false
    }
}
