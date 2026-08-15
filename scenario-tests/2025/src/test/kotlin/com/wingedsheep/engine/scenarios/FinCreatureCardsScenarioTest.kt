package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.Cactuar
import com.wingedsheep.mtg.sets.definitions.fin.cards.Coeurl
import com.wingedsheep.mtg.sets.definitions.fin.cards.Gaelicat
import com.wingedsheep.mtg.sets.definitions.fin.cards.Gigantoad
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario coverage for the FIN "creatures" batch. Cactuar (intervening-if end-step self-bounce),
 * Coeurl (tap target nonenchantment creature via a composed filter), Gaelicat / Gigantoad
 * (conditional static P/T buffs). Hill Gigas reuses stock Trample/Haste/Mountaincycling and is
 * left to the snapshot net.
 */
class FinCreatureCardsScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(vararg cards: com.wingedsheep.sdk.model.CardDefinition): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        cards.forEach { driver.registerCard(it) }
        return driver
    }

    // ---------------------------------------------------------------------------------------------
    // Cactuar
    // ---------------------------------------------------------------------------------------------

    test("Cactuar stays at end step the turn it entered (intervening-if fails)") {
        val driver = createDriver(Cactuar)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)
        val active = driver.activePlayer!!

        val cactuar = driver.putCreatureOnBattlefield(active, "Cactuar")
        // Mark it as having entered this turn — the intervening-if must then NOT fire.
        driver.replaceState(
            driver.state.updateEntity(cactuar) {
                it.with(com.wingedsheep.engine.state.components.battlefield.EnteredThisTurnComponent)
            }
        )

        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.state.getBattlefield().contains(cactuar) shouldBe true
    }

    test("Cactuar returns to hand at end step when it didn't enter this turn") {
        val driver = createDriver(Cactuar)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)
        val active = driver.activePlayer!!

        // putCreatureOnBattlefield does not stamp EnteredThisTurnComponent, so Cactuar reads as
        // "didn't enter this turn" — the bounce trigger fires at the controller's end step.
        val cactuar = driver.putCreatureOnBattlefield(active, "Cactuar")
        val handBefore = driver.getHandSize(active)

        driver.passPriorityUntil(Step.END)
        driver.bothPass()

        driver.state.getBattlefield().contains(cactuar) shouldBe false
        driver.getHandSize(active) shouldBe handBefore + 1
    }

    // ---------------------------------------------------------------------------------------------
    // Coeurl
    // ---------------------------------------------------------------------------------------------

    test("Coeurl taps target nonenchantment creature") {
        val driver = createDriver(Coeurl)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        val coeurl = driver.putCreatureOnBattlefield(active, "Coeurl")
        driver.removeSummoningSickness(coeurl)
        val victim = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        driver.giveMana(active, Color.WHITE, 2)

        driver.isTapped(victim) shouldBe false

        val abilityId = Coeurl.activatedAbilities.first().id
        val result = driver.submit(
            ActivateAbility(
                playerId = active,
                sourceId = coeurl,
                abilityId = abilityId,
                targets = listOf(entityIdToChosenTarget(driver.state, victim)),
            )
        )
        result.isSuccess shouldBe true
        driver.bothPass()

        driver.isTapped(victim) shouldBe true
        // Coeurl itself is tapped as part of the {T} cost.
        driver.isTapped(coeurl) shouldBe true
    }

    // ---------------------------------------------------------------------------------------------
    // Gaelicat
    // ---------------------------------------------------------------------------------------------

    test("Gaelicat gets +2/+0 only while you control two or more artifacts") {
        val driver = createDriver(Gaelicat)
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingLife = 20)
        val active = driver.activePlayer!!

        val cat = driver.putCreatureOnBattlefield(active, "Gaelicat")

        // No artifacts: base 1/3.
        projector.getProjectedPower(driver.state, cat) shouldBe 1
        projector.getProjectedToughness(driver.state, cat) shouldBe 3

        // One artifact: still base (need two or more).
        driver.putPermanentOnBattlefield(active, "Ornithopter")
        projector.getProjectedPower(driver.state, cat) shouldBe 1

        // Two artifacts: +2/+0 → 3/3.
        driver.putPermanentOnBattlefield(active, "Ornithopter")
        projector.getProjectedPower(driver.state, cat) shouldBe 3
        projector.getProjectedToughness(driver.state, cat) shouldBe 3
    }

    // ---------------------------------------------------------------------------------------------
    // Gigantoad
    // ---------------------------------------------------------------------------------------------

    test("Gigantoad gets +2/+2 only while you control seven or more lands") {
        val driver = createDriver(Gigantoad)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)
        val active = driver.activePlayer!!

        val toad = driver.putCreatureOnBattlefield(active, "Gigantoad")

        // Six lands: base 4/4.
        repeat(6) { driver.putLandOnBattlefield(active, "Forest") }
        projector.getProjectedPower(driver.state, toad) shouldBe 4
        projector.getProjectedToughness(driver.state, toad) shouldBe 4

        // Seventh land: +2/+2 → 6/6.
        driver.putLandOnBattlefield(active, "Forest")
        projector.getProjectedPower(driver.state, toad) shouldBe 6
        projector.getProjectedToughness(driver.state, toad) shouldBe 6
    }
})
