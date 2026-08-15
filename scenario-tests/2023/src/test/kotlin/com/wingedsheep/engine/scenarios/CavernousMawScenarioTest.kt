package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.CaptivatingCave
import com.wingedsheep.mtg.sets.definitions.lci.cards.CavernousMaw
import com.wingedsheep.mtg.sets.definitions.lci.cards.PromisingVein
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Cavernous Maw (LCI #270).
 *
 * Land — Cave
 *  {T}: Add {C}.
 *  {2}: This land becomes a 3/3 Elemental creature until end of turn. It's still a Cave land.
 *    Activate only if the number of other Caves you control plus the number of Cave cards in your
 *    graveyard is three or greater.
 *
 * Exercises the composite activation gate: (other Caves you control + Cave cards in your graveyard)
 * >= 3. Proves the "other" (exclude-self) semantics — the Maw itself is not counted — and that the
 * gate draws from both battlefield and graveyard. On a met gate it animates into a colorless 3/3
 * Elemental that is still a Land with the Cave subtype, reverting at end of turn.
 */
class CavernousMawScenarioTest : FunSpec({

    // activatedAbilities[0] = {T}: Add {C}; [1] = {2}: become 3/3 Elemental (gated)
    val animateAbilityId = CavernousMaw.activatedAbilities[1].id
    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(CavernousMaw, CaptivatingCave, PromisingVein))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        return driver
    }

    fun tryAnimate(driver: GameTestDriver, player: EntityId, maw: EntityId) {
        driver.giveColorlessMana(player, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = maw, abilityId = animateAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()
    }

    test("cannot animate with only two other Caves and no Cave cards in the graveyard") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val maw = driver.putLandOnBattlefield(player, "Cavernous Maw")
        // Three Caves on the battlefield counting the Maw, but only TWO *other* Caves.
        driver.putLandOnBattlefield(player, "Captivating Cave")
        driver.putLandOnBattlefield(player, "Promising Vein")

        driver.giveColorlessMana(player, 2)
        driver.submitExpectFailure(
            ActivateAbility(playerId = player, sourceId = maw, abilityId = animateAbilityId)
        )

        projector.project(driver.state).hasType(maw, "CREATURE") shouldBe false
    }

    test("animates into a 3/3 Elemental still a Cave land with three other Caves controlled") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val maw = driver.putLandOnBattlefield(player, "Cavernous Maw")
        driver.putLandOnBattlefield(player, "Captivating Cave")
        driver.putLandOnBattlefield(player, "Captivating Cave")
        driver.putLandOnBattlefield(player, "Promising Vein")

        tryAnimate(driver, player, maw)

        val projected = projector.project(driver.state)
        projected.hasType(maw, "CREATURE") shouldBe true
        projected.hasType(maw, "LAND") shouldBe true // it's still a land
        projected.hasSubtype(maw, "Cave") shouldBe true // still a Cave land
        projected.hasSubtype(maw, "Elemental") shouldBe true
        projected.getPower(maw) shouldBe 3
        projected.getToughness(maw) shouldBe 3
    }

    test("counts Cave cards in the graveyard toward the three-or-greater gate") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val maw = driver.putLandOnBattlefield(player, "Cavernous Maw")
        // One other Cave in play + two Cave cards in the graveyard = 3.
        driver.putLandOnBattlefield(player, "Captivating Cave")
        driver.putCardInGraveyard(player, "Captivating Cave")
        driver.putCardInGraveyard(player, "Promising Vein")

        tryAnimate(driver, player, maw)

        val projected = projector.project(driver.state)
        projected.hasType(maw, "CREATURE") shouldBe true
        projected.getPower(maw) shouldBe 3
        projected.getToughness(maw) shouldBe 3
    }

    test("reverts to a plain Cave land on the next turn") {
        val driver = createDriver()
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val maw = driver.putLandOnBattlefield(player, "Cavernous Maw")
        driver.putLandOnBattlefield(player, "Captivating Cave")
        driver.putLandOnBattlefield(player, "Captivating Cave")
        driver.putLandOnBattlefield(player, "Promising Vein")

        tryAnimate(driver, player, maw)
        projector.project(driver.state).hasType(maw, "CREATURE") shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        val next = projector.project(driver.state)
        next.hasType(maw, "CREATURE") shouldBe false
        next.hasType(maw, "LAND") shouldBe true
        next.hasSubtype(maw, "Cave") shouldBe true
    }
})
