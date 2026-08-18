package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ody.cards.SkycloudEgg
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Skycloud Egg (ODY #309) — "{2}, {T}, Sacrifice this artifact: Add {W}{U}. Draw a card."
 *
 * The fixed-colour half of the CR 605.1a reclassification, and the shape the other four Odyssey
 * Eggs share. Chromatic Sphere covers the any-colour variant, where losing the mana-ability path
 * also means losing the activation-time colour choice; here there is no colour to ask about, so
 * what is left is the plain observable consequence of the August 7, 2026 update: the draw makes
 * this an ordinary activated ability, so the mana arrives on resolution rather than immediately.
 *
 * That is the whole difference, and nothing on the printed card records it — before the update the
 * ability resolved mid-payment and the drawn card stayed unseen until the spell being cast was
 * finished.
 */
class SkycloudEggScenarioTest : FunSpec({

    val abilityId = SkycloudEgg.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(SkycloudEgg)
        return driver
    }

    test("the mana waits for resolution, but the sacrifice is paid at activation") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val egg = driver.putPermanentOnBattlefield(you, "Skycloud Egg")
        driver.giveColorlessMana(you, 2)
        val handBefore = driver.getHand(you).size

        driver.submitSuccess(ActivateAbility(playerId = you, sourceId = egg, abilityId = abilityId))

        // CR 605.1a — the draw disqualifies it, so it uses the stack and can be responded to.
        driver.assertStackSize(1)
        driver.state.getEntity(you)?.get<ManaPoolComponent>()?.total shouldBe 0
        driver.getHand(you).size shouldBe handBefore

        // The cost is still paid on activation: the Egg is already in the graveyard while the
        // ability is on the stack.
        driver.assertInGraveyard(you, "Skycloud Egg")

        driver.bothPass()

        // Both halves land together on resolution — no decision, because the colours are printed.
        driver.assertStackSize(0)
        driver.pendingDecision shouldBe null
        val pool = driver.state.getEntity(you)?.get<ManaPoolComponent>()!!
        pool.white shouldBe 1
        pool.blue shouldBe 1
        pool.total shouldBe 2
        driver.getHand(you).size shouldBe handBefore + 1
    }
})
