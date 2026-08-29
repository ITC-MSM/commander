package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mh1.cards.ForceOfNegation
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Force of Negation {1}{U}{U} — Instant.
 *
 * "If it's not your turn, you may exile a blue card from your hand rather than pay this spell's
 *  mana cost.
 *  Counter target noncreature spell. If that spell is countered this way, exile it instead of
 *  putting it into its owner's graveyard."
 *
 * The blue Force: same off-turn-only pitch cost as [ForceOfVigorScenarioTest]'s green one, so the
 * cases worth proving separately are the two the counter half owns — the countered spell lands in
 * exile rather than its owner's graveyard, and a creature spell is not a legal target.
 */
class ForceOfNegationScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ForceOfNegation))
        return driver
    }

    fun altCastOffered(driver: GameTestDriver, player: EntityId, cardId: EntityId): Boolean =
        driver.legalActions(player).any { legal: LegalAction ->
            val action = legal.action
            action is CastSpell &&
                action.cardId == cardId &&
                action.useAlternativeCost &&
                action.alternativeCostType == AlternativeCostType.SELF_ALTERNATIVE
        }

    test("off your turn: pitching a blue card counters the spell and exiles it instead of milling it") {
        val driver = createDriver()
        // Opponent is active, so "you" acts off-turn.
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20, startingPlayer = 1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val opponent = driver.activePlayer!!
        val you = driver.getOpponent(opponent)

        // The opponent casts a noncreature spell at you.
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.castSpell(opponent, bolt, targets = listOf(you)).error shouldBe null
        driver.stackSize shouldBe 1
        // The caster keeps priority after putting a spell on the stack; pass it to "you".
        driver.passPriority(opponent)

        val blueFodder = driver.putCardInHand(you, "Counterspell")
        val force = driver.putCardInHand(you, "Force of Negation")
        altCastOffered(driver, you, force) shouldBe true

        driver.submit(
            CastSpell(
                playerId = you,
                cardId = force,
                targets = listOf(ChosenTarget.Spell(bolt)),
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.SELF_ALTERNATIVE,
                additionalCostPayment = AdditionalCostPayment(exiledCards = listOf(blueFodder))
            )
        ).error shouldBe null
        while (driver.stackSize > 0) driver.bothPass()

        // The bolt never resolved...
        driver.getLifeTotal(you) shouldBe 20
        // ...and it is in exile, not its owner's graveyard.
        driver.getExileCardNames(opponent) shouldBe listOf("Lightning Bolt")
        driver.getGraveyardCardNames(opponent) shouldBe emptyList()
        // The pitched blue card is exiled from your hand; Force of Negation itself still goes to
        // your graveyard normally.
        driver.getExileCardNames(you) shouldBe listOf("Counterspell")
        driver.getGraveyardCardNames(you) shouldBe listOf("Force of Negation")
    }

    test("on your own turn the alternative cost is not authorized, but the hard cast still works") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val blueFodder = driver.putCardInHand(you, "Counterspell")
        val force = driver.putCardInHand(you, "Force of Negation")
        altCastOffered(driver, you, force) shouldBe false

        driver.submit(
            CastSpell(
                playerId = you,
                cardId = force,
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.SELF_ALTERNATIVE,
                additionalCostPayment = AdditionalCostPayment(exiledCards = listOf(blueFodder))
            )
        ).error shouldBe
            "Alternative cost is not available: ${ForceOfNegation.script.selfAlternativeCost!!.condition!!.description}"

        // Hard-cast on your own turn: the opponent responds to your sorcery-speed play, and you
        // counter their instant with mana.
        val bolt = driver.putCardInHand(opponent, "Lightning Bolt")
        driver.giveMana(opponent, Color.RED, 1)
        driver.passPriority(you)
        driver.castSpell(opponent, bolt, targets = listOf(you)).error shouldBe null
        driver.passPriority(opponent)

        driver.giveMana(you, Color.BLUE, 3)
        driver.castSpellWithTargets(you, force, listOf(ChosenTarget.Spell(bolt))).error shouldBe null
        while (driver.stackSize > 0) driver.bothPass()

        driver.getLifeTotal(you) shouldBe 20
        driver.getExileCardNames(opponent) shouldBe listOf("Lightning Bolt")
    }

    test("a creature spell is not a legal target") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20, startingPlayer = 1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val opponent = driver.activePlayer!!
        val you = driver.getOpponent(opponent)

        val elves = driver.putCardInHand(opponent, "Llanowar Elves")
        driver.giveMana(opponent, Color.GREEN, 1)
        driver.castSpell(opponent, elves).error shouldBe null
        driver.stackSize shouldBe 1
        driver.passPriority(opponent)

        val blueFodder = driver.putCardInHand(you, "Counterspell")
        val force = driver.putCardInHand(you, "Force of Negation")

        driver.submit(
            CastSpell(
                playerId = you,
                cardId = force,
                targets = listOf(ChosenTarget.Spell(elves)),
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.SELF_ALTERNATIVE,
                additionalCostPayment = AdditionalCostPayment(exiledCards = listOf(blueFodder))
            )
        ).error shouldNotBe null
    }
})
