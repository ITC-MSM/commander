package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.UnlockRoomDoor
import com.wingedsheep.engine.state.components.identity.RoomFaceId
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.UnholyAnnexRitualChamber
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Exercises [DynamicAmount.UnlockedDoors] with `distinctNames = true` — the Promising-Stairs shape
 * ("different names among unlocked doors"). This is genuinely separate evaluator logic from the raw
 * door total (proven by Rampaging Soulrager): it deduplicates by printed door-face name, so two
 * Rooms that share an unlocked face name count once.
 *
 * The probe is a 1/1 whose +10/+0 turns on at two or more *distinct* unlocked door names, read
 * under projection.
 */
class UnlockedDoorCountScenarioTest : FunSpec({

    val watcher = card("Distinct Door Watcher") {
        manaCost = "{1}"
        typeLine = "Creature — Spirit"
        power = 1
        toughness = 1
        staticAbility {
            ability = ConditionalStaticAbility(
                ability = ModifyStats(powerBonus = 10, toughnessBonus = 0, filter = GroupFilter.source()),
                condition = Compare(
                    DynamicAmount.UnlockedDoors(distinctNames = true),
                    ComparisonOperator.GTE,
                    DynamicAmount.Fixed(2),
                ),
            )
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(watcher)
        d.registerCard(UnholyAnnexRitualChamber)
        d.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Grizzly Bears" to 20),
            skipMulligans = true,
        )
        return d
    }

    test("one Room with both doors unlocked = two distinct door names → the probe is buffed") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val probe = d.putCreatureOnBattlefield(p1, watcher.name)

        val roomId = d.putCardInHand(p1, UnholyAnnexRitualChamber.name)
        d.giveMana(p1, Color.BLACK, 3)
        d.submitSuccess(CastSpell(p1, roomId, faceIndex = 0))
        d.bothPass()
        // Only "Unholy Annex" unlocked so far — one distinct name, no buff.
        d.state.projectedState.getPower(probe) shouldBe 1

        d.giveMana(p1, Color.BLACK, 5)
        d.submitSuccess(UnlockRoomDoor(p1, roomId, RoomFaceId("Ritual Chamber")))
        d.bothPass()
        // "Unholy Annex" + "Ritual Chamber" = two distinct names → +10/+0.
        d.state.projectedState.getPower(probe) shouldBe 11
    }

    test("two Rooms sharing one unlocked face name = two doors but one distinct name → no buff") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val probe = d.putCreatureOnBattlefield(p1, watcher.name)

        // Two copies of Unholy Annex // Ritual Chamber, each cast on its "Unholy Annex" face.
        val roomA = d.putCardInHand(p1, UnholyAnnexRitualChamber.name)
        d.giveMana(p1, Color.BLACK, 3)
        d.submitSuccess(CastSpell(p1, roomA, faceIndex = 0))
        d.bothPass()

        val roomB = d.putCardInHand(p1, UnholyAnnexRitualChamber.name)
        d.giveMana(p1, Color.BLACK, 3)
        d.submitSuccess(CastSpell(p1, roomB, faceIndex = 0))
        d.bothPass()

        // Two unlocked doors total, but both are named "Unholy Annex": distinct names = 1, so the
        // distinct-name gate (≥ 2) stays off even though the raw total is 2.
        d.state.projectedState.getPower(probe) shouldBe 1
    }
})
