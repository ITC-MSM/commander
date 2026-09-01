package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.SvogthosTheRestlessTomb
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Svogthos, the Restless Tomb (RAV #283) — "{3}{B}{G}: Until end of turn, this land becomes a black
 * and green Plant Zombie creature with 'This creature's power and toughness are each equal to the
 * number of creature cards in your graveyard.' It's still a land."
 *
 * The granted clause is a **characteristic-defining ability**, and that is the thing a test has to
 * prove: the 2005-10-01 ruling says the size "changes each time a creature card enters or leaves
 * its controller's graveyard", so a size frozen at resolution — the natural mistake, since
 * `power`/`toughness` are right there — reads correct on the turn it is used and is wrong forever
 * after. Hence the mid-turn re-read below. The other two claims are the ones a CDA can get subtly
 * wrong: it counts **creature** cards only (Svogthos in the yard is a land card), and it counts
 * **your** graveyard, not the table's.
 */
class SvogthosTheRestlessTombScenarioTest : FunSpec({

    val animateAbility = SvogthosTheRestlessTomb.activatedAbilities[1].id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + SvogthosTheRestlessTomb)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.animate(player: EntityId, land: EntityId) {
        giveColorlessMana(player, 3)
        giveMana(player, Color.BLACK, 1)
        giveMana(player, Color.GREEN, 1)
        submit(ActivateAbility(playerId = player, sourceId = land, abilityId = animateAbility))
            .isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && guard++ < 20) bothPass()
    }

    test("power and toughness track your graveyard's creature cards, live") {
        val d = driver()
        val me = d.activePlayer!!
        val svogthos = d.putLandOnBattlefield(me, "Svogthos, the Restless Tomb")
        repeat(2) { d.putCardInGraveyard(me, "Grizzly Bears") }

        d.animate(me, svogthos)

        val projected = d.state.projectedState
        projected.isCreature(svogthos) shouldBe true
        projected.getPower(svogthos) shouldBe 2
        projected.getToughness(svogthos) shouldBe 2
        withClue("\"It's still a land\", so it still taps for {C}") {
            projected.hasType(svogthos, "LAND") shouldBe true
        }
        withClue("black and green Plant Zombie") {
            projected.getColors(svogthos) shouldBe setOf(Color.BLACK.name, Color.GREEN.name)
            projected.hasSubtype(svogthos, "Plant") shouldBe true
            projected.hasSubtype(svogthos, "Zombie") shouldBe true
        }

        // A CDA, not a size frozen at resolution.
        d.putCardInGraveyard(me, "Grizzly Bears")
        d.state.projectedState.getPower(svogthos) shouldBe 3
    }

    test("noncreature cards in your graveyard, and creature cards in theirs, do not count") {
        val d = driver()
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)
        val svogthos = d.putLandOnBattlefield(me, "Svogthos, the Restless Tomb")

        d.putCardInGraveyard(me, "Grizzly Bears")
        d.putCardInGraveyard(me, "Swamp")
        repeat(3) { d.putCardInGraveyard(opponent, "Grizzly Bears") }

        d.animate(me, svogthos)

        d.state.projectedState.getPower(svogthos) shouldBe 1
    }

    test("the animation ends with the turn") {
        val d = driver()
        val me = d.activePlayer!!
        val svogthos = d.putLandOnBattlefield(me, "Svogthos, the Restless Tomb")
        d.putCardInGraveyard(me, "Grizzly Bears")

        d.animate(me, svogthos)
        d.state.projectedState.isCreature(svogthos) shouldBe true

        d.passPriorityUntil(Step.UPKEEP)
        withClue("\"Until end of turn\" — unlike Woodwraith Corrupter's open-ended animation") {
            d.state.projectedState.isCreature(svogthos) shouldBe false
        }
        withClue("it never stopped being a land") {
            d.state.projectedState.hasType(svogthos, "LAND") shouldBe true
        }
    }
})
