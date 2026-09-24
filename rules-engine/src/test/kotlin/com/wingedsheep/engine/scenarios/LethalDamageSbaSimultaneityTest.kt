package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.AffectsFilter
import com.wingedsheep.engine.mechanics.layers.ContinuousEffectData
import com.wingedsheep.engine.mechanics.layers.ContinuousEffectSourceComponent
import com.wingedsheep.engine.mechanics.layers.Modification
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.mechanics.sba.creature.LethalDamageCheck
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression test for the lethal-damage state-based action being order-dependent
 * (CR 704.3: SBAs are checked, then ALL applicable ones are performed simultaneously as a
 * single event; only afterward is the check repeated).
 *
 * The bug: [LethalDamageCheck] re-projected the Rule 613 board off the progressively-mutated
 * `newState` *inside* its per-creature loop. When an "anti-lord" — a creature whose static
 * gives all OTHER creatures -2/-2 (e.g. Elesh Norn, Grand Cenobite) — itself had lethal damage,
 * processing the lord first moved it to the graveyard and the next re-projection dropped its
 * -2/-2 from the board. A small creature that was only lethal *because* of that -2/-2 then read
 * as non-lethal on the re-projection and wrongly survived. The outcome depended on battlefield
 * iteration order.
 *
 * No implemented card carries a "-X/-Y to all other creatures" creature static yet (Elesh Norn
 * is unimplemented), so this drives the exact projection machinery a real anti-lord would feed:
 * a [ContinuousEffectSourceComponent] holding `ModifyPowerToughness(-2,-2)` over
 * [AffectsFilter.AllOtherCreatures], attached to a vanilla creature. That component is collected
 * per-battlefield-permanent by [StateProjector] the same way a card's static ability is — so a
 * lord leaving the battlefield drops the bonus on re-projection, which is the whole bug.
 *
 * The fix projects once from the original `state` before the loop (mirroring `ZeroToughnessCheck`),
 * so both creatures are determined lethal against the same board and both die — regardless of order.
 */
class LethalDamageSbaSimultaneityTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    /** Stamp a fixed amount of damage onto an entity (direct component write, no DamageEvent). */
    fun GameTestDriver.markDamage(entityId: EntityId, amount: Int) {
        replaceState(state.updateEntity(entityId) { c -> c.with(DamageComponent(amount)) })
    }

    /** Turn a permanent into an "anti-lord": all OTHER creatures get -2/-2 (Elesh Norn shape). */
    fun GameTestDriver.makeAntiLord(entityId: EntityId) {
        replaceState(state.updateEntity(entityId) { c ->
            c.with(
                ContinuousEffectSourceComponent(
                    effects = listOf(
                        ContinuousEffectData(
                            modification = Modification.ModifyPowerToughness(powerMod = -2, toughnessMod = -2),
                            affectsFilter = AffectsFilter.AllOtherCreatures
                        )
                    )
                )
            )
        })
    }

    fun GameTestDriver.inGraveyard(playerId: EntityId, entityId: EntityId): Boolean =
        state.getGraveyard(playerId).contains(entityId)

    test("anti-lord with lethal damage and a creature only lethal due to its -2/-2 both die (lord first)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = driver.activePlayer!!

        // Add the lord FIRST so it is iterated before the small creature — the ordering that
        // surfaced the bug. Grizzly Bears (2/2) stands in for the anti-lord.
        val lord = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.makeAntiLord(lord)
        // Hill Giant (3/3) is reduced to 1/1 by the lord's -2/-2.
        val small = driver.putCreatureOnBattlefield(active, "Hill Giant")

        // Sanity: the static is live before any deaths.
        projector.getProjectedToughness(driver.state, small) shouldBe 1
        projector.getProjectedToughness(driver.state, lord) shouldBe 2 // unaffected by own AllOtherCreatures

        // The lord has taken its own lethal damage; the small creature has 1 damage, which is
        // lethal ONLY because the lord shrank it to 1 toughness.
        driver.markDamage(lord, 2)
        driver.markDamage(small, 1)

        val result = LethalDamageCheck(driver.zones).check(driver.state)
        driver.replaceState(result.newState)

        // CR 704.3: both are determined lethal against the same (pre-event) board, so both die.
        driver.inGraveyard(active, lord) shouldBe true
        driver.inGraveyard(active, small) shouldBe true
        driver.state.getBattlefield().contains(lord) shouldBe false
        driver.state.getBattlefield().contains(small) shouldBe false
    }

    test("same outcome regardless of battlefield order (small creature first)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Forest" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = driver.activePlayer!!

        // Reverse insertion order to prove the result is order-independent.
        val small = driver.putCreatureOnBattlefield(active, "Hill Giant")
        val lord = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        driver.makeAntiLord(lord)

        projector.getProjectedToughness(driver.state, small) shouldBe 1

        driver.markDamage(lord, 2)
        driver.markDamage(small, 1)

        val result = LethalDamageCheck(driver.zones).check(driver.state)
        driver.replaceState(result.newState)

        driver.inGraveyard(active, lord) shouldBe true
        driver.inGraveyard(active, small) shouldBe true
    }
})
