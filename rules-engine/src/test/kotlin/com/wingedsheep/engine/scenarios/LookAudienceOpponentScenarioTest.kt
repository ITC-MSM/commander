package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.RevealedToComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.LookAudience
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Exercises [LookAudience.Opponent] on [GatherCardsEffect] — "an opponent looks at the top N
 * cards of your library." The non-public library look is persisted as revealed to the
 * opponent(s) and NOT to the controller, who never sees the cards. (The [LookAudience.None]
 * and default [LookAudience.Controller] branches are covered by SauronsRansomScenarioTest and
 * the Scry/Surveil tests respectively; this pins the remaining branch.)
 */
class LookAudienceOpponentScenarioTest : FunSpec({

    // {U} sorcery: an opponent looks at the top two cards of your library (no further effect).
    val opponentLooks = card("Opponent Looks") {
        manaCost = "{U}"
        colorIdentity = "U"
        typeLine = "Sorcery"
        oracleText = "An opponent looks at the top two cards of your library."
        spell {
            effect = GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(2)),
                storeAs = "looked",
                lookAudience = LookAudience.Opponent
            )
        }
    }

    fun GameTestDriver.revealedTo(card: EntityId, player: EntityId): Boolean =
        state.getEntity(card)?.get<RevealedToComponent>()?.isRevealedTo(player) == true

    test("an opponent looking at your top cards reveals them to the opponent, not to you") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(opponentLooks)
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val top1 = driver.putCardOnTopOfLibrary(active, "Island")
        val top2 = driver.putCardOnTopOfLibrary(active, "Forest")

        val spell = driver.putCardInHand(active, "Opponent Looks")
        driver.giveMana(active, Color.BLUE, 1)
        driver.castSpell(active, spell).outcome shouldBe Outcome.Done
        driver.bothPass()

        driver.isPaused shouldBe false

        // The opponent (the looker) privately sees both cards; the controller never does.
        driver.revealedTo(top1, opponent) shouldBe true
        driver.revealedTo(top2, opponent) shouldBe true
        driver.revealedTo(top1, active) shouldBe false
        driver.revealedTo(top2, active) shouldBe false
    }
})
