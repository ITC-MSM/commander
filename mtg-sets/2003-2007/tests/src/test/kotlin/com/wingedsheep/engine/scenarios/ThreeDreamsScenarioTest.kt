package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.ClingingDarkness
import com.wingedsheep.mtg.sets.definitions.rav.cards.ConclavesBlessing
import com.wingedsheep.mtg.sets.definitions.rav.cards.ThreeDreams
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Three Dreams — {4}{W} Sorcery (Ravnica: City of Guilds #32)
 *
 * "Search your library for up to three Aura cards with different names, reveal them, put them
 *  into your hand, then shuffle."
 *
 * The pipeline is gather → `ChooseUpTo(3)` under `SelectionRestriction.OnePerCardName` → move to
 * hand. "With different names" is the whole point of the restriction, and it is enforced
 * server-side, so the test asks for two copies of one Aura and checks only one comes back.
 */
class ThreeDreamsScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + ThreeDreams + ClingingDarkness + ConclavesBlessing)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.handNames() =
        state.getHand(player1).mapNotNull { state.getEntity(it)?.get<CardComponent>()?.name }

    test("finds Auras with different names and refuses a second copy of one") {
        val d = driver()
        val darkness1 = d.putCardOnTopOfLibrary(d.player1, "Clinging Darkness")
        val darkness2 = d.putCardOnTopOfLibrary(d.player1, "Clinging Darkness")
        val blessing = d.putCardOnTopOfLibrary(d.player1, "Conclave's Blessing")

        val threeDreams = d.putCardInHand(d.player1, "Three Dreams")
        d.giveMana(d.player1, Color.WHITE, 1)
        d.giveColorlessMana(d.player1, 4)
        d.castSpell(d.player1, threeDreams).error shouldBe null
        d.bothPass()

        withClue("\"with different names\" caps the search at the number of distinct names on offer") {
            d.submitCardSelection(d.player1, listOf(darkness1, darkness2, blessing))
                .error shouldBe "Too many cards selected: maximum is 2"
        }

        d.submitCardSelection(d.player1, listOf(darkness1, blessing)).error shouldBe null

        withClue("one of each name reaches hand") {
            d.handNames().count { it == "Clinging Darkness" } shouldBe 1
            d.handNames().count { it == "Conclave's Blessing" } shouldBe 1
        }
        withClue("the second copy stays in the library") {
            (darkness2 in d.state.getLibrary(d.player1)) shouldBe true
        }
    }

    test("\"up to three\" allows finding nothing") {
        val d = driver()
        val threeDreams = d.putCardInHand(d.player1, "Three Dreams")
        val handBefore = d.getHandSize(d.player1)

        d.giveMana(d.player1, Color.WHITE, 1)
        d.giveColorlessMana(d.player1, 4)
        d.castSpell(d.player1, threeDreams).error shouldBe null
        d.bothPass()

        withClue("an all-Plains library holds no Auras, so the spell simply finds nothing") {
            d.getHandSize(d.player1) shouldBe handBefore - 1
        }
    }
})
