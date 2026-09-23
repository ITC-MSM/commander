package com.wingedsheep.engine.state

import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.EffectContinuation
import com.wingedsheep.engine.core.Suspension
import com.wingedsheep.engine.core.TriggerDamageDistributionContinuation
import com.wingedsheep.engine.core.TriggeredAbilityContinuation
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.engineSerializersModule
import com.wingedsheep.engine.event.TriggerContext
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * Games persisted before 2026-09-23 carried every trigger fact as a flat field on each carrier.
 * [LegacyTriggerContextLift] (run by [GameStateSerializer]) must move those keys into the nested
 * `triggerContext` record, or the permissive persistence Json would silently drop them.
 *
 * The old shape is produced here from an explicit table of the removed property names — the
 * table is the specification of the old format, independent of the lift's own tables.
 */
class LegacyTriggerContextLiftTest : FunSpec({
    val json = Json {
        serializersModule = engineSerializersModule
        allowStructuredMapKeys = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    val p1 = EntityId("player-1")
    val p2 = EntityId("player-2")
    val source = EntityId("source")
    val dying = EntityId("dying")
    val ability = EntityId("ability")

    // Every fact a carrier used to hold as its own field, with a non-default value.
    val record = TriggerContext(
        triggeringEntityId = dying,
        triggeringPlayerId = p2,
        damageAmount = 4,
        counterCount = 2,
        totalCounterCount = 3,
        targetingSourceEntityId = EntityId("warded-spell"),
        lastKnownPower = 5,
        lastKnownToughness = 6,
        diedBatchTotalPower = 11,
        lastKnownSubtypes = setOf("Elf", "Warrior"),
        lastKnownCardTypes = setOf("CREATURE"),
        lastKnownCounters = mapOf("+1/+1" to 2, "revival" to 1),
        lastKnownDamageDealtByPlayers = mapOf(p1 to 3),
        lastKnownBlockingOrBlockedByIds = listOf(EntityId("blocker")),
        modesChosenCount = 2,
        manaSpentOnTriggeringSpell = 7,
        colorsSpentOnTriggeringSpell = 3,
        manaValueOfTriggeringSpell = 6,
        xValueOfTriggeringSpell = 4,
        enchantedCreatureLastKnownPower = 8,
        scryCount = 2,
        clashWon = true,
        discardedCardCount = 3,
        discoverValue = 5,
        excessDamageAmount = 1,
        recipientToughnessAtDamage = 9,
        capturedEntityIds = listOf(EntityId("batch-a"), EntityId("batch-b")),
        unattachedFromEntityId = EntityId("old-host"),
    )
    // The facts each carrier actually had a field for (so the old shape could express them).
    val stackRecord = record
    val continuationRecord = record.copy(targetingSourceEntityId = null, unattachedFromEntityId = null, xValue = 2)
    val distributionRecord = record.copy(targetingSourceEntityId = null, unattachedFromEntityId = null)
    val contextRecord = record.copy(capturedEntityIds = null, minusOneMinusOneCounterCount = 1)

    fun decision(id: String) = YesNoDecision(id, p1, "?", DecisionContext(sourceId = source))

    val current = GameState(
        entities = mapOf(
            ability to ComponentContainer.of(
                TriggeredAbilityOnStackComponent(
                    sourceId = source, sourceName = "Source", controllerId = p1,
                    effect = Effects.GainLife(1), description = "gain", triggerContext = stackRecord, xValue = 3
                )
            )
        ),
        continuationStack = listOf(
            Suspension(
                decision("r0"),
                TriggerDamageDistributionContinuation(
                    sourceId = source, sourceName = "Source", controllerId = p1,
                    effect = Effects.GainLife(1), description = "divide", triggerContext = distributionRecord,
                    selectedTargets = listOf(ChosenTarget.Player(p2)), targetRequirements = emptyList(),
                    totalDamage = 3
                )
            ),
            EffectContinuation(
                remainingEffects = listOf(Effects.GainLife(1)),
                effectContext = EffectContext(
                    sourceId = source, controllerId = p1,
                    triggeringEntityId = dying, triggeringPlayerId = p2, xValue = 9,
                    triggerContext = contextRecord
                )
            ),
            Suspension(
                decision("r1"),
                TriggeredAbilityContinuation(
                    sourceId = source, sourceName = "Source", controllerId = p1,
                    effect = Effects.GainLife(2), description = "ping",
                    triggerContext = continuationRecord
                )
            ),
        ),
        nextRoutingId = 2,
    )

    // --- The old format, spelled out -------------------------------------------------------------
    val frameKeys = mapOf(
        "triggerDamageAmount" to "damageAmount", "triggeringEntityId" to "triggeringEntityId",
        "triggeringPlayerId" to "triggeringPlayerId", "triggerCounterCount" to "counterCount",
        "triggerTotalCounterCount" to "totalCounterCount", "triggerLastKnownCounters" to "lastKnownCounters",
        "triggerLastKnownSubtypes" to "lastKnownSubtypes", "triggerLastKnownCardTypes" to "lastKnownCardTypes",
        "triggerLastKnownDamageDealtByPlayers" to "lastKnownDamageDealtByPlayers",
        "triggerLastKnownBlockingOrBlockedByIds" to "lastKnownBlockingOrBlockedByIds",
        "lastKnownPower" to "lastKnownPower", "lastKnownToughness" to "lastKnownToughness",
        "diedBatchTotalPower" to "diedBatchTotalPower", "triggerModesChosenCount" to "modesChosenCount",
        "enchantedCreatureLastKnownPower" to "enchantedCreatureLastKnownPower", "triggerScryCount" to "scryCount",
        "triggerClashWon" to "clashWon", "triggerDiscardCount" to "discardedCardCount",
        "triggerDiscoverValue" to "discoverValue", "triggerExcessDamageAmount" to "excessDamageAmount",
        "triggerRecipientToughness" to "recipientToughnessAtDamage",
        "triggerManaSpentOnTriggeringSpell" to "manaSpentOnTriggeringSpell",
        "triggerColorsSpentOnTriggeringSpell" to "colorsSpentOnTriggeringSpell",
        "triggerManaValueOfTriggeringSpell" to "manaValueOfTriggeringSpell",
        "triggerXValueOfTriggeringSpell" to "xValueOfTriggeringSpell", "capturedEntityIds" to "capturedEntityIds",
    )
    val stackKeys = frameKeys + mapOf(
        "targetingSourceEntityId" to "targetingSourceEntityId",
        "triggerUnattachedFromEntityId" to "unattachedFromEntityId",
    )
    val continuationKeys = frameKeys + ("xValue" to "xValue")
    val effectContextKeys = mapOf(
        "triggerDamageAmount" to "damageAmount", "triggerCounterCount" to "counterCount",
        "triggerTotalCounterCount" to "totalCounterCount",
        "triggerMinusOneMinusOneCounterCount" to "minusOneMinusOneCounterCount",
        "triggerLastKnownSubtypes" to "lastKnownSubtypes", "triggerLastKnownCardTypes" to "lastKnownCardTypes",
        "targetingSourceEntityId" to "targetingSourceEntityId",
        "triggerUnattachedFromEntityId" to "unattachedFromEntityId", "triggerLastKnownPower" to "lastKnownPower",
        "triggerLastKnownToughness" to "lastKnownToughness", "triggerDiedBatchTotalPower" to "diedBatchTotalPower",
        "enchantedCreatureLastKnownPower" to "enchantedCreatureLastKnownPower",
        "triggerLastKnownCounters" to "lastKnownCounters",
        "triggerLastKnownDamageDealtByPlayers" to "lastKnownDamageDealtByPlayers",
        "triggerLastKnownBlockingOrBlockedByIds" to "lastKnownBlockingOrBlockedByIds",
        "triggerModesChosenCount" to "modesChosenCount",
        "triggerManaSpentOnTriggeringSpell" to "manaSpentOnTriggeringSpell",
        "triggerColorsSpentOnTriggeringSpell" to "colorsSpentOnTriggeringSpell",
        "triggerManaValueOfTriggeringSpell" to "manaValueOfTriggeringSpell",
        "triggerXValueOfTriggeringSpell" to "xValueOfTriggeringSpell", "triggerScryCount" to "scryCount",
        "triggerClashWon" to "clashWon", "triggerDiscardCount" to "discardedCardCount",
        "triggerDiscoverValue" to "discoverValue", "triggerExcessDamageAmount" to "excessDamageAmount",
        "triggerRecipientToughness" to "recipientToughnessAtDamage",
    )

    /** Rewrite a current-shape carrier into the pre-record shape: record keys become flat fields. */
    fun flatten(obj: JsonObject, keys: Map<String, String>): JsonObject {
        val nested = obj["triggerContext"] as? JsonObject ?: JsonObject(emptyMap())
        val flat = keys.mapValues { (oldKey, recordKey) ->
            nested[recordKey] ?: if (oldKey == "capturedEntityIds") JsonArray(emptyList()) else JsonNull
        }
        return JsonObject(obj - "triggerContext" + flat)
    }

    fun toOldShape(element: JsonElement): JsonElement = when (element) {
        is JsonArray -> JsonArray(element.map(::toOldShape))
        is JsonObject -> {
            val type = (element["type"] as? JsonPrimitive)?.content
            val flattened = when {
                type?.endsWith(".TriggeredAbilityOnStackComponent") == true -> flatten(element, stackKeys)
                type?.endsWith(".TriggeredAbilityContinuation") == true -> flatten(element, continuationKeys)
                type?.endsWith(".TriggerDamageDistributionContinuation") == true -> flatten(element, frameKeys)
                type == null && "controllerId" in element && "pipeline" in element -> flatten(element, effectContextKeys)
                else -> element
            }
            JsonObject(flattened.mapValues { (_, value) -> toOldShape(value) })
        }
        else -> element
    }

    fun encode(state: GameState): JsonObject = json.parseToJsonElement(json.encodeToString(state)).jsonObject

    test("an old-shape stack object, continuation and effect context decode to the same trigger facts") {
        val old = toOldShape(encode(current)).jsonObject
        // Guard the fixture itself: the old shape really has no record and really has the flat facts.
        old.toString().contains("\"triggerContext\"") shouldBe false
        old.toString().contains("\"triggerClashWon\":true") shouldBe true
        old.toString().contains("\"triggerLastKnownPower\":5") shouldBe true

        val decoded = json.decodeFromString<GameState>(old.toString())
        decoded shouldBe current
        decoded.getEntity(ability)!!.get<TriggeredAbilityOnStackComponent>()!!.triggerContext shouldBe stackRecord
        val context = (decoded.continuationStack[1] as EffectContinuation).effectContext
        // EffectContext kept its own triggering slots; the lift mirrors them into the record.
        context.triggeringEntityId shouldBe dying
        context.xValue shouldBe 9
        context.triggerContext shouldBe contextRecord
    }

    test("an old-shape carrier with no trigger facts decodes with no record") {
        val plain = current.copy(
            entities = mapOf(
                ability to ComponentContainer.of(
                    TriggeredAbilityOnStackComponent(
                        sourceId = source, sourceName = "Copy", controllerId = p1,
                        effect = Effects.GainLife(1), description = "copy"
                    )
                )
            ),
            continuationStack = listOf(
                EffectContinuation(listOf(Effects.GainLife(1)), EffectContext(sourceId = null, controllerId = p1))
            ),
            nextRoutingId = 0,
        )
        val old = toOldShape(encode(plain))
        old.toString().contains("\"triggerScryCount\":null") shouldBe true
        json.decodeFromString<GameState>(old.toString()) shouldBe plain
    }

    test("a current-shape document passes through the lift untouched") {
        val encoded = encode(current)
        LegacyTriggerContextLift.lift(encoded) shouldBeSameInstanceAs encoded
        json.decodeFromString<GameState>(encoded.toString()) shouldBe current
    }
})
