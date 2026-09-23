package com.wingedsheep.engine.state

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Reads games persisted before trigger facts travelled as one
 * [com.wingedsheep.engine.event.TriggerContext] record.
 *
 * Until 2026-09-23 every carrier copied each fact into a flat field of its own —
 * `triggerDamageAmount`, `triggerScryCount`, `lastKnownPower`, `diedBatchTotalPower`, … on the
 * triggered-ability stack component, the two target-selection continuations and `EffectContext`.
 * Those carriers now hold a single `triggerContext` object instead, and the persistence Json
 * ignores unknown keys, so an in-flight game saved in the old shape would silently drop every
 * fact. This lift moves the old keys into the nested record (under the record's own property
 * names) before decoding. It only touches objects that are recognisably one of the old carriers
 * and that do not already carry a `triggerContext`, so a current-shape document passes through
 * unchanged.
 */
internal object LegacyTriggerContextLift {
    private const val RECORD = "triggerContext"

    /** Old flat key → [com.wingedsheep.engine.event.TriggerContext] property, shared by every carrier. */
    private val commonFacts = mapOf(
        "triggerDamageAmount" to "damageAmount",
        "triggerCounterCount" to "counterCount",
        "triggerTotalCounterCount" to "totalCounterCount",
        "triggerLastKnownCounters" to "lastKnownCounters",
        "triggerLastKnownSubtypes" to "lastKnownSubtypes",
        "triggerLastKnownCardTypes" to "lastKnownCardTypes",
        "triggerLastKnownDamageDealtByPlayers" to "lastKnownDamageDealtByPlayers",
        "triggerLastKnownBlockingOrBlockedByIds" to "lastKnownBlockingOrBlockedByIds",
        "triggerModesChosenCount" to "modesChosenCount",
        "enchantedCreatureLastKnownPower" to "enchantedCreatureLastKnownPower",
        "triggerScryCount" to "scryCount",
        "triggerClashWon" to "clashWon",
        "triggerDiscardCount" to "discardedCardCount",
        "triggerDiscoverValue" to "discoverValue",
        "triggerExcessDamageAmount" to "excessDamageAmount",
        "triggerRecipientToughness" to "recipientToughnessAtDamage",
        "triggerManaSpentOnTriggeringSpell" to "manaSpentOnTriggeringSpell",
        "triggerColorsSpentOnTriggeringSpell" to "colorsSpentOnTriggeringSpell",
        "triggerManaValueOfTriggeringSpell" to "manaValueOfTriggeringSpell",
        "triggerXValueOfTriggeringSpell" to "xValueOfTriggeringSpell",
    )

    /** The stack component and the continuations also held the identity, LKI and batch facts. */
    private val frameFacts = commonFacts + mapOf(
        "triggeringEntityId" to "triggeringEntityId",
        "triggeringPlayerId" to "triggeringPlayerId",
        "lastKnownPower" to "lastKnownPower",
        "lastKnownToughness" to "lastKnownToughness",
        "diedBatchTotalPower" to "diedBatchTotalPower",
        "capturedEntityIds" to "capturedEntityIds",
    )

    /** Keys removed per carrier type (by serial name — the class's FQCN discriminator). */
    private val carriers: Map<String, Map<String, String>> = mapOf(
        "com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent" to frameFacts + mapOf(
            "targetingSourceEntityId" to "targetingSourceEntityId",
            "triggerUnattachedFromEntityId" to "unattachedFromEntityId",
        ),
        // The trigger's own X lived on this frame as `xValue`; the stack component keeps its own
        // `xValue` (the resolving ability's X), so only the continuation moves it.
        "com.wingedsheep.engine.core.TriggeredAbilityContinuation" to frameFacts + ("xValue" to "xValue"),
        "com.wingedsheep.engine.core.TriggerDamageDistributionContinuation" to frameFacts,
    )

    /**
     * `EffectContext` is statically typed wherever it is serialized, so it has no discriminator; it is
     * recognised by `controllerId` plus at least one of these keys, none of which any other persisted
     * object declares. `triggeringEntityId` / `triggeringPlayerId` / `xValue` stay on `EffectContext`.
     */
    private val effectContextFacts = commonFacts + mapOf(
        "triggerMinusOneMinusOneCounterCount" to "minusOneMinusOneCounterCount",
        "targetingSourceEntityId" to "targetingSourceEntityId",
        "triggerUnattachedFromEntityId" to "unattachedFromEntityId",
        "triggerLastKnownPower" to "lastKnownPower",
        "triggerLastKnownToughness" to "lastKnownToughness",
        "triggerDiedBatchTotalPower" to "diedBatchTotalPower",
    )

    /** Returns [element] itself (no copy) when nothing in it needs lifting — the common case. */
    fun lift(element: JsonElement): JsonElement = when (element) {
        is JsonArray -> {
            val children = element.map(::lift)
            if (children.indices.all { children[it] === element[it] }) element else JsonArray(children)
        }
        is JsonObject -> {
            val lifted = liftObject(element)
            val children = lifted.mapValues { (_, value) -> lift(value) }
            if (lifted === element && children.all { (key, value) -> value === element[key] }) element
            else JsonObject(children)
        }
        else -> element
    }

    private fun liftObject(obj: JsonObject): JsonObject {
        if (RECORD in obj) return obj
        val type = (obj["type"] as? JsonPrimitive)?.takeIf { it.isString }?.content
        val carrierFacts = type?.let { carriers[it] }
        return when {
            carrierFacts != null -> move(obj, carrierFacts, alsoCopy = emptyList())
            type == null && "controllerId" in obj && effectContextFacts.keys.any { it in obj } ->
                // Mirror what `EffectContext.forTriggeredAbility` writes today: the as-fired
                // triggering entity/player sit in the record as well as on the context.
                move(obj, effectContextFacts, alsoCopy = listOf("triggeringEntityId", "triggeringPlayerId"))
            else -> obj
        }
    }

    private fun move(obj: JsonObject, facts: Map<String, String>, alsoCopy: List<String>): JsonObject {
        val record = LinkedHashMap<String, JsonElement>()
        for ((oldKey, recordKey) in facts) {
            val value = obj[oldKey] ?: continue
            if (value is JsonNull) continue
            if (oldKey == "capturedEntityIds" && value is JsonArray && value.isEmpty()) continue
            record[recordKey] = value
        }
        if (facts.keys.none { it in obj }) return obj
        val remaining = obj - facts.keys
        if (record.isEmpty()) return JsonObject(remaining)
        for (key in alsoCopy) {
            val value = obj[key]
            if (value != null && value !is JsonNull) record.putIfAbsent(key, value)
        }
        return JsonObject(remaining + (RECORD to JsonObject(record)))
    }
}
