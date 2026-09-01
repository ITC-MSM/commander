package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.PrimalCommand
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Primal Command (LRW #233) — {3}{G}{G} Sorcery.
 *
 * Choose two —
 * • Target player gains 7 life.
 * • Put target noncreature permanent on top of its owner's library.
 * • Target player shuffles their graveyard into their library.
 * • Search your library for a creature card, reveal it, put it into your hand, then shuffle.
 *
 * Three of the four modes take a *player* or *owner* reference that can plausibly resolve to the
 * caster instead of the chosen target, and every one of those mistakes reads correctly on the card.
 * So each is aimed at the **opponent** — the one direction where "target player" and an implicit
 * "you" give opposite answers: the opponent gains the life, the opponent's graveyard is the one
 * that shuffles away, and the tucked permanent goes to *its owner's* library.
 *
 * The tuck mode is also a fail-open axis on its filter: a requirement that lost `noncreature`
 * would happily take a creature, so the creature is offered and expected to be refused.
 */
class PrimalCommandScenarioTest : FunSpec({

    // Mode order follows the printed bullets.
    val gainLife = 0
    val tuck = 1
    val shuffleGraveyard = 2
    val searchCreature = 3

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(PrimalCommand))
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.castCommand(
        caster: EntityId,
        modes: List<Int>,
        modeTargets: List<List<ChosenTarget>>,
    ): ExecutionResult {
        giveMana(caster, Color.GREEN, 5)
        val spell = putCardInHand(caster, "Primal Command")
        return submit(
            CastSpell(
                playerId = caster,
                cardId = spell,
                targets = modeTargets.flatten(),
                chosenModes = modes,
                modeTargetsOrdered = modeTargets,
            )
        )
    }

    test("the life goes to the chosen player, not automatically to the caster") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.castCommand(
            me,
            modes = listOf(gainLife, shuffleGraveyard),
            modeTargets = listOf(
                listOf(ChosenTarget.Player(opp)),
                listOf(ChosenTarget.Player(opp)),
            ),
        ).error shouldBe null
        driver.bothPass()

        driver.getLifeTotal(opp) shouldBe 27
        driver.getLifeTotal(me) shouldBe 20
    }

    test("the graveyard shuffle empties the chosen player's graveyard, not the caster's") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        driver.putCardInGraveyard(opp, "Grizzly Bears")
        driver.putCardInGraveyard(opp, "Savannah Lions")
        driver.putCardInGraveyard(me, "Centaur Courser")

        driver.castCommand(
            me,
            modes = listOf(shuffleGraveyard, gainLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Player(opp)),
                listOf(ChosenTarget.Player(me)),
            ),
        ).error shouldBe null
        driver.bothPass()

        driver.getGraveyardCardNames(opp) shouldBe emptyList()
        // The caster's graveyard is untouched — and Primal Command itself lands in it afterwards,
        // which is the 2017-03-14 ruling about targeting yourself with this mode.
        driver.getGraveyardCardNames(me) shouldBe listOf("Centaur Courser", "Primal Command")
    }

    test("the spell itself is not shuffled away when the caster targets their own graveyard") {
        val driver = newDriver()
        val me = driver.activePlayer!!

        driver.putCardInGraveyard(me, "Grizzly Bears")

        driver.castCommand(
            me,
            modes = listOf(shuffleGraveyard, gainLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Player(me)),
                listOf(ChosenTarget.Player(me)),
            ),
        ).error shouldBe null
        driver.bothPass()

        // A sorcery is put into its owner's graveyard only as the final part of its own
        // resolution, so Primal Command is still on the stack while the shuffle runs and is not
        // among the cards shuffled in — it lands in the graveyard afterwards.
        driver.getGraveyardCardNames(me) shouldBe listOf("Primal Command")
    }

    test("a noncreature permanent is tucked onto its owner's library") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val enchantment = driver.putPermanentOnBattlefield(opp, "Test Enchantment")

        driver.castCommand(
            me,
            modes = listOf(tuck, gainLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(enchantment)),
                listOf(ChosenTarget.Player(me)),
            ),
        ).error shouldBe null
        driver.bothPass()

        driver.findPermanent(opp, "Test Enchantment") shouldBe null
        driver.getGraveyardCardNames(opp).contains("Test Enchantment") shouldBe false
    }

    test("the tuck mode refuses a creature") {
        val driver = newDriver()
        val me = driver.activePlayer!!
        val opp = driver.getOpponent(me)

        val bears = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")

        driver.castCommand(
            me,
            modes = listOf(tuck, gainLife),
            modeTargets = listOf(
                listOf(ChosenTarget.Permanent(bears)),
                listOf(ChosenTarget.Player(me)),
            ),
        ).error shouldNotBe null

        driver.findPermanent(opp, "Grizzly Bears") shouldBe bears
    }
})
