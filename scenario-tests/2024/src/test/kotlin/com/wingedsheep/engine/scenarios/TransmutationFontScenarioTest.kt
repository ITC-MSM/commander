package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.big.cards.TransmutationFont
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Transmutation Font — {5} Artifact (BIG #28).
 *
 * "{T}: Create your choice of a Blood token, a Clue token, or a Food token.
 *  {3}, {T}, Sacrifice three artifact tokens with different names: Search your library for an
 *  artifact card, put it onto the battlefield, then shuffle. Activate only as a sorcery."
 */
class TransmutationFontScenarioTest : FunSpec({

    val createTokenAbilityId = TransmutationFont.activatedAbilities[0].id
    val searchAbilityId = TransmutationFont.activatedAbilities[1].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + PredefinedTokens.allTokens + listOf(TransmutationFont))
        return driver
    }

    fun GameTestDriver.tokensNamed(playerId: EntityId, name: String): List<EntityId> =
        state.getZone(playerId, Zone.BATTLEFIELD).filter {
            state.getEntity(it)?.get<CardComponent>()?.name == name
        }

    test("{T} modal: create your choice of a Blood, Clue, or Food token") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val font = driver.putPermanentOnBattlefield(me, "Transmutation Font")

        driver.submit(ActivateAbility(playerId = me, sourceId = font, abilityId = createTokenAbilityId))
            .isSuccess shouldBe true
        driver.bothPass() // resolve → mode choice

        val mode = driver.pendingDecision as ChooseOptionDecision
        driver.submitDecision(me, OptionChosenResponse(mode.id, 0)) // Blood

        driver.tokensNamed(me, "Blood").size shouldBe 1
    }

    test("{3},{T}, sacrifice three artifact tokens with different names: tutor an artifact onto the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val font = driver.putPermanentOnBattlefield(me, "Transmutation Font")

        // Three artifact tokens with different names, created via the {T} ability (untapping between).
        fun createToken(modeIndex: Int) {
            driver.untapPermanent(font)
            driver.submit(ActivateAbility(playerId = me, sourceId = font, abilityId = createTokenAbilityId))
            driver.bothPass()
            val mode = driver.pendingDecision as ChooseOptionDecision
            driver.submitDecision(me, OptionChosenResponse(mode.id, modeIndex))
        }
        createToken(0) // Blood
        createToken(1) // Clue
        createToken(2) // Food

        val blood = driver.tokensNamed(me, "Blood")
        val clue = driver.tokensNamed(me, "Clue")
        val food = driver.tokensNamed(me, "Food")
        (blood.size to clue.size to food.size) shouldBe ((1 to 1) to 1)

        // Put an artifact card to find in the library.
        val target = driver.putCardOnTopOfLibrary(me, "Palladium Myr")

        driver.untapPermanent(font)
        driver.giveColorlessMana(me, 3)
        // "With different names" is always a real choice, so the activation pauses to let the
        // player pick the distinctly-named set to sacrifice.
        driver.submit(ActivateAbility(playerId = me, sourceId = font, abilityId = searchAbilityId))
            .isPaused shouldBe true

        // Pay the sacrifice cost: pick the three distinctly-named tokens.
        driver.submitCardSelection(me, blood + clue + food)
        driver.bothPass() // resolve the search

        // The library search prompts a selection of the artifact card.
        if (driver.pendingDecision != null) driver.submitCardSelection(me, listOf(target))
        driver.bothPass()

        driver.state.getZone(me, Zone.BATTLEFIELD).contains(target) shouldBe true
        // The three tokens were sacrificed.
        driver.tokensNamed(me, "Blood").size shouldBe 0
    }

    test("cannot pay the sacrifice cost with three same-named tokens (different names required)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val font = driver.putPermanentOnBattlefield(me, "Transmutation Font")

        // Three Food tokens (same name) — not "different names".
        repeat(3) {
            driver.untapPermanent(font)
            driver.submit(ActivateAbility(playerId = me, sourceId = font, abilityId = createTokenAbilityId))
            driver.bothPass()
            val mode = driver.pendingDecision as ChooseOptionDecision
            driver.submitDecision(me, OptionChosenResponse(mode.id, 2)) // Food each time
        }
        driver.tokensNamed(me, "Food").size shouldBe 3

        driver.untapPermanent(font)
        driver.giveColorlessMana(me, 3)
        val result = driver.submit(ActivateAbility(playerId = me, sourceId = font, abilityId = searchAbilityId))

        // The cost is unpayable (no three distinctly-named artifact tokens), so the ability is illegal.
        result.isSuccess shouldBe false
    }
})
