package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mh1.cards.ForceOfVigor
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Force of Vigor {2}{G}{G} — Instant.
 *
 * "If it's not your turn, you may exile a green card from your hand rather than pay this spell's
 *  mana cost.
 *  Destroy up to two target artifacts and/or enchantments."
 *
 * The Force cycle's alternative cost is only legal off-turn (mirrors Blasphemous Edict's
 * condition-gated `SelfAlternativeCost`, here gated by `Conditions.IsNotYourTurn` instead of a
 * board-state count). The destroy half is "up to two" — an optional multi-target requirement, so
 * zero or one chosen target must resolve without fizzling, same as Rack and Ruin's mandatory
 * two-target shape widened with `optional = true`.
 */
class ForceOfVigorScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ForceOfVigor))
        return driver
    }

    /** True when the enumerator is currently offering the self-alternative cast of [cardId]. */
    fun altCastOffered(driver: GameTestDriver, player: EntityId, cardId: EntityId): Boolean =
        driver.legalActions(player).any { legal: LegalAction ->
            val action = legal.action
            action is CastSpell &&
                action.cardId == cardId &&
                action.useAlternativeCost &&
                action.alternativeCostType == AlternativeCostType.SELF_ALTERNATIVE
        }

    fun altCastAction(driver: GameTestDriver, player: EntityId, cardId: EntityId): LegalAction =
        driver.legalActions(player).first { legal ->
            val action = legal.action
            action is CastSpell && action.cardId == cardId &&
                action.alternativeCostType == AlternativeCostType.SELF_ALTERNATIVE
        }

    test("on your own turn: the alternative cost is not offered, and paying full mana cost destroys the chosen targets") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val artifact = driver.putPermanentOnBattlefield(opponent, "Artifact Creature")
        val enchantment = driver.putPermanentOnBattlefield(opponent, "Test Enchantment")

        driver.giveMana(you, Color.GREEN, 4)
        val spell = driver.putCardInHand(you, "Force of Vigor")

        altCastOffered(driver, you, spell) shouldBe false

        driver.castSpell(you, spell, targets = listOf(artifact, enchantment)).error shouldBe null
        while (driver.stackSize > 0) driver.bothPass()

        driver.state.getBattlefield(opponent).contains(artifact) shouldBe false
        driver.state.getBattlefield(opponent).contains(enchantment) shouldBe false
        driver.getGraveyardCardNames(opponent).sorted() shouldBe listOf("Artifact Creature", "Test Enchantment")
    }

    test("on your own turn: the alternative cost is not authorized even if attempted directly") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        val greenFodder = driver.putCardInHand(you, "Llanowar Elves")
        val spell = driver.putCardInHand(you, "Force of Vigor")

        driver.submit(
            CastSpell(
                playerId = you,
                cardId = spell,
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.SELF_ALTERNATIVE,
                additionalCostPayment = AdditionalCostPayment(exiledCards = listOf(greenFodder))
            )
        ).error shouldBe "Alternative cost is not available: ${ForceOfVigor.script.selfAlternativeCost!!.condition!!.description}"
    }

    test("off your turn: exiling a green card from hand pays for the spell and destroys the chosen targets") {
        val driver = createDriver()
        // Opponent is active, so "you" acts off-turn once the opponent passes priority.
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20, startingPlayer = 1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val opponentTurn = driver.activePlayer!!
        val you = driver.getOpponent(opponentTurn)
        driver.passPriority(opponentTurn)

        val artifact = driver.putPermanentOnBattlefield(opponentTurn, "Artifact Creature")
        val greenFodder = driver.putCardInHand(you, "Llanowar Elves")
        val spell = driver.putCardInHand(you, "Force of Vigor")

        altCastOffered(driver, you, spell) shouldBe true
        val cost = altCastAction(driver, you, spell).additionalCostInfo!!
        cost.costType shouldBe "ExileFromHand"
        cost.validExileTargets shouldBe listOf(greenFodder)
        cost.exileMinCount shouldBe 1
        cost.exileMaxCount shouldBe 1

        driver.submit(
            CastSpell(
                playerId = you,
                cardId = spell,
                targets = listOf(ChosenTarget.Permanent(artifact)),
                useAlternativeCost = true,
                alternativeCostType = AlternativeCostType.SELF_ALTERNATIVE,
                additionalCostPayment = AdditionalCostPayment(exiledCards = listOf(greenFodder))
            )
        ).error shouldBe null
        while (driver.stackSize > 0) driver.bothPass()

        // No mana was ever given to "you" — the spell resolved for free.
        driver.getExileCardNames(you) shouldBe listOf("Llanowar Elves")
        driver.state.getBattlefield(opponentTurn).contains(artifact) shouldBe false
    }

    test("off your turn: the alternative cost is not offered without another green card in hand") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20, startingPlayer = 1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val opponentTurn = driver.activePlayer!!
        val you = driver.getOpponent(opponentTurn)
        driver.passPriority(opponentTurn)

        driver.putCardInHand(you, "Artifact Creature")
        val spell = driver.putCardInHand(you, "Force of Vigor")

        altCastOffered(driver, you, spell) shouldBe false
    }

    test("up to two targets: choosing only one, or none, still resolves without fizzling") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.getOpponent(you)

        val artifact = driver.putPermanentOnBattlefield(opponent, "Artifact Creature")

        driver.giveMana(you, Color.GREEN, 4)
        val spell = driver.putCardInHand(you, "Force of Vigor")

        // Only one legal target on the battlefield — the requirement is "up to two", not "exactly two".
        driver.castSpell(you, spell, targets = listOf(artifact)).error shouldBe null
        while (driver.stackSize > 0) driver.bothPass()

        driver.state.getBattlefield(opponent).contains(artifact) shouldBe false

        // Choosing zero targets is legal too — the spell still resolves.
        driver.giveMana(you, Color.GREEN, 4)
        val secondCast = driver.putCardInHand(you, "Force of Vigor")
        driver.castSpell(you, secondCast, targets = emptyList()).error shouldBe null
        while (driver.stackSize > 0) driver.bothPass()

        driver.getGraveyardCardNames(you).count { it == "Force of Vigor" } shouldBe 2
    }
})
