package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.RevealedToComponent
import com.wingedsheep.engine.state.components.player.TheRingComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ltr.cards.SauronsRansom
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Sauron's Ransom: choose an opponent; they look at your top four and split them into a
 * face-up pile and a face-down pile; you put one pile into your hand and the other into your
 * graveyard; the Ring tempts you.
 *
 * The point of the card — and of the engine support behind it — is the *hidden information*:
 * the opponent looks (the caster does not), and only the face-up pile is shown to the caster
 * when they choose where each pile goes. These tests pin that visibility, expressed through the
 * [RevealedToComponent] the engine uses to decide what a player may see in a hidden zone.
 */
class SauronsRansomScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(SauronsRansom)
        return driver
    }

    fun GameTestDriver.revealedTo(card: EntityId, player: EntityId): Boolean =
        state.getEntity(card)?.get<RevealedToComponent>()?.isRevealedTo(player) == true

    test("opponent looks and splits; only the face-up pile is shown to the caster; piles route to hand and graveyard; Ring tempts") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack a known top four (top first as pushed last).
        val c1 = driver.putCardOnTopOfLibrary(active, "Island")
        val c2 = driver.putCardOnTopOfLibrary(active, "Forest")
        val c3 = driver.putCardOnTopOfLibrary(active, "Mountain")
        val c4 = driver.putCardOnTopOfLibrary(active, "Plains")
        // Library from top: c4, c3, c2, c1, <deck...>

        // A creature to designate as Ring-bearer when the Ring tempts.
        val bearer = driver.putCreatureOnBattlefield(active, "Grizzly Bears")

        val spell = driver.putCardInHand(active, "Sauron's Ransom")
        driver.giveMana(active, Color.BLUE, 2)
        driver.giveMana(active, Color.BLACK, 1)

        val handBefore = driver.getHandSize(active)
        val graveBefore = driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD)).size

        driver.castSpell(active, spell, targets = listOf(opponent)).isSuccess shouldBe true
        driver.bothPass()

        // 1. The opponent — not the caster — separates the top four. The opponent looks at them
        //    through this decision; the caster has not been shown any of the four.
        driver.isPaused shouldBe true
        val split = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        split.playerId shouldBe opponent
        split.options.size shouldBe 4
        // The opponent's decision carries the real identities so they can look at the cards.
        listOf(c1, c2, c3, c4).forEach { (split.cardInfo?.containsKey(it) == true) shouldBe true }
        // The caster is still blind to every looked-at card.
        listOf(c1, c2, c3, c4).forEach { driver.revealedTo(it, active) shouldBe false }

        // Opponent puts c4 and c3 face up; c2 and c1 stay face down.
        driver.submitDecision(opponent, CardsSelectedResponse(split.id, listOf(c4, c3)))

        // 2. The caster chooses which pile goes to hand vs graveyard. Now — and only now — the
        //    face-up pile (c4, c3) is visible to the caster; the face-down pile (c2, c1) is not.
        driver.isPaused shouldBe true
        val choose = driver.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        choose.playerId shouldBe active
        driver.revealedTo(c4, active) shouldBe true
        driver.revealedTo(c3, active) shouldBe true
        driver.revealedTo(c2, active) shouldBe false
        driver.revealedTo(c1, active) shouldBe false
        // The face-up pile is option 0; the caster keeps it (→ hand).
        choose.optionCardIds?.get(0) shouldBe listOf(c4, c3)
        choose.optionCardIds?.get(1) shouldBe listOf(c2, c1)
        driver.submitDecision(active, OptionChosenResponse(choose.id, 0))

        // 3. The Ring tempts you → choose a Ring-bearer.
        driver.isPaused shouldBe true
        val tempt = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        tempt.playerId shouldBe active
        driver.submitDecision(active, CardsSelectedResponse(tempt.id, listOf(bearer)))

        driver.isPaused shouldBe false

        // The kept (face-up) pile went to hand; the other (face-down) pile went to graveyard.
        val hand = driver.state.getZone(ZoneKey(active, Zone.HAND))
        hand.contains(c4) shouldBe true
        hand.contains(c3) shouldBe true

        val grave = driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD))
        grave.contains(c2) shouldBe true
        grave.contains(c1) shouldBe true

        // Hand: before - 1 (spell cast) + 2 (kept pile).
        driver.getHandSize(active) shouldBe handBefore - 1 + 2
        // Graveyard: 2 face-down cards + the Sauron's Ransom spell itself.
        driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD)).size shouldBe graveBefore + 2 + 1

        // The Ring tempted the controller exactly once.
        driver.state.getEntity(active)?.get<TheRingComponent>()?.temptCount shouldBe 1
    }

    test("opponent may keep the whole pile face down; the caster then sees neither pile and chooses by size") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Grizzly Bears" to 40), startingLife = 20)

        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val c1 = driver.putCardOnTopOfLibrary(active, "Island")
        val c2 = driver.putCardOnTopOfLibrary(active, "Forest")
        val c3 = driver.putCardOnTopOfLibrary(active, "Mountain")
        val c4 = driver.putCardOnTopOfLibrary(active, "Plains")

        val bearer = driver.putCreatureOnBattlefield(active, "Grizzly Bears")
        val spell = driver.putCardInHand(active, "Sauron's Ransom")
        driver.giveMana(active, Color.BLUE, 2)
        driver.giveMana(active, Color.BLACK, 1)

        driver.castSpell(active, spell, targets = listOf(opponent)).isSuccess shouldBe true
        driver.bothPass()

        // Opponent selects nothing → the whole face-down pile, an empty face-up pile (CR 700.3d).
        val split = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitDecision(opponent, CardsSelectedResponse(split.id, emptyList()))

        // Nothing was turned face up, so the caster sees none of the four when choosing.
        val choose = driver.pendingDecision.shouldBeInstanceOf<ChooseOptionDecision>()
        choose.playerId shouldBe active
        listOf(c1, c2, c3, c4).forEach { driver.revealedTo(it, active) shouldBe false }

        // Keep the (empty) face-up pile → nothing to hand; the four face-down cards go to graveyard.
        val faceUpOption = choose.optionCardIds?.entries?.first { it.value.isEmpty() }?.key ?: 0
        driver.submitDecision(active, OptionChosenResponse(choose.id, faceUpOption))

        val tempt = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitDecision(active, CardsSelectedResponse(tempt.id, listOf(bearer)))

        driver.isPaused shouldBe false
        val grave = driver.state.getZone(ZoneKey(active, Zone.GRAVEYARD))
        listOf(c1, c2, c3, c4).forEach { grave.contains(it) shouldBe true }
    }
})
