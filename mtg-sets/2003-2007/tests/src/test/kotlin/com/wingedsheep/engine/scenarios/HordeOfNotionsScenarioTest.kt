package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.PlayWithoutPayingCostComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.HoofprintsOfTheStag
import com.wingedsheep.mtg.sets.definitions.lrw.cards.HordeOfNotions
import com.wingedsheep.mtg.sets.definitions.lrw.cards.Smokebraider
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Horde of Notions (LRW #249) — "{W}{U}{B}{R}{G}: You may play target Elemental card from your
 * graveyard without paying its mana cost."
 *
 * Two claims worth proving, both of which a snapshot golden is blind to.
 *
 * The first is that the free-cast permission is *readable in the graveyard at all*. The effect is
 * named for exile, and the natural reading of that name is that a graveyard target would be
 * granted a permission nothing ever consults — a card that looks right and does nothing. So the
 * test does not stop at the component stamp: it casts the Elemental out of the graveyard with an
 * empty mana pool.
 *
 * The second is the bare tribal noun. "Elemental card" is any card with the subtype, so a Kindred
 * noncreature Elemental — Hoofprints of the Stag — is a legal target; narrowing the filter to
 * creature cards would silently drop it, and every creature-only test would still pass.
 */
class HordeOfNotionsScenarioTest : FunSpec({

    val hordeAbility = HordeOfNotions.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(HordeOfNotions, Smokebraider, HoofprintsOfTheStag))
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Exactly the ability's cost and not a mana more, so a free cast can't be paid for by accident. */
    fun GameTestDriver.giveWubrg() {
        listOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN)
            .forEach { giveMana(player1, it, 1) }
    }

    test("the targeted Elemental becomes castable from the graveyard with no mana at all") {
        val d = driver()
        val horde = d.putCreatureOnBattlefield(d.player1, "Horde of Notions")
        val smokebraider = d.putCardInGraveyard(d.player1, "Smokebraider")
        d.giveWubrg()

        d.submit(
            ActivateAbility(
                d.player1, horde, hordeAbility,
                targets = listOf(ChosenTarget.Card(smokebraider, d.player1, Zone.GRAVEYARD))
            )
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("the permission and the free-cast stamp both land on the graveyard card") {
            d.state.mayPlayPermissions.firstOrNull { smokebraider in it.cardIds } shouldNotBe null
            d.state.getEntity(smokebraider)?.get<PlayWithoutPayingCostComponent>() shouldNotBe null
        }

        // The WUBRG paid for the ability, so the pool is empty — an affordable cast here can only
        // be the free one.
        val castAction = d.legalActions(d.player1).singleOrNull {
            it.action is CastSpell && (it.action as CastSpell).cardId == smokebraider
        }
        withClue("the graveyard card is offered as a castable spell") {
            castAction shouldNotBe null
            castAction!!.sourceZone shouldBe "GRAVEYARD"
            castAction.affordable shouldBe true
        }

        d.submit(castAction!!.action).isSuccess shouldBe true
        d.bothPass()

        d.findPermanent(d.player1, "Smokebraider") shouldNotBe null
        d.getGraveyardCardNames(d.player1) shouldBe emptyList()
    }

    test("a Kindred noncreature Elemental is a legal target — 'Elemental card' is the bare noun") {
        val d = driver()
        val horde = d.putCreatureOnBattlefield(d.player1, "Horde of Notions")
        val hoofprints = d.putCardInGraveyard(d.player1, "Hoofprints of the Stag")
        d.giveWubrg()

        d.submit(
            ActivateAbility(
                d.player1, horde, hordeAbility,
                targets = listOf(ChosenTarget.Card(hoofprints, d.player1, Zone.GRAVEYARD))
            )
        ).isSuccess shouldBe true
        d.bothPass()

        d.state.getEntity(hoofprints)?.get<PlayWithoutPayingCostComponent>() shouldNotBe null

        val castAction = d.legalActions(d.player1).singleOrNull {
            it.action is CastSpell && (it.action as CastSpell).cardId == hoofprints
        }
        castAction shouldNotBe null
        castAction!!.affordable shouldBe true
        d.submit(castAction.action).isSuccess shouldBe true
        d.bothPass()

        d.findPermanent(d.player1, "Hoofprints of the Stag") shouldNotBe null
    }

    test("a non-Elemental card in the graveyard is not a legal target") {
        val d = driver()
        val horde = d.putCreatureOnBattlefield(d.player1, "Horde of Notions")
        val bears = d.putCardInGraveyard(d.player1, "Grizzly Bears")
        d.giveWubrg()

        d.submitExpectFailure(
            ActivateAbility(
                d.player1, horde, hordeAbility,
                targets = listOf(ChosenTarget.Card(bears, d.player1, Zone.GRAVEYARD))
            )
        )

        d.state.getEntity(bears)?.get<PlayWithoutPayingCostComponent>() shouldBe null
    }
})
