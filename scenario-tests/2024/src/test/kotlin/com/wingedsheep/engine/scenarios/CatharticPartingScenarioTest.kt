package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.CatharticParting
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain

/**
 * Cathartic Parting (DSK #171) — {1}{G} Sorcery.
 *
 * "The owner of target artifact or enchantment an opponent controls shuffles it into their library.
 *  You may shuffle up to four target cards from your graveyard into your library."
 *
 * Verifies the opponent's enchantment leaves the battlefield (into the opponent's library) and the
 * chosen graveyard cards leave your graveyard (into your library). The second clause is optional /
 * "up to four", so we also confirm choosing fewer than four works.
 */
class CatharticPartingScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + CatharticParting)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("shuffles the opponent's enchantment away and selected graveyard cards back") {
        val d = driver()
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Opponent controls an enchantment; you have two cards in your graveyard.
        val enchantment = d.putPermanentOnBattlefield(opponent, "Test Enchantment")
        val gy1 = d.putCardInGraveyard(you, "Forest")
        val gy2 = d.putCardInGraveyard(you, "Forest")

        val parting = d.putCardInHand(you, "Cathartic Parting")
        d.giveMana(you, Color.GREEN, 2)

        // Target the enchantment + two graveyard cards (choosing 2 of the allowed up-to-4).
        d.castSpellWithTargets(
            you,
            parting,
            listOf(
                ChosenTarget.Permanent(enchantment),
                ChosenTarget.Card(gy1, you, Zone.GRAVEYARD),
                ChosenTarget.Card(gy2, you, Zone.GRAVEYARD),
            )
        ).error shouldBe null
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // The enchantment is no longer on the battlefield and now lives in the opponent's library.
        d.getPermanents(opponent) shouldNotContain enchantment
        d.state.zones[com.wingedsheep.engine.state.ZoneKey(opponent, Zone.LIBRARY)]!! shouldContain enchantment

        // Both chosen graveyard cards left the graveyard back into your library.
        val gy = d.getGraveyard(you)
        gy shouldNotContain gy1
        gy shouldNotContain gy2
        val yourLibrary = d.state.zones[com.wingedsheep.engine.state.ZoneKey(you, Zone.LIBRARY)]!!
        yourLibrary.toSet().intersect(setOf(gy1, gy2)) shouldContainExactlyInAnyOrder setOf(gy1, gy2)
    }

    test("graveyard clause is optional — only the opponent's permanent is required") {
        val d = driver()
        val you = d.activePlayer!!
        val opponent = d.getOpponent(you)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val enchantment = d.putPermanentOnBattlefield(opponent, "Test Enchantment")
        val parting = d.putCardInHand(you, "Cathartic Parting")
        d.giveMana(you, Color.GREEN, 2)

        // No graveyard targets at all (the "you may" clause declined).
        d.castSpellWithTargets(you, parting, listOf(ChosenTarget.Permanent(enchantment))).error shouldBe null
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.getPermanents(opponent) shouldNotContain enchantment
        d.state.zones[com.wingedsheep.engine.state.ZoneKey(opponent, Zone.LIBRARY)]!! shouldContain enchantment
    }
})
