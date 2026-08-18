package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.ArcSlogger
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Arc-Slogger (MRD #85) — "{R}, Exile the top ten cards of your library: This creature deals 2
 * damage to any target."
 *
 * Ten cards off the top is a **cost**, and that is the whole test. CR 118.3 — a player can't pay a
 * cost without the resources to pay it fully — so a nine-card library can't activate this at all.
 * The tempting wrong implementation exiles as many as it can and fires anyway, which is what the
 * exile *effect* does and what a cost must never do; the shallow-library case below is there to
 * catch exactly that, from the enumerator's side as well as the handler's.
 *
 * The destination is the other half: exile, not the graveyard. A cost atom cloned from
 * [com.wingedsheep.sdk.scripting.costs.CostAtom.Mill] with only the count changed passes a damage
 * assertion and fails these zone assertions.
 */
class ArcSloggerScenarioTest : FunSpec({

    val sloggerAbility = ArcSlogger.activatedAbilities.single().id

    /** Player 1's precombat main with an empty library, ready to be stacked exactly. */
    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + ArcSlogger)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val library = ZoneKey(d.player1, Zone.LIBRARY)
        d.replaceState(d.state.copy(zones = d.state.zones + (library to emptyList())))
        return d
    }

    fun GameTestDriver.library(): List<EntityId> = state.getZone(ZoneKey(player1, Zone.LIBRARY))
    fun GameTestDriver.exile(): List<EntityId> = state.getZone(ZoneKey(player1, Zone.EXILE))
    fun GameTestDriver.graveyard(): List<EntityId> = state.getZone(ZoneKey(player1, Zone.GRAVEYARD))

    /** Stack [count] cards onto player 1's library, deepest first. */
    fun GameTestDriver.stackLibrary(count: Int): List<EntityId> =
        (1..count).map { putCardOnTopOfLibrary(player1, "Grizzly Bears") }

    test("activating exiles the top ten and deals 2") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.stackLibrary(12)
        val slogger = d.putCreatureOnBattlefield(d.player1, "Arc-Slogger")
        d.giveMana(d.player1, com.wingedsheep.sdk.core.Color.RED, 1)

        d.submit(
            ActivateAbility(d.player1, slogger, sloggerAbility, targets = listOf(ChosenTarget.Player(opponent)))
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("exactly ten leave the library") {
            d.library().size shouldBe 2
        }
        withClue("they go to exile, not the graveyard — this is not a mill cost") {
            d.exile().size shouldBe 10
            d.graveyard().size shouldBe 0
        }
        d.getLifeTotal(opponent) shouldBe 18
    }

    test("a library of exactly ten pays, and is emptied doing it") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.stackLibrary(10)
        val slogger = d.putCreatureOnBattlefield(d.player1, "Arc-Slogger")
        d.giveMana(d.player1, com.wingedsheep.sdk.core.Color.RED, 1)

        d.submit(
            ActivateAbility(d.player1, slogger, sloggerAbility, targets = listOf(ChosenTarget.Player(opponent)))
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("the cost is affordable down to the last card") {
            d.library().size shouldBe 0
            d.exile().size shouldBe 10
            d.getLifeTotal(opponent) shouldBe 18
        }
    }

    test("a nine-card library can't activate at all — it does not exile nine and fire") {
        // CR 118.3. The exile *effect* takes what it finds; a cost may not.
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.stackLibrary(9)
        val slogger = d.putCreatureOnBattlefield(d.player1, "Arc-Slogger")
        d.giveMana(d.player1, com.wingedsheep.sdk.core.Color.RED, 1)

        withClue("the enumerator must not offer an unpayable activation") {
            d.legalActions(d.player1).none { it.description.contains("Arc-Slogger") } shouldBe true
        }

        d.submitExpectFailure(
            ActivateAbility(d.player1, slogger, sloggerAbility, targets = listOf(ChosenTarget.Player(opponent)))
        )

        withClue("nothing was paid and nothing was dealt") {
            d.library().size shouldBe 9
            d.exile().size shouldBe 0
            d.getLifeTotal(opponent) shouldBe 20
        }
    }

    test("a second activation exiles ten more") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.stackLibrary(21)
        val slogger = d.putCreatureOnBattlefield(d.player1, "Arc-Slogger")
        d.giveMana(d.player1, com.wingedsheep.sdk.core.Color.RED, 2)

        repeat(2) {
            d.submit(
                ActivateAbility(d.player1, slogger, sloggerAbility, targets = listOf(ChosenTarget.Player(opponent)))
            ).isSuccess shouldBe true
            d.bothPass()
        }

        withClue("the cost is re-paid each time, not amortized") {
            d.library().size shouldBe 1
            d.exile().size shouldBe 20
            d.getLifeTotal(opponent) shouldBe 16
        }
    }
})
